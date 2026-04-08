# Test Requests

## Crear un Usuario
Método: POST
URL: http://localhost:4002/usuarios
Body (JSON)
```bash
{
  "username": "comprador_test",
  "email": "comprador@test.com",
  "contrasena": "password123",
  "nombre": "Juan",
  "apellido": "Perez"
}
```
## Crear Producto
Método: POST
URL: http://localhost:4002/productos
Body (JSON)
```bash
{
  "nombre": "Álbum Mundial 2026",
  "description": "Álbum oficial de tapa dura",
  "precio": 15000.0,
  "stock": 50,
  "descuento": 10,
  "categoria": "ALBUNES",
  "idUsuario": 1,
  "urlsImagenes": [
    "http://ejemplo.com/imagen1.jpg",
    "http://ejemplo.com/imagen2.jpg"
  ]
}
```
## Obtener todos los productos
Método: GET
URL (sin paginación): http://localhost:4002/productos
URL (con paginación): http://localhost:4002/productos?page=0&size=10

## Obtener un producto por ID
Método: GET
URL: http://localhost:4002/productos/1

## Actualizar un producto
Método: PUT
URL: http://localhost:4002/productos/1
Body (JSON)
```bash
{
  "nombre": "Álbum Mundial 2026 (Edición Limitada)",
  "description": "Álbum oficial de tapa dura con páginas extra",
  "precio": 18000.0,
  "stock": 30,
  "descuento": 15,
  "categoria": "ALBUNES",
  "idUsuario": 1,
  "urlsImagenes": [
    "http://ejemplo.com/imagen_nueva.jpg"
  ]
}
```
## Eliminar (desactivar) un producto
Método: DELETE
URL: http://localhost:4002/productos/1

Para volver a activar, ejecutar query en bdd
UPDATE marketplace.productos 
SET activo = 1 
WHERE id_producto = 1;

## Filtrar productos por Categoría
Método: GET
URL: http://localhost:4002/productos/filtrar/ALBUNES
URL (con paginación): http://localhost:4002/productos/filtrar/ALBUNES?page=0&size=10

## Filtrar productos por Rango de Precio
Método: GET
URL: http://localhost:4002/productos/filtrar/precio?min=10000&max=20000
URL (con paginación): http://localhost:4002/productos/filtrar/precio?min=10000&max=20000&page=0&size=10

## Buscar productos por Nombre
Método: GET
URL: http://localhost:4002/productos/filtrar/nombre?nombre=Mundial
URL (con paginación): http://localhost:4002/productos/filtrar/nombre?nombre=Mundial&page=0&size=10