package caffeine.nest_dev.domain.ticket.dto.request;

import caffeine.nest_dev.domain.ticket.entity.Ticket;
import caffeine.nest_dev.domain.ticket.enums.TicketTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketRequestDto {

    @NotBlank(message = "티켓명은 필수입니다")
    @Size(min = 2, max = 100, message = "티켓명은 2-100자 사이여야 합니다")
    private String name;
    
    @NotNull(message = "티켓 가격은 필수입니다")
    @Min(value = 1000, message = "최소 티켓 가격은 1,000원입니다")
    @Max(value = 1000000, message = "최대 티켓 가격은 100만원입니다")
    private Integer price;
    
    @NotBlank(message = "티켓 설명은 필수입니다")
    @Size(min = 10, max = 255, message = "티켓 설명은 10-255자 사이여야 합니다")
    private String description;
    
    @NotNull(message = "티켓 시간은 필수입니다")
    private TicketTime ticketTime;

    public Ticket toEntity() {
        return Ticket.builder()
                .name(name)
                .price(price)
                .ticketTime(ticketTime)
                .description(description)
                .build();
    }
}
