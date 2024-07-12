package intraer.ccabr.barbearia.barbearia_api.repositories;

import intraer.ccabr.barbearia.barbearia_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByLogin(String login);
}
