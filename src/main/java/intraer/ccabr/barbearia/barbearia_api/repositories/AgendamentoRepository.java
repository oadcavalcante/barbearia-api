package intraer.ccabr.barbearia.barbearia_api.repositories;

import intraer.ccabr.barbearia.barbearia_api.domain.agendamento.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Verifica se existe um agendamento de acordo com 'data', 'hora' e 'dia da semana'
    boolean existsByDataAndHoraAndDiaSemanaAndCategoria(LocalDate data, LocalTime hora, String diaSemana, String categoria);
}
