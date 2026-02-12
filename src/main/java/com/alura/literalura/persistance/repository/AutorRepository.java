package com.alura.literalura.persistance.repository;

import com.alura.literalura.persistance.entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<AutorEntity, Long> {
    Optional<AutorEntity> findByNombre(String nombre);

    // derived queries que usaremos
    List<AutorEntity> findByAnoNacimientoLessThanEqual(Integer year);

    List<AutorEntity> findByAnoFallecimientoIsNullOrAnoFallecimientoGreaterThanEqual(Integer year);
}
