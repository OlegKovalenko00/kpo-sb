package hse.kpo.domains;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @PrePersist
    protected void onCreate() {
        receivedAt = LocalDateTime.now();
    }
}
