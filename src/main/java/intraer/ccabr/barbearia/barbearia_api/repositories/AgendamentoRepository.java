package intraer.ccabr.barbearia.barbearia_api.repositories;

import intraer.ccabr.barbearia.barbearia_api.domain.agendamento.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // Verifica se existe um agendamento de acordo com 'data', 'hora' e 'dia da semana'
    boolean existsByDataAndHoraAndDiaSemanaAndCategoria(LocalDate data, LocalTime hora, String diaSemana, String categoria);

    @Query("SELECT a FROM Agendamento a WHERE a.militar.saram = :saram ORDER BY a.data DESC")
    Optional<Agendamento> findUltimoAgendamentoBySaram(@Param("saram") String saram);

}
