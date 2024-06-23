package intraer.ccabr.barbearia.barbearia_api.controller;

import intraer.ccabr.barbearia.barbearia_api.model.Militar;
import intraer.ccabr.barbearia.barbearia_api.service.MilitarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/militares")
public class MilitarController {
    @Autowired
    private MilitarService service;

    @GetMapping
    public List<Militar> findAll() {
        return service.findAll();
    }

    @PostMapping
    public Militar save(@RequestBody Militar militar) {
        return service.save(militar);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }
}
