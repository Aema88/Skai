package aema.skai.skaitest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "simplified_polynomials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimplifiedPolynomial{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simplified_expression", nullable = false)
    private String simplifiedExpression; 

    @ManyToOne
    @JoinColumn(name = "polynomial_id", nullable = false)
    private Polynomial polynomial; 
}

