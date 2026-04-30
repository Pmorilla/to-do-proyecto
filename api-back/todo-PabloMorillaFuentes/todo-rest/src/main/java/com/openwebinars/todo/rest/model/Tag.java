package com.openwebinars.todo.rest.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(mappedBy = "tags")
    private List<Task> tasks = new ArrayList<>();
}
