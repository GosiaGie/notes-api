package com.notes_api.entity;

import com.notes_api.Role;
import jakarta.persistence.*;
import lombok.*;

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
        Item itemToEquals = (Item) o;
        return id != null && id.equals(itemToEquals.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // Stały hashCode dla danej klasy (standard Hibernate dla encji)
    }

}