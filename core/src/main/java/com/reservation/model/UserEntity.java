package com.reservation.model;

import com.reservation.model.common.BaseEntity;
import com.reservation.constant.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "USER")
public class UserEntity extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String account;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    // 엔티티가 영속성 컨텍스트에 저장되기 전에 호출되는 메서드입니다.
    // 이 메서드를 통해 기본값을 설정할 수 있습니다.
    // 데이터베이스에서 기본값이 설정돼 있지만, 데이터베이스에 구애받지 않도록 함.
    /*@PrePersist
    public void prePersist() {
        if (this.userRole == null) {
            this.userRole = UserRole.ROLE_CUSTOMER;
        }
    }*/
}
