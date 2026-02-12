package com.alura.literalura.service;

import com.alura.literalura.client.GutendexClient;
import com.alura.literalura.client.dto.GutendexAutorDTO;
import com.alura.literalura.client.dto.GutendexLibroDTO;
import com.alura.literalura.persistance.entity.AutorEntity;
import com.alura.literalura.persistance.entity.LibroEntity;
import com.alura.literalura.persistance.repository.AutorRepository;
import com.alura.literalura.persistance.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LibroService {

    private final GutendexClient gutendexClient;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public LibroService(GutendexClient gutendexClient,
                        LibroRepository libroRepository,
                        AutorRepository autorRepository) {
        this.gutendexClient = gutendexClient;
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Optional<LibroEntity> buscarYGuardarPorTitulo(String tituloBusqueda) {
        if (tituloBusqueda == null || tituloBusqueda.isBlank()) {
            return Optional.empty();
        }

        String tituloNormalizado = tituloBusqueda.trim();

        // 1) Evitar duplicados: si ya existe, retornarlo (no insertar otra vez)
        Optional<LibroEntity> yaExistente = libroRepository.findByTituloContainingIgnoreCase(tituloNormalizado);
        if (yaExistente.isPresent()) {
            return yaExistente;
        }

        // 2) Consultar la API
        Optional<GutendexLibroDTO> optDto = gutendexClient.searchFirstByTitle(tituloNormalizado);
        if (optDto.isEmpty()) {
            return Optional.empty();
        }
        GutendexLibroDTO dto = optDto.get();

        // 3) Extraer datos del DTO de forma clara (evita reasignaciones que rompan efectivamente final)
        final String tituloDto = dto.title != null ? dto.title.trim() : tituloNormalizado;

        final String nombreAutorFinal;
        final Integer nacimientoFinal;
        final Integer fallecimientoFinal;

        if (dto.authors != null && !dto.authors.isEmpty()) {
            var a = dto.authors.get(0);
            nombreAutorFinal = a.name != null ? a.name.trim() : "Desconocido";
            nacimientoFinal = a.birthYear;
            fallecimientoFinal = a.deathYear;
        } else {
            nombreAutorFinal = "Desconocido";
            nacimientoFinal = null;
            fallecimientoFinal = null;
        }

        final String idiomaFinal = (dto.languages != null && !dto.languages.isEmpty())
                ? dto.languages.get(0)
                : "desconocido";

        final Long descargasFinal = dto.downloadCount != null ? dto.downloadCount.longValue() : 0L;

        // 4) Buscar o crear autor (usando variables final para evitar el problema en lambdas)
        AutorEntity autor = autorRepository.findByNombre(nombreAutorFinal)
                .orElseGet(() -> {
                    AutorEntity nuevo = new AutorEntity(nombreAutorFinal, nacimientoFinal, fallecimientoFinal);
                    return autorRepository.save(nuevo);
                });

        // 5) Antes de guardar, volvemos a comprobar duplicado por si otro hilo insertó el libro entre medias.
        // (Evita insertar dos veces en concurrencia; mejor con restricción DB, ver nota al final)
        if (libroRepository.findByTituloContainingIgnoreCase(tituloDto).isPresent()) {
            return libroRepository.findByTituloContainingIgnoreCase(tituloDto);
        }

        // 6) Crear y persistir libro
        LibroEntity libro = new LibroEntity(tituloDto, idiomaFinal, descargasFinal, autor);
        LibroEntity saved = libroRepository.save(libro);

        // 7) mantener la relación bidireccional en memoria (opcional)
        autor.getLibros().add(saved);
        autorRepository.save(autor);

        return Optional.of(saved);
    }

    public java.util.List<LibroEntity> listarTodos() {
        return libroRepository.findAll();
    }

    public java.util.List<LibroEntity> listarPorIdioma(String idioma) {
        return libroRepository.findAllByIdioma(idioma);
    }

    public long contarPorIdioma(String idioma) {
        return libroRepository.countByIdioma(idioma);
    }

    public java.util.List<LibroEntity> top10MasDescargados() {
        return libroRepository.findTop10ByOrderByNumeroDescargasDesc();
    }
}
