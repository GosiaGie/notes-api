package com.notes_api.entity;

import com.notes_api.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "item_permissions")
@Getter
@Setter
@NoArgsConstructor
public class ItemPermission {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "ENUM('VIEWER', 'EDITOR')")
    private Role role;

}