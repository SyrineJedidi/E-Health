package com.ehealth.auth.controller;

import com.ehealth.auth.dto.AuthResponse;
import com.ehealth.auth.dto.ChangePasswordRequest;
import com.ehealth.auth.dto.ForgotPasswordRequest;
import com.ehealth.auth.dto.ForgotPasswordResponse;
import com.ehealth.auth.dto.LoginRequest;
import com.ehealth.auth.dto.RegisterRequest;
import com.ehealth.auth.dto.ResetPasswordRequest;
import com.ehealth.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication));
    }

    /** Supprime le compte lié au JWT (permet de refaire un POST /register avec le même email). */
    @DeleteMapping("/account")
    public ResponseEntity<Map<String, String>> deleteAccount(Authentication authentication) {
        authService.deleteMyAccount(authentication);
        return ResponseEntity.ok(Map.of("message", "Compte supprimé. Vous pouvez vous réinscrire avec le même email."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour. Vous pouvez vous connecter."));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication, request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès."));
    }
}
