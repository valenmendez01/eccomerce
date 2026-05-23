package com.uade.eccomerce.entity;


import java.sql.Blob;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "imagenesProductos")
public class ImagenProductos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImagen;

    @Lob // Anotación para objetos grandes
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private Blob contenido;

    @Column(name = "url", nullable = true)
    private String nombreArchivo;

    // Relaciones

    @ManyToOne
    @JoinColumn(name = "idProducto", nullable = false)
    private Producto producto;
}
