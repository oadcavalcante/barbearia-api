package intraer.ccabr.barbearia.barbearia_api.domain.militar;

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
