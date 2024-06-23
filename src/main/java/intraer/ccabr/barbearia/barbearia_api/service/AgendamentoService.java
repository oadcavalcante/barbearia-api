package intraer.ccabr.barbearia.barbearia_api.service;

import intraer.ccabr.barbearia.barbearia_api.model.Agendamento;
import intraer.ccabr.barbearia.barbearia_api.model.Militar;
import intraer.ccabr.barbearia.barbearia_api.repository.AgendamentoRepository;
import intraer.ccabr.barbearia.barbearia_api.repository.MilitarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private MilitarRepository militarRepository;

    public List<Agendamento> findAll() {
        return agendamentoRepository.findAll();
    }

    @Transactional
    public Agendamento save(Agendamento agendamento) {
        Militar militar = agendamento.getMilitar();

        // Verifica se o militar já existe na mesma om
        Optional<Militar> existingMilitar = militarRepository.findByNomeGuerraAndOm(militar.getNomeGuerra(), militar.getOm());

        if (existingMilitar.isPresent()) {
            // Militar já existe, reutiliza a instância existente
            agendamento.setMilitar(existingMilitar.get());
        } else {
            // Militar não existe, salva uma nova instância
            militar = militarRepository.save(militar);
            agendamento.setMilitar(militar);
        }

        return agendamentoRepository.save(agendamento);
    }

    public boolean isAgendamentoDisponivel(LocalDate data, LocalTime hora, String diaSemana) {
        return !agendamentoRepository.existsByDataAndHoraAndDiaSemana(data, hora, diaSemana);
    }

    public void deleteById(Long id) {
        agendamentoRepository.deleteById(id);
    }
}
