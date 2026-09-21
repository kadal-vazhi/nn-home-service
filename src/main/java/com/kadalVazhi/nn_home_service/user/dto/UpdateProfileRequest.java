package com.kadalVazhi.nn_home_service.user.dto;

import com.kadalVazhi.nn_home_service.user.domain.Language;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

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

    private String avatarUrl;

    private String headerBannerUrl;

    private String bio;

    private Language preferredLanguage;
}
