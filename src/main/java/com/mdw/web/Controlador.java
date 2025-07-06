package com.mdw.web;

import com.mdw.dominio.ERole;
import com.mdw.dominio.Libro;
import com.mdw.dominio.RegistroLibro;
import com.mdw.dominio.RoleEntity;
import com.mdw.dominio.Usuario;
import dao.LibroDao;
import dao.UsuarioDao;
import interfaces.CategoriaRepository;
import interfaces.LibroRepository;
import interfaces.RegistroLibroRepository;
import interfaces.RoleRepository;
import interfaces.TiposRepository;
import interfaces.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import org.springframework.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class Controlador {

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private LibroDao libroDao;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TiposRepository TiposRepository;

    @Autowired
    private RegistroLibroRepository registroLibroRepository;

    @Autowired
    private LibroRepository LibroRepository;

    @Autowired
    private UsuarioRepository UsuarioRepository;

    @Autowired
    private RoleRepository RoleRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;


    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {

        if (usuarioDao.findByUsername(usuario.getUsername()) != null) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "registro";
        }

        RoleEntity userRole = RoleRepository.findByName(ERole.USER).orElseThrow(() -> new IllegalStateException("Rol USER no encontrado en la base de datos."));

        usuario.getRoles().add(userRole);
        usuarioDao.save(usuario);

        return "redirect:/index";
    }




    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session) {
        Usuario usuario = usuarioDao.findByUsernameAndPassword(username, password);
        if (usuario != null) {
            session.setAttribute("usuarioLogeado", usuario);
            return "redirect:/index";
        } else {
            model.addAttribute("loginError", "Username o contraseña incorrectos.");
            model.addAttribute("showLoginModal", true);
            return "index";
        }
    }

    //SHHHHH no mostrar por ahora, sube sube sube
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/portadas/{id}")
    public ResponseEntity<byte[]> obtenerPortada(@PathVariable int id) {
        Libro libro = libroDao.findById(id);
        if (libro == null || libro.getPortada() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] portada = libro.getPortada();
        HttpHeaders headers = new HttpHeaders();

        // Detectar si es PNG o JPEG
        if (portada.length > 3 && portada[0] == (byte) 0x89 && portada[1] == 0x50 && portada[2] == 0x4E && portada[3] == 0x47) {
            headers.setContentType(MediaType.IMAGE_PNG);
        } else {
            headers.setContentType(MediaType.IMAGE_JPEG); // por defecto
        }

        return new ResponseEntity<>(portada, headers, HttpStatus.OK);
    }

    @PostMapping("/libros/guardar")
    public String guardarLibro(
            @ModelAttribute Libro libro,
            @RequestParam("archivoPortada") MultipartFile archivoPortada,
            Principal principal) {
        try {
            if (!archivoPortada.isEmpty()) {
                if (!archivoPortada.getContentType().startsWith("image/")) {
                    throw new IllegalArgumentException("El archivo no es una imagen válida.");
                }
                libro.setPortada(archivoPortada.getBytes());
            }

            String username = principal.getName();
            Usuario usuarioLogeado = usuarioDao.findByUsername(username);
            if (usuarioLogeado == null) {
                throw new IllegalStateException("No hay un usuario logeado.");
            }

            libro.setIdUsuario(usuarioLogeado.getId());

            if (libro.getUrl() != null && !libro.getUrl().isEmpty()) {
                String urlOriginal = libro.getUrl();
                if (urlOriginal.contains("/view?usp=sharing")) {
                    String urlModificada = urlOriginal.replace("/view?usp=sharing", "/preview");
                    libro.setUrl(urlModificada);
                }
            }

            Libro libroGuardado = libroDao.save(libro);

            RegistroLibro registro = new RegistroLibro();
            registro.setIdLibro(libroGuardado.getID());
            registro.setFecha(LocalDateTime.now());
            registroLibroRepository.save(registro);

            return "redirect:/Libreria";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error?mensaje=" + e.getMessage();
        }
    }


    @GetMapping("/RegistroUsuario")
    public String listarUsuariosYLibros(Model model) {
        List<Libro> listaLibros = LibroRepository.findAll();
        model.addAttribute("listaLibros", listaLibros);

        List<Usuario> listaUsuarios = UsuarioRepository.findAll();
        model.addAttribute("listaUsuarios", listaUsuarios);
        model.addAttribute("usuario", new Usuario());

        return "RegistroUsuario";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") int id) {
        Usuario usuario = usuarioDao.findById(id);
        if (usuario != null) {
            usuarioDao.delete(usuario);
        }
        return "redirect:/RegistroUsuario";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }

    @GetMapping("/libros/eliminar/{id}")
    public String eliminarLibro(@PathVariable("id") int id) {
        // Eliminar registros relacionados en registro_libro
        List<RegistroLibro> registros = registroLibroRepository.findAll();
        registros.stream()
                .filter(registro -> registro.getIdLibro() == id)
                .forEach(registro -> registroLibroRepository.delete(registro));

        // Eliminar el libro de la tabla libros
        LibroRepository.deleteById(id);

        return "redirect:/RegistroUsuario";
    }


}
