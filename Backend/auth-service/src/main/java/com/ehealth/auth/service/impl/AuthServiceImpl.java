package com.ehealth.auth.service.impl;

import com.ehealth.auth.dto.AuthResponse;
import com.ehealth.auth.dto.ChangePasswordRequest;
import com.ehealth.auth.dto.ForgotPasswordRequest;
import com.ehealth.auth.dto.ForgotPasswordResponse;
import com.ehealth.auth.dto.LoginRequest;
import com.ehealth.auth.dto.RegisterRequest;
import com.ehealth.auth.dto.ResetPasswordRequest;
import com.ehealth.auth.exception.InvalidResetTokenException;
import com.ehealth.auth.exception.UserAlreadyExistsException;
import com.ehealth.auth.exception.WrongPasswordException;
import com.ehealth.auth.model.User;
import com.ehealth.auth.repository.UserRepository;
import com.ehealth.auth.security.JwtService;
import com.ehealth.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${ehealth.auth.expose-reset-token:false}")
    private boolean exposeResetToken;

    /**
     * Pas de @Transactional ici : sur MongoDB standalone (sans replica set), Spring tente une
     * transaction multi-documents et lève une erreur → 500 à l’inscription.
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Un compte existe déjà avec cet email");
        }
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();
        user = userRepository.save(user);
        String token = jwtService.generateToken(user);
        return toAuthResponse(user, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable après authentification"));
        String token = jwtService.generateToken(user);
        return toAuthResponse(user, token);
    }

    @Override
    public AuthResponse me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return toAuthResponse(user, null);
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String neutralMessage =
                "Si un compte est associé à cet email, une procédure de réinitialisation a été enregistrée.";
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.info("Demande de réinitialisation pour email inconnu (réponse neutre) : {}", email);
            return ForgotPasswordResponse.builder().message(neutralMessage).resetToken(null).build();
        }
        User user = userOpt.get();
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(Instant.now().plus(1, ChronoUnit.HOURS));
        userRepository.save(user);
        log.info(
                "Jeton de réinitialisation créé pour {} — valable 1 h (configurez un envoi email en production).",
                email);
        return ForgotPasswordResponse.builder()
                .message(neutralMessage)
                .resetToken(exposeResetToken ? token : null)
                .build();
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken().trim();
        User user = userRepository
                .findByPasswordResetToken(token)
                .orElseThrow(() -> new InvalidResetTokenException("Lien ou jeton invalide ou expiré."));
        if (user.getPasswordResetExpires() == null || Instant.now().isAfter(user.getPasswordResetExpires())) {
            user.setPasswordResetToken(null);
            user.setPasswordResetExpires(null);
            userRepository.save(user);
            throw new InvalidResetTokenException("Lien ou jeton invalide ou expiré.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Authentication authentication, ChangePasswordRequest request) {
        User user = (User) authentication.getPrincipal();
        User fresh = userRepository
                .findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), fresh.getPassword())) {
            throw new WrongPasswordException("Le mot de passe actuel est incorrect.");
        }
        fresh.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(fresh);
    }

    private static AuthResponse toAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType(token != null ? "Bearer" : null)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }
}
