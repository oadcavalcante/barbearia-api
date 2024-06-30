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
public class MilitarService {
    //Injeção de Dependências - Repositório do Militar
    @Autowired
    private MilitarRepository militarRepository;

    // Salva o militar.
    @Transactional
    public Militar save(Militar militar) {
        return militarRepository.save(militar);
    }

    // Lista todos os militares.
    public List<Militar> findAll() {
        return militarRepository.findAll();
    }

    // Procura um Militar pelo ID.
    public Optional<Militar> findById(Long id) {
        return militarRepository.findById(id);
    }

    // Deleta o militar de acordo com o ID.
    @Transactional
    public void deleteById(Long id) {
        militarRepository.deleteById(id);
    }
}

