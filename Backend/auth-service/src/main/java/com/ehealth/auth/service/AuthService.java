package com.ehealth.auth.service;

import com.ehealth.auth.dto.AuthResponse;
import com.ehealth.auth.dto.ChangePasswordRequest;
import com.ehealth.auth.dto.ForgotPasswordRequest;
import com.ehealth.auth.dto.ForgotPasswordResponse;
import com.ehealth.auth.dto.LoginRequest;
import com.ehealth.auth.dto.RegisterRequest;
import com.ehealth.auth.dto.ResetPasswordRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse me(Authentication authentication);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(Authentication authentication, ChangePasswordRequest request);

    /** Supprime le compte de l’utilisateur authentifié (réinscription possible ensuite). */
    void deleteMyAccount(Authentication authentication);
}
