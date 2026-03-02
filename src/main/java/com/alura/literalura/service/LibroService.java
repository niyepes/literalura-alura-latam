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

import java.util.List;
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
    public EnumResultadoGuardar buscarYGuardarPorTitulo(String titulo) {

        List<LibroEntity> similares =
                libroRepository.findByTituloContainingIgnoreCase(titulo);

        boolean existeExacto = similares.stream()
                .anyMatch(libro ->
                        libro.getTitulo().equalsIgnoreCase(titulo));

        if (existeExacto) {
            return EnumResultadoGuardar.YA_EXISTE;
        }

        Optional<LibroEntity> libroOpt = buscarLibroEnApi(titulo);

        if (libroOpt.isEmpty()) {
            return EnumResultadoGuardar.NO_ENCONTRADO;
        }

        libroRepository.save(libroOpt.get());
        return EnumResultadoGuardar.GUARDADO;
    }

    private Optional<LibroEntity> buscarLibroEnApi(String titulo) {
        Optional<GutendexLibroDTO> dtoOpt = gutendexClient.searchFirstByTitle(titulo);
        
        if (dtoOpt.isEmpty()) {
            return Optional.empty();
        }
        
        GutendexLibroDTO dto = dtoOpt.get();
        
        // Handle author - create or find existing
        AutorEntity autor = null;
        if (dto.authors != null && !dto.authors.isEmpty()) {
            GutendexAutorDTO autorDto = dto.authors.get(0);
            Optional<AutorEntity> autorExistente = autorRepository.findByNombre(autorDto.name);
            autor = autorExistente.orElseGet(() -> {
                AutorEntity nuevoAutor = new AutorEntity(
                    autorDto.name,
                    autorDto.birthYear,
                    autorDto.deathYear
                );
                return autorRepository.save(nuevoAutor);
            });
        }
        
        // Handle language - take first language or default
        String idioma = (dto.languages != null && !dto.languages.isEmpty()) 
            ? dto.languages.get(0) 
            : "unknown";
        
        LibroEntity libro = new LibroEntity(
            dto.title,
            idioma,
            dto.downloadCount != null ? dto.downloadCount.longValue() : 0L,
            autor
        );
        
        return Optional.of(libro);
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
