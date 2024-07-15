package intraer.ccabr.barbearia.barbearia_api.controllers;

import intraer.ccabr.barbearia.barbearia_api.domain.user.*;
import intraer.ccabr.barbearia.barbearia_api.infra.security.TokenService;
import intraer.ccabr.barbearia.barbearia_api.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${spring.ldap.host}")
    private String ldapHost;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());

        try {
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (AuthenticationException exception) {
            if (this.autenticarPeloLdap(data.login(), data.password())) {
                UserDetails user = userRepository.findByLogin(data.login());
                if (user == null) {
                    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
                    user = new User(data.login(), encryptedPassword, UserRole.USER);
                    userRepository.save((User) user);
                }
                var token = tokenService.generateToken((User) user);
                return ResponseEntity.ok(new LoginResponseDTO(token));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO data){
        if(this.userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());
        this.userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário registrado com sucesso");
    }

    private Boolean autenticarPeloLdap(String user, String senha) {
            try {
                Hashtable<String, String> env = new Hashtable<>();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, this.ldapHost);
                env.put(Context.SECURITY_AUTHENTICATION, "simple");
                env.put(Context.SECURITY_PRINCIPAL, "uid=" + user + "," + this.ldapBase);
                env.put(Context.SECURITY_CREDENTIALS, senha);

                DirContext ctx = new InitialDirContext(env);
                System.out.println("Bind LDAP estabelecido com sucesso");
                ctx.close();
                return true;
            } catch (NamingException e) {
                System.out.println("Erro ao estabelecer bind LDAP");
                return false;
        }
    }
}
