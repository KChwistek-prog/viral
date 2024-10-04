package com.med.viral.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@ToString
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@Entity
@AllArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @CreatedBy
    private Integer createdBy;
    @CreatedDate
    private LocalDateTime createdDate;
    @Enumerated
    private ActionType actionType;
    private String fieldName;
    private String oldVersion;
    private String newVersion;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Action action = (Action) o;
        return getId() != null && Objects.equals(getId(), action.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
