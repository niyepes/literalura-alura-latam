package com.alura.literalura.cli;

import com.alura.literalura.persistance.entity.AutorEntity;
import com.alura.literalura.persistance.entity.LibroEntity;
import com.alura.literalura.service.AutorService;
import com.alura.literalura.service.EnumResultadoGuardar;
import com.alura.literalura.service.LibroService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class MenuRunner implements CommandLineRunner {

    private final LibroService bookService;
    private final AutorService authorService;

    public MenuRunner(LibroService bookService, AutorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @Override
    public void run(String... args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                showMenu();
                System.out.print("Elija la opción: ");
                String opt = scanner.nextLine().trim();
                switch (opt) {
                    case "1" -> buscarLibroPorTitulo(scanner);
                    case "2" -> listarLibrosRegistrados();
                    case "3" -> listarAutoresRegistrados();
                    case "4" -> listarAutoresVivosEnAno(scanner);
                    case "5" -> listarLibrosPorIdioma(scanner);
                    case "6" -> mostrarCantidadPorIdioma();
                    case "7" -> mostrarTop10Descargas();
                    case "8" -> buscarAutorPorNombre(scanner);
                    case "0" -> {
                        System.out.println("Saliendo...");
                        running = false;
                    }
                    default -> System.out.println("Opción inválida.");
                }
                System.out.println();
            }
        }
    }

    private void showMenu() {
        System.out.println("------------ MENU ------------");
        System.out.println("1 - Buscar libro por título (API y guardar)");
        System.out.println("2 - Listar libros registrados (DB)");
        System.out.println("3 - Listar autores registrados (DB)");
        System.out.println("4 - Listar autores vivos en un determinado año (DB)");
        System.out.println("5 - Listar libros por idioma (DB)");
        System.out.println("6 - Mostrar cantidad de libros por idioma (ing y es mínimo)");
        System.out.println("7 - Top 10 libros más descargados (DB)");
        System.out.println("8 - Buscar autor por nombre (DB)");
        System.out.println("0 - Salir");
    }

    private void buscarLibroPorTitulo(Scanner scanner) {
        System.out.print("Ingrese el nombre del libro que desea buscar: ");
        String titulo = scanner.nextLine().trim();
        if (titulo.isBlank()) {
            System.out.println("Título inválido.");
            return;
        }
        EnumResultadoGuardar resultado = bookService.buscarYGuardarPorTitulo(titulo);
        switch (resultado) {
            case GUARDADO -> System.out.println("Libro encontrado y guardado exitosamente.");
            case YA_EXISTE -> System.out.println("El libro ya existe en la base de datos.");
            case NO_ENCONTRADO -> System.out.println("No se encontró ningún libro con ese título en la API.");
        }
    }

    @Transactional
    private void listarLibrosRegistrados() {
        List<LibroEntity> libros = bookService.listarTodos();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        for (LibroEntity l : libros) {
            System.out.println("-----------------------------");
            System.out.println(l);
        }
    }

    private void listarAutoresRegistrados() {
        List<AutorEntity> autores = authorService.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }
        autores.forEach(a -> {
            System.out.println("-----------------------------");
            System.out.println(a);
        });
    }

    private void listarAutoresVivosEnAno(Scanner scanner) {
        System.out.print("Ingrese el año (ej. 1900): ");
        String s = scanner.nextLine().trim();
        try {
            Integer year = Integer.valueOf(s);
            List<AutorEntity> vivos = authorService.findAutoresVivosEn(year);
            if (vivos.isEmpty()) {
                System.out.println("No se hallaron autores vivos en el año " + year);
            } else {
                System.out.println("Autores vivos en " + year + ":");
                vivos.forEach(System.out::println);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Año inválido.");
        }
    }

    private void listarLibrosPorIdioma(Scanner scanner) {
        System.out.print("Ingrese el idioma (ej: en, es): ");
        String idioma = scanner.nextLine().trim();
        if (idioma.isBlank()) {
            System.out.println("Idioma inválido.");
            return;
        }
        List<LibroEntity> libros = bookService.listarPorIdioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en el idioma: " + idioma);
        } else {
            libros.forEach(l -> {
                System.out.println("-----------------------------");
                System.out.println(l);
            });
        }
    }

    private void mostrarCantidadPorIdioma() {
        String[] idiomas = {"en", "es"};
        for (String idi : idiomas) {
            long cnt = bookService.contarPorIdioma(idi);
            System.out.printf("Idioma '%s': %d libros%n", idi, cnt);
        }
    }

    private void mostrarTop10Descargas() {
        List<LibroEntity> top = bookService.top10MasDescargados();
        if (top.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        System.out.println("Top libros por descargas:");
        for (int i = 0; i < top.size(); i++) {
            LibroEntity l = top.get(i);
            System.out.printf("%d) %s - %d descargas%n", i + 1, l.getTitulo(), l.getNumeroDescargas());
        }
    }

    private void buscarAutorPorNombre(Scanner scanner) {
        System.out.print("Ingrese el nombre (o parte) del autor: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isBlank()) {
            System.out.println("Nombre inválido.");
            return;
        }
        // simple search by full name exact
        var opt = authorService.findByNombre(nombre);
        if (opt.isPresent()) {
            System.out.println(opt.get());
        } else {
            System.out.println("Autor no encontrado por nombre exacto. (Podrías implementar búsqueda partial en repo)");
        }
    }
}