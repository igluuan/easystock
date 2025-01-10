package br.devluan.easystock.infrastructure.audit;

import br.devluan.easystock.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action_date")
    private LocalDateTime actionDate;

    @Column(columnDefinition = "TEXT")
    private String previousData;

    @Column(columnDefinition = "TEXT")
    private String newData;

    public enum AuditAction {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT, PASSWORD_CHANGE
    }

}
