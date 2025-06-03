package caffeine.nest_dev.common.config;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            // 토큰이 블랙리스트에 있는지 확인
            log.info("블랙리스트에서 토큰 값 확인");
            String blacklistToken = stringRedisTemplate.opsForValue().get(BLACKLIST_PREFIX + token);
            if (blacklistToken != null) {
                // 있을 때 에러 발생
                log.info("블랙리스에 토큰 존재");
                sendErrorCommonResponse(response, ErrorCode.IS_BLACKLISTED);
                return;
            }

            // 토큰 유효성 검사
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                User user = userRepository.findByIdAndIsDeletedFalse(userId).orElse(null);

                // UserDetailsImpl에 로그인 된 유저 정보 저장
                if (user != null) {
                    UserDetailsImpl userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), user.getUserRole(), user.getPassword());
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } else {
                // 토큰이 유효하지 않을 때 에러 발생
                sendErrorCommonResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    // "Bearer" 부분 자르기
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    // 공통 응답 형식으로 에러 보내기
    private void sendErrorCommonResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException{
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        CommonResponse<Object> objectCommonResponse = CommonResponse.of(errorCode);

        String s = objectMapper.writeValueAsString(objectCommonResponse);

        response.getWriter().write(s);
    }
}
