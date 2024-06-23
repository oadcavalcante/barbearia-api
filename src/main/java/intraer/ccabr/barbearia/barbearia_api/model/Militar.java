package intraer.ccabr.barbearia.barbearia_api.model;

import intraer.ccabr.barbearia.barbearia_api.dto.DadosAgendamentoMilitar;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "militares")
@Entity(name = "Militar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Militar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String saram;
    private String gradposto;
    private String nomeGuerra;
    private String om;
}
