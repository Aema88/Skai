package aema.skai.skaitest.controller;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.entity.SimplifiedPolynomial;
import aema.skai.skaitest.exception.InvalidInputException;
import aema.skai.skaitest.exception.InvalidPolynomialException;
import aema.skai.skaitest.service.PolynomialService;
import aema.skai.skaitest.validator.PolynomialValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PolynomialControllerTest {

    @Mock
    private PolynomialService polynomialService;

    @Mock
    private List<PolynomialValidator> validatorList;

    @InjectMocks
    private PolynomialController polynomialController;

    private MockMvc mockMvc;

    private Polynomial polynomial;
    private final String urlToEvaluate = "/api/polynomials/evaluate";
    private final String urlToSimplify = "/api/polynomials/simplify";
    private final String expressionParam = "expression";
    private final String xParam = "x";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(polynomialController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        polynomial = Polynomial.builder()
                .id(1L)
                .expression("x*x")
                .build();
    }

    @Test
    void evaluatePolynomial_ShouldReturnEvaluationResult() throws Exception {

        when(polynomialService.save(any(Polynomial.class))).thenReturn(polynomial);
        when(polynomialService.evaluatePolynomial(any(Polynomial.class), eq(2.0))).thenReturn(4.0);

        mockMvc.perform(post(urlToEvaluate)
                        .param(expressionParam, polynomial.getExpression())
                        .param(xParam, "2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("4.0"));
    }

    @Test
    void simplifyPolynomial_ShouldReturnSimplifiedExpression() throws Exception {
        SimplifiedPolynomial simplifiedPolynomial = SimplifiedPolynomial.builder()
                .polynomial(polynomial)
                .simplifiedExpression("x^2")
                .build();

        when(polynomialService.save(any(Polynomial.class))).thenReturn(polynomial);
        when(polynomialService.simplifyPolynomial(polynomial)).thenReturn(simplifiedPolynomial);

        mockMvc.perform(post(urlToSimplify)
                        .param(expressionParam, polynomial.getExpression())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(content().string("x^2"));
    }
}
