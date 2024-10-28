package com.reservation.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

// 해당 클래스에 정의된 필드들은 상속받는 하위 엔티티 클래스의 매핑 정보에 포함되지만,
// @MappedSuperclass로 선언된 클래스 자체는 데이터베이스 테이블로 생성되지 않습니다.
@MappedSuperclass
// 엔티티의 생성 및 수정 시점에 자동으로 날짜를 설정하기 위해 JPA Auditing 기능을 활성화합니다.
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
