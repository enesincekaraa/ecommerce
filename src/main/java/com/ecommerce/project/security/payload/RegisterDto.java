package com.ecommerce.project.security.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NotBlank(message = "Username not blank")
    private String userName;

    @NotBlank(message = "Email not blank")
    @Email
    private String email;

    @NotBlank(message = "Password not blank")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;

    private Set<String> roles;
}
