package intraer.ccabr.barbearia.barbearia_api.repositories;

import intraer.ccabr.barbearia.barbearia_api.domain.militar.Militar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MilitarRepository extends JpaRepository<Militar, Long> {

    // verifica se o determinado militar já existe na determinada organização militar (OM)
    Optional<Militar> findBySaramAndGradpostoAndNomeGuerraAndOm(String saram, String gradposto, String nomeGuerra, String om);

    @Query("SELECT m FROM Militar m WHERE LOWER(m.categoria) = LOWER(:categoria)")
    List<Militar> findByCategoria(String categoria);
}
