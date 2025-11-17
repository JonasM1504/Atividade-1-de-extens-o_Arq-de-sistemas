package com.exemplo.seguranca.console;

import com.exemplo.seguranca.api.ApiSegurancaApplication;
import com.exemplo.seguranca.modelo.Usuario;
import com.exemplo.seguranca.repositorio.UsuarioRepository;
import com.exemplo.seguranca.util.JwtUtil;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Scanner;

/**
 * Pequena interface de console para executar operações básicas localmente
 * (não-HTTP). Inicializa um contexto Spring sem servidor web.
 */
public class ConsoleApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(ApiSegurancaApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        UsuarioRepository usuarioRepository = ctx.getBean(UsuarioRepository.class);
        PasswordEncoder passwordEncoder = ctx.getBean(PasswordEncoder.class);
        JwtUtil jwtUtil = ctx.getBean(JwtUtil.class);

        try {
            new ConsoleApplication().runInteractive(usuarioRepository, passwordEncoder, jwtUtil);
        } finally {
            ctx.close();
        }
    }

    private void runInteractive(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                JwtUtil jwtUtil) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Menu Console - API Segurança ---");
            System.out.println("1) Listar usuários");
            System.out.println("2) Criar usuário");
            System.out.println("3) Login (gera JWT)");
            System.out.println("4) Sair");
            System.out.print("Escolha uma opção: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> listUsers(usuarioRepository);
                case "2" -> createUser(scanner, usuarioRepository, passwordEncoder);
                case "3" -> doLogin(scanner, usuarioRepository, passwordEncoder, jwtUtil);
                case "4" -> {
                    running = false;
                    System.out.println("Saindo...");
                }
                default -> System.out.println("Opção inválida.");
            }
        }

        scanner.close();
    }

    private void listUsers(UsuarioRepository usuarioRepository) {
        System.out.println("\nUsuários cadastrados:");
        usuarioRepository.findAll().forEach(u -> System.out.println(u.toString()));
    }

    private void createUser(Scanner scanner, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Senha (texto plano): ");
        String senha = scanner.nextLine().trim();
        System.out.print("Role (CLIENTE, RESTAURANTE, ADMIN, ENTREGADOR): ");
        String roleInput = scanner.nextLine().trim().toUpperCase();

        Usuario.Role role;
        try {
            role = Usuario.Role.valueOf(roleInput);
        } catch (Exception e) {
            System.out.println("Role inválida. Operação cancelada.");
            return;
        }

        Long restauranteId = null;
        if (role == Usuario.Role.RESTAURANTE || role == Usuario.Role.ENTREGADOR) {
            System.out.print("RestauranteId (número): ");
            String rest = scanner.nextLine().trim();
            if (!rest.isEmpty()) {
                try { restauranteId = Long.parseLong(rest); } catch (NumberFormatException ex) { restauranteId = null; }
            }
        }

        if (usuarioRepository.findByEmail(email).isPresent()) {
            System.out.println("Email já cadastrado.");
            return;
        }

        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setEmail(email);
        novo.setSenha(passwordEncoder.encode(senha));
        novo.setRole(role);
        novo.setRestauranteId(restauranteId);

        Usuario salvo = usuarioRepository.save(novo);
        System.out.println("Usuário criado: " + salvo);
    }

    private void doLogin(Scanner scanner, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        if (opt.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        Usuario usuario = opt.get();
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            System.out.println("Senha incorreta.");
            return;
        }

        String token = jwtUtil.generateToken(usuario);
        System.out.println("JWT gerado: \n" + token);
    }
}
