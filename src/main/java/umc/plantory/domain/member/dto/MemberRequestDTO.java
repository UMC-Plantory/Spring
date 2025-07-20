package umc.plantory.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Map;

public class MemberRequestDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalInfo {
        @NotBlank
        private String nickname;
        @NotBlank
        private String userId;
        @NotBlank
        private String gender;
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birth;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Agreement {
        @NotNull
        @Schema(example = "{\"over14\": true, \"service_terms\": true, \"privacy\": true, \"location\": false, \"marketing\": false}")
        private Map<String, Boolean> termAgreement;
    }
} 