package intraer.ccabr.barbearia.barbearia_api.repository;

import intraer.ccabr.barbearia.barbearia_api.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    boolean existsByDataAndHoraAndDiaSemana(LocalDate data, LocalTime hora, String diaSemana);
}
