package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name ="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @ToString.Exclude
    @Column(length = 20,name = "role_name")
    @Enumerated(EnumType.STRING)
    private AppRole roleName;

    public Role() {}

    public Role(AppRole roleName) {
        this.roleName = roleName;
    }


}
