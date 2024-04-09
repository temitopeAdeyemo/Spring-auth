package com.springauth.auth.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor // Also needed cuz of the @Builder
@Data
@Builder
@Entity
@Table(name = "auth_users")
public class User implements UserDetails {
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID) // MYSQL does not work with sequence
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(name = "UUID")
//    @Column(unique = true, updatable = false, nullable = false)
//    private UUID id;
    @GeneratedValue

    private Integer id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING) // EnumType.ORDINAL means you get 0, 1, 2 but string get the string value.
    private Role role;

    /**
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * @return
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
