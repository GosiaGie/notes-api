package com.notes_api.entity;

import com.notes_api.security.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "item_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPermission {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "ENUM('VIEWER', 'EDITOR')")
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPermission that = (ItemPermission) o;
        return Objects.equals(id, that.id) && Objects.equals(item, that.item) && Objects.equals(user, that.user)
                && role == that.role;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // Stały hashCode dla danej klasy (standard Hibernate dla encji)
    }

}