package com.openwebinars.todo.rest.repos;

import com.openwebinars.todo.rest.model.Tag;
import com.openwebinars.todo.rest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    List<Tag> findByUser(User user);

    Optional<Tag> findByNameAndUser(String name, User user);
}
