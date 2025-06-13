package caffeine.nest_dev.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRequestDto {

    private String password;
    private String refreshToken;

}
