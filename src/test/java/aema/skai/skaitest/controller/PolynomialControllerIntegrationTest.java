package aema.skai.skaitest.controller;

import aema.skai.skaitest.SkaiTestApplication;
import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.repository.PolynomialRepository;
import aema.skai.skaitest.validator.PolynomialValidator;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SkaiTestApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PolynomialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private List<PolynomialValidator> validatorList;

    @Autowired
    private PolynomialRepository polynomialRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final String urlToEvaluate = "/api/polynomials/evaluate";
    private final String urlToSimplify = "/api/polynomials/simplify";
    private final String expressionParam = "expression";
    private final String xParam = "x";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void evaluatePolynomial_ShouldReturnEvaluationResult() throws Exception {
        Polynomial polynomial = Polynomial.builder()
                .expression("x+2")
                .build();
        Polynomial savedPolynomial = polynomialRepository.save(polynomial);

        mockMvc.perform(post(urlToEvaluate)
                        .param(expressionParam, "x+2")
                        .param(xParam, "2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("4.0"));
    }

    @Test
    void simplifyPolynomial_ShouldReturnSimplifiedExpression() throws Exception {
        Polynomial polynomial = Polynomial.builder()
                .expression("x+x")
                .build();
        Polynomial savedPolynomial = polynomialRepository.save(polynomial);

        mockMvc.perform(post(urlToSimplify)
                        .param(expressionParam, "x+x")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(content().string("2*x"));
    }

    @Test
    void validatePolynomialExpression_ShouldThrowExceptionForInvalidExpression() throws Exception {
                mockMvc.perform(post(urlToSimplify)
                        .param(expressionParam, "3x + + 1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Invalid polynomial expression: 3x + + 1"));
    }
}
