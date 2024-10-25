package aema.skai.skaitest.service;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.entity.PolynomialEvaluation;
import aema.skai.skaitest.entity.SimplifiedPolynomial;
import aema.skai.skaitest.repository.PolynomialEvaluationRepository;
import aema.skai.skaitest.repository.PolynomialRepository;
import aema.skai.skaitest.repository.SimplifiedPolynomialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IExpr;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolynomialServiceTest {

    @Mock
    private PolynomialRepository polynomialRepository;

    @Mock
    private SimplifiedPolynomialRepository simplifiedPolynomialRepository;

    @Mock
    private PolynomialEvaluationRepository evaluationRepository;

    @Mock
    IExpr mockedExpr;

    @InjectMocks
    private PolynomialService polynomialService;

    private Polynomial polynomial;
    private SimplifiedPolynomial simplifiedPolynomial;
    private final String expression = "x^2+2*x+1";

    @BeforeEach
    void setUp() {
        polynomial = Polynomial.builder()
                .id(1L)
                .expression(expression)
                .build();

        simplifiedPolynomial = SimplifiedPolynomial.builder()
                .polynomial(polynomial)
                .simplifiedExpression(expression)
                .build();
    }

    @Test
    void testSave_NewPolynomial_SavesSuccessfully() {
        when(polynomialRepository.existsByExpression(polynomial.getExpression())).thenReturn(false);
        when(polynomialRepository.save(polynomial)).thenReturn(polynomial);

        Polynomial savedPolynomial = polynomialService.save(polynomial);

        assertEquals(polynomial, savedPolynomial);
        verify(polynomialRepository).save(polynomial);
    }

    @Test
    void testSave_ExistingPolynomial_ReturnsExistingPolynomial() {
        when(polynomialRepository.existsByExpression(polynomial.getExpression())).thenReturn(true);
        when(polynomialRepository.findByExpression(polynomial.getExpression())).thenReturn(polynomial);

        Polynomial foundPolynomial = polynomialService.save(polynomial);

        assertEquals(polynomial, foundPolynomial);
        verify(polynomialRepository, never()).save(polynomial);
        verify(polynomialRepository).findByExpression(polynomial.getExpression());
    }

    @Test
    void testSimplifyPolynomial_NewPolynomial_SimplifiesAndSaves() {
        when(simplifiedPolynomialRepository.findByPolynomialId(polynomial.getId())).thenReturn(null);

        
        try (MockedStatic<EvalUtilities> mockedEvalUtilities = mockStatic(EvalUtilities.class)) {
            mockedEvalUtilities.when(() -> EvalUtilities.eval(anyString(), any(EvalEngine.class)))
                    .thenReturn(mockedExpr);
            when(mockedExpr.toString()).thenReturn(expression);
            when(simplifiedPolynomialRepository.save(any(SimplifiedPolynomial.class))).thenReturn(simplifiedPolynomial);
            SimplifiedPolynomial result = polynomialService.simplifyPolynomial(polynomial);

            assertEquals(simplifiedPolynomial.getSimplifiedExpression(), result.getSimplifiedExpression());
            verify(simplifiedPolynomialRepository).save(any(SimplifiedPolynomial.class));
        }
    }



    @Test
    void testSimplifyPolynomial_ExistingSimplifiedPolynomial_ReturnsExistingPolynomial() {
        when(simplifiedPolynomialRepository.findByPolynomialId(polynomial.getId())).thenReturn(simplifiedPolynomial);

        SimplifiedPolynomial result = polynomialService.simplifyPolynomial(polynomial);

        assertEquals(simplifiedPolynomial, result);
        verify(simplifiedPolynomialRepository, never()).save(any(SimplifiedPolynomial.class));
    }

    @Test
    void testEvaluatePolynomial_NewEvaluation_CalculatesAndSaves() {
        when(mockedExpr.evalDouble()).thenReturn(9.0);

        
        try (MockedStatic<EvalUtilities> mockedEvalUtilities = mockStatic(EvalUtilities.class)) {
            mockedEvalUtilities.when(() -> EvalUtilities.eval(anyString(), any(EvalEngine.class)))
                    .thenReturn(mockedExpr);
            PolynomialEvaluation evaluation = new PolynomialEvaluation();
            evaluation.setValue(9.0);
            when(evaluationRepository.save(any(PolynomialEvaluation.class))).thenReturn(evaluation);

            Double result = polynomialService.evaluatePolynomial(polynomial, 2.0);

            assertEquals(9.0, result);
            verify(evaluationRepository).save(any(PolynomialEvaluation.class));
        }
    }


    @Test
    void testEvaluatePolynomial_ExistingEvaluation_ReturnsCachedValue() {
        PolynomialEvaluation evaluation = new PolynomialEvaluation();
        evaluation.setValue(4.0);
        when(evaluationRepository.findByPolynomialIdAndInput(polynomial.getId(), 2.0)).thenReturn(evaluation);

        Double result = polynomialService.evaluatePolynomial(polynomial, 2.0);

        assertEquals(4.0, result);
        verify(evaluationRepository, never()).save(any(PolynomialEvaluation.class));
    }
}
