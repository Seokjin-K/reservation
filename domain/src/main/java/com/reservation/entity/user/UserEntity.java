package com.reservation.entity.user;

import com.reservation.entity.base.BaseEntity;
import com.reservation.entity.reservation.ReservationEntity;
import com.reservation.entity.review.ReviewEntity;
import com.reservation.entity.store.StoreEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "USER")
public class UserEntity extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String account;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "userEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<StoreEntity> storeEntities;

    @OneToMany(mappedBy = "userEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<ReservationEntity> reservationEntities;

    @OneToMany(mappedBy = "userEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<ReviewEntity> reviewEntities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;
        UserEntity that = (UserEntity) o;
        return this.id != null && Objects.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority(this.userRole.getAuthority()));
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
}
