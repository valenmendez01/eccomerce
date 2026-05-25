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

-- 1. Desactivamos el modo seguro temporalmente
SET SQL_SAFE_UPDATES = 0;

-- 2. Ampliamos el ENUM para que acepte ambas versiones
ALTER TABLE productos 
MODIFY COLUMN categoria ENUM('FIGURITAS', 'ALBUNES', 'ALBUMES', 'COMBOS', 'COCA_COLA', 'EXTRA_STICKERS');

-- 3. Corregimos los datos mal escritos
UPDATE productos 
SET categoria = 'ALBUMES' 
WHERE categoria = 'ALBUNES';

-- 4. Limpiamos el ENUM dejando solo la versión final correcta
ALTER TABLE productos 
MODIFY COLUMN categoria ENUM('FIGURITAS', 'ALBUMES', 'COMBOS', 'COCA_COLA', 'EXTRA_STICKERS');

-- 5. Volvemos a activar el modo seguro por precaución
SET SQL_SAFE_UPDATES = 1;