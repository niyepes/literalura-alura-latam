package com.alura.literalura.persistance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "libros")
public class LibroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String idioma;

    private Long numeroDescargas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private AutorEntity autor;

    public LibroEntity() {}

    public LibroEntity(String titulo, String idioma, Long numeroDescargas, AutorEntity autor) {
        this.titulo = titulo;
        this.idioma = idioma;
        this.numeroDescargas = numeroDescargas;
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "LIBRO\nTítulo: " + titulo + "\nAutor: " + (autor != null ? autor.getNombre() : "Desconocido")
                + "\nIdioma: " + idioma + "\nNúmero de descargas: " + numeroDescargas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LibroEntity)) return false;
        LibroEntity libro = (LibroEntity) o;
        return Objects.equals(id, libro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
