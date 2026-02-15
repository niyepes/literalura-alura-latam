package com.alura.literalura.persistance.repository;

import com.alura.literalura.persistance.entity.LibroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LibroRepository extends JpaRepository<LibroEntity, Long> {
    Optional<LibroEntity> findByTituloContainingIgnoreCase(String titulo);

    List<LibroEntity> findAllByIdioma(String idioma);

    long countByIdioma(String idioma);

    List<LibroEntity> findTop10ByOrderByNumeroDescargasDesc();
}
