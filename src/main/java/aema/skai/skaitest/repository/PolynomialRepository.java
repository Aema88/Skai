package aema.skai.skaitest.repository;

import aema.skai.skaitest.entity.Polynomial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolynomialRepository extends JpaRepository<Polynomial, Long> {
    boolean existsByExpression(String expression);
    Polynomial findByExpression(String expression);
}


