package com.kadalVazhi.nn_home_service.user.dto;

import com.kadalVazhi.nn_home_service.user.domain.Language;
import com.kadalVazhi.nn_home_service.user.domain.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for user onboarding and registration.
 *
 * Validations:
 * 1. Phone number must be 10-15 digits.
 * 2. Password minimum 6 characters.
 * 3. Full name and role are mandatory.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotNull(message = "Role is required (FISHERMAN, BOAT_OWNER, CAPTAIN, CREW_MEMBER, BUYER, ADMIN)")
    private Role role;

    @Builder.Default
    private Language preferredLanguage = Language.TA;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 100, message = "Invalid age")
    private Integer age;

    private String nativePlace;

    private String address;

    @Min(value = 0, message = "Experience years cannot be negative")
    private Integer experienceYears;

    private String primaryHarbor;
}
