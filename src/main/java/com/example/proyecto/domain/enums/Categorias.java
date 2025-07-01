package com.example.proyecto.domain.enums;

import java.util.Arrays;
import java.util.List;

public enum Categorias {
    LIMPIEZA, PLOMERIA, ELECTRICISTA, CARPINTERIA, PINTURA, JARDINERIA, CUIDADOS, CONSTRUCCION;
    public static List<Categorias> listarCategorias() {
        return Arrays.asList(Categorias.values());
    }
}
