package intraer.ccabr.barbearia.barbearia_api.controllers;

import intraer.ccabr.barbearia.barbearia_api.domain.agendamento.Agendamento;
import intraer.ccabr.barbearia.barbearia_api.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {
    @Autowired
    private AgendamentoService agendamentoService;

    // Método CREATE - Salva o Agendamento.
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody Agendamento agendamento) {
        try {
            if (!isHorarioDisponivel(agendamento)) {
                return buildResponse("O horário selecionado já está ocupado. Por favor, escolha outro horário.", HttpStatus.CONFLICT);
            }

            String saram = agendamento.getMilitar().getSaram();
            if (!agendamentoService.podeAgendar(saram)) {
                return buildResponse("Só é possível agendar a cada 15 dias. Por favor, aguarde antes de fazer um novo agendamento.", HttpStatus.FORBIDDEN);
            }

            Agendamento savedAgendamento = agendamentoService.save(agendamento);
            return buildResponse(savedAgendamento, HttpStatus.CREATED);

        } catch (Exception e) {
            return buildResponse("Erro interno no servidor. Por favor, tente novamente mais tarde.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isHorarioDisponivel(Agendamento agendamento) {
        return agendamentoService.isAgendamentoDisponivel(
                agendamento.getData(),
                agendamento.getHora(),
                agendamento.getDiaSemana(),
                agendamento.getCategoria()
        );
    }

    private ResponseEntity<Object> buildResponse(Object body, HttpStatus status) {
        return new ResponseEntity<>(body, status);
    }

    // Método READ - Lista todos os agendamentos disponíveis.
    @GetMapping
    public ResponseEntity<List<Agendamento>> findAll() {
        List<Agendamento> agendamentos = agendamentoService.findAll();
        if (agendamentos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(agendamentos, HttpStatus.OK);
    }

    // Método READ de um agendamento por ID.
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> findById(@PathVariable Long id) {
        Optional<Agendamento> agendamento = agendamentoService.findById(id);
        return agendamento.map(ag -> new ResponseEntity<>(ag, HttpStatus.OK)) //status 200
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Método DELETE - Exclui um Agendamento pelo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            agendamentoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Status 409
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Status 500
        }
    }
}
