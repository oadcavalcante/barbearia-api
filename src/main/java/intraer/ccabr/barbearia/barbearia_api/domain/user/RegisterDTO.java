package intraer.ccabr.barbearia.barbearia_api.domain.user;

public record RegisterDTO(String login, String password, UserRole role) {
}
