package com.alura.literalura.service;

import com.alura.literalura.persistance.entity.AutorEntity;
import com.alura.literalura.persistance.repository.AutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public Optional<AutorEntity> findByNombre(String nombre) {
        return autorRepository.findByNombre(nombre);
    }

    public List<AutorEntity> buscarPorNombre(String nombre) {
        return autorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public AutorEntity save(AutorEntity autor) {
        return autorRepository.save(autor);
    }

    public List<AutorEntity> findAll() {
        return autorRepository.findAll();
    }

    /**
     * Devuelve autores que estuvieron vivos en el año indicado.
     * Implementación combinada usando derived queries:
     *  - todos los nacidos <= year
     *  - y además (fallecimiento is null OR fallecimiento >= year)
     */
    public List<AutorEntity> findAutoresVivosEn(Integer year) {
        // derived queries to fetch parts
        List<AutorEntity> nacidosAntes = autorRepository.findByAnoNacimientoLessThanEqual(year);
        List<AutorEntity> noMuertosOPosteriores = autorRepository.findByAnoFallecimientoIsNullOrAnoFallecimientoGreaterThanEqual(year);

        // intersect the two lists (por id) - simple and seguro
        return nacidosAntes.stream()
                .filter(a -> noMuertosOPosteriores.stream().anyMatch(b -> b.getId().equals(a.getId())))
                .toList();
    }
}