package intraer.ccabr.barbearia.barbearia_api.services;

import intraer.ccabr.barbearia.barbearia_api.domain.agendamento.Agendamento;
import intraer.ccabr.barbearia.barbearia_api.domain.militar.Militar;
import intraer.ccabr.barbearia.barbearia_api.repositories.AgendamentoRepository;
import intraer.ccabr.barbearia.barbearia_api.repositories.MilitarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AgendamentoService {
    //Injeção de dependências - repositórios
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private MilitarRepository militarRepository;

    // Verifica se o militar já existe na mesma OM, se existir ele não salva o militar no banco. Apenas reutiliza a instância já criada.
    @Transactional
    public Agendamento save(Agendamento agendamento) {
        Militar militar = agendamento.getMilitar();
        // Verifica se o militar já existe na mesma OM.
        Optional<Militar> existingMilitar = militarRepository.findByNomeGuerraAndOm(militar.getNomeGuerra(), militar.getOm());
        if (existingMilitar.isPresent()) {
            // Militar já existe, reutiliza a instância existente.
            agendamento.setMilitar(existingMilitar.get());
        } else {
            // Militar não existe, salva uma nova instância.
            militar = militarRepository.save(militar);

            agendamento.setMilitar(militar);
        }
        // Salva o agendamento.
        return agendamentoRepository.save(agendamento);
    }

    // Método responsável por listar todos os agendamentos.
    public List<Agendamento> findAll() {
        return agendamentoRepository.findAll();
    }

    // Procura um Agendamento pelo ID.
    public Optional<Agendamento> findById(Long id) {
        return agendamentoRepository.findById(id);
    }

    // Exclui definitivamente o agendamento.
    @Transactional
    public void delete(Long id) {
        agendamentoRepository.deleteById(id);
    }

    // Verifica de acordo com a 'data', 'hora' e 'dia da semana' se o agendamento pode ser feito.
    public boolean isAgendamentoDisponivel(LocalDate data, LocalTime hora, String diaSemana, String categoria) {
        return !agendamentoRepository.existsByDataAndHoraAndDiaSemanaAndCategoria(data, hora, diaSemana, categoria);
    }
}
