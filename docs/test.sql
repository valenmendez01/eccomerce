use marketplace;

select * from usuarios;
select * from productos;
select * from imagenes_productos;
select * from pedidos;
select * from detalle_pedidos;

# COMPRADOR a VENDEDOR
UPDATE usuarios
SET rol = 'VENDEDOR'
WHERE id_usuario = 1;

# Reactivar producto
UPDATE productos
SET activo = 1
WHERE id_producto = 2;