package aema.skai.skaitest.repository;

import aema.skai.skaitest.entity.PolynomialEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolynomialEvaluationRepository extends JpaRepository<PolynomialEvaluation, Long> {
    PolynomialEvaluation findByPolynomialIdAndInput(Long polynomialId, Double x);
}
