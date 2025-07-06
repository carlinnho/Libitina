package com.mdw.web;

import com.mdw.dominio.Tipo;
import interfaces.TiposRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tipos")
@CrossOrigin(origins = "http://localhost:4200")
public class TipoRestController {

    @Autowired
    private TiposRepository tiposRepository;

    @GetMapping
    public List<Tipo> obtenerTipos() {
        return tiposRepository.findAll();
    }
}
