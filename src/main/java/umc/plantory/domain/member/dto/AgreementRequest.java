package umc.plantory.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgreementRequest {
    @Schema(example = "{\"over14\": true, \"service_terms\": true, \"privacy\": true, \"location\": false, \"marketing\": false}")
    private Map<String, Boolean> termAgreement;
}
