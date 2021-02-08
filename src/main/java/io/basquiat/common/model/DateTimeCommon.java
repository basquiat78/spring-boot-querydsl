package io.basquiat.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 공통적으로 생성일, 수정일을 담당하기 위한 공통 MappedSuperClass
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class DateTimeCommon {

    /** db row 생성일 */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** db row 수정일 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
