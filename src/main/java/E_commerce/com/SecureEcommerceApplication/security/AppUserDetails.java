package E_commerce.com.SecureEcommerceApplication.security;

import E_commerce.com.SecureEcommerceApplication.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Wraps our User entity — Spring Security works with this
// @AuthenticationPrincipal AppUserDetails → gives access to full User entity
@RequiredArgsConstructor
public class AppUserDetails implements UserDetails {

    @Getter
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE_ prefix is required by Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        // null for OAuth users — Spring Security handles this
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        // email is the unique identifier
        return user.getEmail();
    }

    public String getFullUsername() {
        // email is the unique identifier
        return user.getName();
    }


    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()            {    return true;}
}