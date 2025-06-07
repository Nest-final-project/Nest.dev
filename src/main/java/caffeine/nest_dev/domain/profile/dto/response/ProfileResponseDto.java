package caffeine.nest_dev.domain.profile.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProfileResponseDto {
    private Long id;
    private String role;
    private String title;
    private String introduction;
    private String category;
    private List<String> keywords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
