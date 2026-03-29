package com.notes_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.sql.Types;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "items")
@Audited
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @NotAudited
    private User owner;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "version")
    @Version
    private Long version;

    @Column(name = "deleted")
    @JdbcTypeCode(Types.TINYINT)
    private boolean isDeleted;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", updatable = false, nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private Instant updatedAt;

    @NotAudited
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemPermission> permissions;

}
