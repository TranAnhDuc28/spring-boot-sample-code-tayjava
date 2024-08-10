package com.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_group")
public class Group extends AbstractEntity<Integer>{

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToOne
    private Role role;

    @OneToMany(mappedBy = "group")
    private Set<UserHasGroup> users = new HashSet<>();


}
