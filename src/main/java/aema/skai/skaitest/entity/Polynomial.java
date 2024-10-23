package aema.skai.skaitest.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "polynomials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Polynomial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String expression;

    @OneToMany(mappedBy = "polynomial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PolynomialEvaluation> evaluations;
}

