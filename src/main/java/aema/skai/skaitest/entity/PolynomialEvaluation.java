package aema.skai.skaitest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "polynomial_evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolynomialEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "polynomial_id", nullable = false)
    private Polynomial polynomial;

    private Double value;

    private Double input;
}

