package intraer.ccabr.barbearia.barbearia_api.controller;

import intraer.ccabr.barbearia.barbearia_api.model.Agendamento;
import intraer.ccabr.barbearia.barbearia_api.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {
    @Autowired
    private AgendamentoService service;

    @GetMapping
    public List<Agendamento> findAll() {
        return service.findAll();
    }

    @PostMapping
    public Agendamento save(@RequestBody Agendamento agendamento) {
        if (service.isAgendamentoDisponivel(agendamento.getData(), agendamento.getHora(), agendamento.getDiaSemana())) {
            return service.save(agendamento);
        }
        throw new RuntimeException("Agendamento já existe para essa data e hora.");
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }
}
