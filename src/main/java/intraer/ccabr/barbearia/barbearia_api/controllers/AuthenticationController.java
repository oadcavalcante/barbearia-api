package intraer.ccabr.barbearia.barbearia_api.controllers;

import intraer.ccabr.barbearia.barbearia_api.domain.user.*;
import intraer.ccabr.barbearia.barbearia_api.dtos.LdapUserDataDTO;
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
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

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

    //POST de Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            // Tenta autenticar via LDAP
            ResponseEntity<LoginResponseDTO> ldapAuthResponse = authenticateViaLdap(data);
            if (ldapAuthResponse != null) {
                return ldapAuthResponse;
            }

            // Tenta autenticar localmente
            ResponseEntity<LoginResponseDTO> localAuthResponse = authenticateLocally(data);
            if (localAuthResponse != null) {
                return localAuthResponse;
            }

            // Se ambas as autenticações falharem, retorna UNAUTHORIZED
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //Autenticação via LDAP.
    private ResponseEntity<LoginResponseDTO> authenticateViaLdap(AuthenticationDTO data) {
        DirContext ctx = null;
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, this.ldapHost);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid=" + data.login() + "," + this.ldapBase);
            env.put(Context.SECURITY_CREDENTIALS, data.password());

            ctx = new InitialDirContext(env);
            System.out.println("LDAP conectado com sucesso");

            // Obter dados do LDAP
            ArrayList<Map<String, String>> dadosLdap = getDadosLdap(data.login(), data.password(), ctx);

            List<LdapUserDataDTO> ldapUserDataList = new ArrayList<>();
            System.out.println("dadosLdap: " + dadosLdap);

            for (Map<String, String> ldapData : dadosLdap) {
                LdapUserDataDTO ldapUserData = new LdapUserDataDTO();
                ldapUserData.setFabGuerra(ldapData.get("FABguerra"));
                ldapUserData.setFabPostoGrad(ldapData.get("FABpostograd"));
                ldapUserData.setFabOM(ldapData.get("FABom"));
                ldapUserData.setFabMail(ldapData.get("mail"));
                ldapUserData.setFabUid(ldapData.get("uid"));
                ldapUserData.setFabNrOrdem(ldapData.get("FABnrordem"));

                ldapUserDataList.add(ldapUserData);

                System.out.println("FABguerra: " + ldapUserData.getFabGuerra());
                System.out.println("FABpostograd: " + ldapUserData.getFabPostoGrad());
                System.out.println("FABom: " + ldapUserData.getFabOM());
                System.out.println("mail: " + ldapUserData.getFabMail());
                System.out.println("uid: " + ldapUserData.getFabUid());
                System.out.println("FABnrordem: " + ldapUserData.getFabNrOrdem());
            }

            UserDetails user = userRepository.findByLogin(data.login());
            if (user == null) {
                String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
                user = new User(data.login(), encryptedPassword, UserRole.USER);
                userRepository.save((User) user);
            }
            var token = tokenService.generateToken((User) user);
            LoginResponseDTO responseDTO = new LoginResponseDTO(token);
            return ResponseEntity.ok(responseDTO);

        } catch (NamingException e) {
            System.out.println("Erro ao conectar no LDAP");
            return null;
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    System.out.println("Erro ao fechar contexto LDAP");
                }
            }
        }
    }

    //Autenticação via banco.
    private ResponseEntity<LoginResponseDTO> authenticateLocally(AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    //POST de cadastro.
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

    private ArrayList<Map<String, String>> getDadosLdap(String user, String senha, DirContext ctx) {
        ArrayList<Map<String, String>> dadosLdap = new ArrayList<>();

        try {
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(ldapBase, "(uid=" + user + ")", searchControls);

            while (results.hasMore()) {
                SearchResult searchResult = results.next();
                //Mostra os atributos do usuário LDAP
                //System.out.println("Encontrado: " + searchResult.getAttributes());

                Attributes attrs = searchResult.getAttributes();
                Map<String, String> ldapData = new HashMap<>();

                ldapData.put("FABpostograd", getStringAttribute(attrs, "FABpostograd"));
                ldapData.put("FABguerra", getStringAttribute(attrs, "FABguerra"));
                ldapData.put("FABom", getStringAttribute(attrs, "FABom"));
                ldapData.put("mail", getStringAttribute(attrs, "mail"));
                ldapData.put("uid", getStringAttribute(attrs, "uid"));
                ldapData.put("FABnrordem", getStringAttribute(attrs, "FABnrordem"));

                dadosLdap.add(ldapData);
            }
        } catch (NamingException e) {
            System.out.println("Erro ao realizar busca LDAP");
            e.printStackTrace();
        }
        return dadosLdap;
    }

    private String getStringAttribute(Attributes attrs, String attributeName) throws NamingException {
        Attribute attr = attrs.get(attributeName);
        return (attr != null) ? (String) attr.get() : null;
    }
}
