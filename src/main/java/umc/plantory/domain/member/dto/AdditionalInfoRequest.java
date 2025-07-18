package umc.plantory.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInfoRequest {
    private String nickname;
    private String userId;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
}
