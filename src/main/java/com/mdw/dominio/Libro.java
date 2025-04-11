package com.mdw.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name="libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @NotEmpty
    @Size(max = 300)
    @Column(name = "Titulo")
    private String titulo;

    private String Descripción;

    @Lob
    @Column(name = "Portada", columnDefinition = "LONGBLOB")
    private byte[] Portada;

    @NotEmpty
    private String url;

    private String categoria;

    private String tipo;


    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", insertable = false, updatable = false)
    private Usuario usuario;


    @Column(name = "id_usuario")
    private int idUsuario;
}
