package caffeine.nest_dev.domain.user.entity;

import caffeine.nest_dev.domain.user.enums.UserRole;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserDetailsImpl implements UserDetails, CredentialsContainer { // Spring security 에서 인증이 끝나면 eraseCredentials() 를 자동으로 호출

    private final Long userId;
    private final String email;
    private final UserRole userRole;
    private String password;

    public UserDetailsImpl(Long userId, String email, UserRole userRole, String password) {
        this.userId = userId;
        this.email = email;
        this.userRole = userRole;
        this.password = password;
    }

    public Long getId() {
        return userId;
    }


    // UserRole enum을 문자열로 변환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // true를 직접 반환하는 것이 더 명확! (가독성을 위해)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    // 인증이 끝난 뒤에는 즉시 메모리에서 지우는 것이 보안상 권장되는 방식
    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
