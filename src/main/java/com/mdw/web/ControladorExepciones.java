package com.mdw.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControladorExepciones {
    @ExceptionHandler(Exception.class)
    public String manejarExcepcion(Exception e, Model model) {
        // Sanitizar el mensaje de error
        String mensajeLimpio = e.getMessage().replaceAll("[^\\w\\s.,-]", ""); // Elimina caracteres especiales
        model.addAttribute("mensaje", mensajeLimpio != null ? mensajeLimpio : "Error desconocido.");
        return "error"; // Renderiza la página `error.html`
    }
}
