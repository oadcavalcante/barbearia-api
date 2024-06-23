package intraer.ccabr.barbearia.barbearia_api.repository;

import intraer.ccabr.barbearia.barbearia_api.model.Militar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MilitarRepository extends JpaRepository<Militar, Long> {

    // verifica se o determinado militar já existe na determinada organização militar (om)
    Optional<Militar> findByNomeGuerraAndOm(String nomeGuerra, String om);
}
