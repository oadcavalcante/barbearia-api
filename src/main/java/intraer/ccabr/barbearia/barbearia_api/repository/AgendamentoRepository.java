package intraer.ccabr.barbearia.barbearia_api.repository;

import intraer.ccabr.barbearia.barbearia_api.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Verifica se existe um agendamento de acordo com 'data', 'hora' e 'dia da semana'
    boolean existsByDataAndHoraAndDiaSemana(LocalDate data, LocalTime hora, String diaSemana);
}
