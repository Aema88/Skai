package aema.skai.skaitest.repository;

import aema.skai.skaitest.entity.SimplifiedPolynomial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimplifiedPolynomialRepository extends JpaRepository<SimplifiedPolynomial, Long> {
    SimplifiedPolynomial findByPolynomialId(Long id);
}