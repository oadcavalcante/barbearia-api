package intraer.ccabr.barbearia.barbearia_api.service;

import intraer.ccabr.barbearia.barbearia_api.model.Militar;
import intraer.ccabr.barbearia.barbearia_api.repository.MilitarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MilitarService {
    @Autowired
    private MilitarRepository repository;

    public List<Militar> findAll() {
        return repository.findAll();
    }

    public Militar save(Militar militar) {
        return repository.save(militar);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

