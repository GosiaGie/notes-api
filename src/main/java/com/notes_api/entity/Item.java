package com.notes_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "items")
@Audited
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @NotAudited
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "version")
    private Integer version;

    @Column(name = "deleted")
    @JdbcTypeCode(Types.TINYINT)
    private boolean isDeleted;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updateDateTime;

    @NotAudited
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "note", cascade = CascadeType.REMOVE)
    private Set<ItemPermission> permissions;

}
