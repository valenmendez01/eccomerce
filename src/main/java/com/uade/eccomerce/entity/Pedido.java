package com.uade.eccomerce.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @Column(nullable = false)
    private Date fechaPedido;

    @Column(nullable = false)
    private Double total;

    public void addDetalle(DetallePedidos detalle) {
    if (this.detallePedidos == null) {
        this.detallePedidos = new java.util.ArrayList<>();
    }
    this.detallePedidos.add(detalle);
    detalle.setPedido(this);
    }

    // Relaciones

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedidos> detallePedidos;
}
