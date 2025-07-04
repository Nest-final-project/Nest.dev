package caffeine.nest_dev.common.dto;

import caffeine.nest_dev.common.enums.BaseCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonResponse<T> {
    private final String httpStatus;
    private final String message;
    // null 인 경우 응답에서 제외할 수 있다 (주석처리하면 null 값 포함해서 응답 반환)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private CommonResponse(HttpStatus httpStatus, String message, T data) {
        this.httpStatus = httpStatus.getReasonPhrase();
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> of(BaseCode code, T data) {
        return new CommonResponse<>(code.getStatus(), code.getMessage(), data);
    }

    public static <T> CommonResponse<T> of(BaseCode code) {
        return of(code, null);
    }
}
