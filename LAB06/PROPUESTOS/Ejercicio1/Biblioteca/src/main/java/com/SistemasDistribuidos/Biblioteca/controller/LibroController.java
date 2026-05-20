package com.SistemasDistribuidos.Biblioteca.controller;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/libros")
@CrossOrigin(origins = "*")
public class LibroController {

    private List<String> libros = new ArrayList<>();

    public LibroController() {
        libros.add("El Psicoanalista");
        libros.add("Crónica de una muerte anunciada");
    }

    // 1. Listar libros (GET)
    @GetMapping
    public List<String> listar() {
        return libros;
    }

    // 2. Registrar libro (POST)
    @PostMapping
    public String agregar(@RequestBody String libro) {
        libros.add(libro);
        return "Libro registrado con éxito";
    }

    // 3. Buscar por ID (GET con parámetro)
    @GetMapping("/{id}")
    public String buscarPorId(@PathVariable int id) {
        if (id >= 0 && id < libros.size()) {
            return libros.get(id);
        }
        return "Error: Libro no encontrado";
    }

    // 4. Eliminar libro (DELETE)
    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable int id) {
        if (id >= 0 && id < libros.size()) {
            String eliminado = libros.remove(id);
            return "Eliminado: " + eliminado;
        }
        return "Error: ID no válido";
    }
}