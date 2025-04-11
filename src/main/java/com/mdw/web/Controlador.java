package com.mdw.web;

import com.mdw.dominio.Categoria;
import com.mdw.dominio.ERole;
import com.mdw.dominio.Libro;
import com.mdw.dominio.RegistroLibro;
import com.mdw.dominio.RoleEntity;
import com.mdw.dominio.Tipo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/Libreria")
    public String Libreria(Model model) {
        List<Libro> libros = LibroRepository.findAll();
        model.addAttribute("libros", libros);

        List<Tipo> tipos = TiposRepository.findAll();
        model.addAttribute("tipos", tipos);

        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);

        model.addAttribute("usuario", new Usuario());
        return ("Libreria");
    }

    @GetMapping("/libreria/buscar")
    public String buscarLibros(@RequestParam("query") String query, Model model) {
        List<Libro> libros = LibroRepository.findByTituloContainingIgnoreCase(query);

        model.addAttribute("libros", libros);

        List<Tipo> tipos = TiposRepository.findAll();
        model.addAttribute("tipos", tipos);

        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);

        model.addAttribute("usuario", new Usuario());
        return "Libreria";
    }

    @GetMapping("/Libreria/Filtrar")
    public String LibreriaFiltrada(@RequestParam(required = false) String tipo,
            @RequestParam(required = false) String[] categoria,
            Model model) {

        List<Libro> libros = libroDao.findByFilter(tipo, categoria);

        model.addAttribute("libros", libros);
        model.addAttribute("tipos", TiposRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());

        model.addAttribute("usuario", new Usuario());
        return "Libreria";
    }

    @GetMapping("/api/portadas/{id}")
    public ResponseEntity<byte[]> obtenerPortada(@PathVariable int id) {
        Libro libro = libroDao.findById(id);
        if (libro == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] portada = libro.getPortada();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(portada, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/Miperfil")
    public String Miperfil(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuarioLogeado = usuarioDao.findByUsername(username);

        if (usuarioLogeado == null) {
            return "redirect:/login";
        }

        int idUsuario = usuarioLogeado.getId();
        long cantidadLibros = LibroRepository.countByIdUsuario(idUsuario);
        List<Libro> librosDelUsuario = LibroRepository.findByIdUsuario(idUsuario);

        model.addAttribute("usuario", usuarioLogeado);
        model.addAttribute("cantidadLibros", cantidadLibros);
        model.addAttribute("librosDelUsuario", librosDelUsuario);

        return "Miperfil";
    }

    @GetMapping("/SubirLibro")
    public String SubirLibro(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tipos", TiposRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return ("SubirLibro");
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

    @GetMapping("/VistaDeLibro")
    public String VistaDeLibro(@RequestParam("id") int id, Model model) {
        Optional<Libro> optionalLibro = LibroRepository.findById(id);

        if (optionalLibro.isEmpty()) {
            return "redirect:/Libreria";
        }

        Libro libro = optionalLibro.get();
        Usuario usuario = libro.getUsuario();

        model.addAttribute("libro", libro);
        model.addAttribute("usuario", usuario);
        return "VistaDeLibro";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        model.addAttribute("usuario", new Usuario());
        return ("nosotros");
    }

    @GetMapping("/MenuLibro/{id}")
    public String menuLibro(@PathVariable("id") int idLibro, Model model) {
        Libro libro = libroDao.findById(idLibro);

        if (libro != null) {
            model.addAttribute("libro", libro);
        } else {
            model.addAttribute("error", "Libro no encontrado");
            return "error";
        }

        model.addAttribute("usuario", new Usuario());
        return "MenuLibro";
    }

    @GetMapping({"/", "/index"})
    public String home(Model model) {
        model.addAttribute("usuario", new Usuario());

        List<RegistroLibro> registros = registroLibroRepository.findTop4ByOrderByFechaDesc();

        List<Libro> libros = new ArrayList<>();
        for (RegistroLibro registro : registros) {
            Libro libro = LibroRepository.findById(registro.getIdLibro()).orElse(null);
            if (libro != null) {
                libros.add(libro);
            }
        }
        model.addAttribute("libros", libros);

        return "index";
    }

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
