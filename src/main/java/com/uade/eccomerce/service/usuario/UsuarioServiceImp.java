// package com.uade.eccomerce.service.usuario;

// import java.util.Optional;
// import java.sql.Date;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.stereotype.Service;

// import com.uade.eccomerce.controllers.usuarios.UsuarioRequest;
// import com.uade.eccomerce.controllers.usuarios.CambiarContraRequest;
// import com.uade.eccomerce.entity.Rol;
// import com.uade.eccomerce.entity.Usuario;
// import com.uade.eccomerce.repository.UsuarioRepository;

// @Service
// public class UsuarioServiceImp implements UsuarioService{

//     @Autowired
//     private UsuarioRepository usuarioRepository;

//     @Override
//     public Page<Usuario> getUsuarios(PageRequest pageable) {
//         return usuarioRepository.findAll(pageable);
//     }

//     @Override
//     public Optional<Usuario> getUsuarioById(Long id) {
//         return usuarioRepository.findById(id);
//     }

//     @Override
//     public Optional<Usuario> getUsuarioByEmail(String email) {
//         return usuarioRepository.findByEmail(email);
//     }

//     @Override
//     public Usuario ingresarUsuario(UsuarioRequest request) {
//         Usuario u = new Usuario();

//         u.setUsername(request.getUsername());
//         u.setEmail(request.getEmail());
//         u.setContrasena(request.getContrasena());
//         u.setNombre(request.getNombre());
//         u.setApellido(request.getApellido());
//         u.setActivo(true);
//         u.setFecha_creacion(new java.sql.Date(System.currentTimeMillis()));
//         u.setRol(Rol.COMPRADOR);

//         return usuarioRepository.save(u);
//     }

//     @Override
//     public Usuario cambiarContrasena(Long id, CambiarContraRequest request) {
//         Usuario usuario = usuarioRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//         if (!usuario.getContrasena().equals(request.getContraVieja())) {
//             throw new RuntimeException("La contraseña actual es incorrecta");
//         }

//         usuario.setContrasena(request.getContraNueva());

//         return usuarioRepository.save(usuario);
//     }

//     @Override
//     public Usuario actualizarUsuario(Long id, UsuarioRequest request) {
//         Usuario u = usuarioRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
//         if (request.getUsername() != null) {
//             u.setUsername(request.getUsername());
//         }

//         if (request.getNombre() != null) {
//             u.setNombre(request.getNombre());
//         }

//         if (request.getApellido() != null) {
//             u.setApellido(request.getApellido());;
//         }

//         if (request.getEmail() != null) {
//             u.setEmail(request.getEmail());
//         }

//         return usuarioRepository.save(u);
//     }

//     @Override
//     public void eliminarUsuario(Long id) {
//         usuarioRepository.findById(id).ifPresent(u -> {
//             u.setActivo(false);
//             usuarioRepository.save(u);
//         });
//     }
    
// }
