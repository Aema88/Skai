package aema.skai.skaitest.service;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.entity.SimplifiedPolynomial;
import aema.skai.skaitest.repository.PolynomialEvaluationRepository;
import aema.skai.skaitest.repository.PolynomialRepository;
import aema.skai.skaitest.repository.SimplifiedPolynomialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PolynomialServiceIntegrationTest {

    @Autowired
    private PolynomialService polynomialService;

    @Autowired
    private PolynomialRepository polynomialRepository;

    @Autowired
    private SimplifiedPolynomialRepository simplifiedPolynomialRepository;

    @Autowired
    private PolynomialEvaluationRepository evaluationRepository;

    private Polynomial polynomial;

    @BeforeEach
    void setUp() {
        polynomialRepository.deleteAll();
        simplifiedPolynomialRepository.deleteAll();
        evaluationRepository.deleteAll();
        polynomial = Polynomial.builder()
                .expression("x^2+2*x+1+x")
                .build();
    }

    @Test
    void testSavePolynomial() {
        Polynomial savedPolynomial = polynomialService.save(polynomial);

        assertNotNull(savedPolynomial.getId());
        assertEquals(polynomial.getExpression(), savedPolynomial.getExpression());
    }

    @Test
    void testSimplifyPolynomial() {
        Polynomial savedPolynomial = polynomialService.save(polynomial);

        SimplifiedPolynomial simplifiedPolynomial = polynomialService.simplifyPolynomial(savedPolynomial);

        assertNotNull(simplifiedPolynomial);
        assertEquals("x^2+3*x+1", simplifiedPolynomial.getSimplifiedExpression());
    }

    @Test
    void testEvaluatePolynomial() {
        Polynomial savedPolynomial = polynomialService.save(polynomial);

        Double result = polynomialService.evaluatePolynomial(savedPolynomial, 2.0);

        assertNotNull(result);
        assertEquals(11.0, result);
    }

    @Test
    void testEvaluatePolynomial_CacheResult() {
        Polynomial savedPolynomial = polynomialService.save(polynomial);

        Double firstResult = polynomialService.evaluatePolynomial(savedPolynomial, 2.0);

        Double secondResult = polynomialService.evaluatePolynomial(savedPolynomial, 2.0);

        assertEquals(firstResult, secondResult);
    }

    @Test
    void testSimplifyPolynomial_CacheResult() {
        Polynomial savedPolynomial = polynomialService.save(polynomial);
        SimplifiedPolynomial firstSimplified = polynomialService.simplifyPolynomial(savedPolynomial);
        SimplifiedPolynomial secondSimplified = polynomialService.simplifyPolynomial(savedPolynomial);
        assertEquals(firstSimplified.getSimplifiedExpression(), secondSimplified.getSimplifiedExpression());
    }
}
