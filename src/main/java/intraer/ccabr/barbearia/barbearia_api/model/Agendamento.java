package intraer.ccabr.barbearia.barbearia_api.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "agendamentos")
@Entity(name = "Agendamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate data;
    private LocalTime hora;
    private String diaSemana;

    @ManyToOne
    @JoinColumn(name = "militar_id")
    private Militar militar;
}
