package de.dhbw.fileservice.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer> {
    List<DocumentEntity> findByNameContaining(String name);
    Optional<DocumentEntity> findByPath(String path);
}
