package com.ehealth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponse {

    private String message;

    /**
     * Renseigné uniquement si {@code ehealth.auth.expose-reset-token=true} (ex. développement sans SMTP).
     */
    private String resetToken;
}
