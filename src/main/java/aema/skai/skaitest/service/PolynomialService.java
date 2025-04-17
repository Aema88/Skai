package aema.skai.skaitest.service;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.entity.PolynomialEvaluation;
import aema.skai.skaitest.entity.SimplifiedPolynomial;
import aema.skai.skaitest.repository.PolynomialEvaluationRepository;
import aema.skai.skaitest.repository.PolynomialRepository;
import aema.skai.skaitest.repository.SimplifiedPolynomialRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class PolynomialService {
    @Resource
    private PolynomialRepository polynomialRepository;

    @Resource
    private SimplifiedPolynomialRepository simplifiedPolynomialRepository;

    @Resource
    private PolynomialEvaluationRepository evaluationRepository;

    private final EvalUtilities evalUtilities;
    private final EvalEngine evalEngine;

    public PolynomialService() {
        evalUtilities = new EvalUtilities(false, false);
        evalEngine = new EvalEngine();
    }

    public Polynomial save(Polynomial polynomial) {
        String expressionWithoutSpaces = polynomial.getExpression().replaceAll("\\s+", "");
        polynomial.setExpression(expressionWithoutSpaces);
        if (!polynomialRepository.existsByExpression(polynomial.getExpression())) {
            log.info("Saving new polynomial expression: {}", polynomial.getExpression());
            return polynomialRepository.save(polynomial);
        } else {
            log.info("Polynomial expression already exists: {}", polynomial.getExpression());
            return polynomialRepository.findByExpression(polynomial.getExpression());
        }
    }

    public SimplifiedPolynomial simplifyPolynomial(Polynomial polynomial) {
        SimplifiedPolynomial existingSimplifiedPolynomial = simplifiedPolynomialRepository.findByPolynomialId(polynomial.getId());
        if (existingSimplifiedPolynomial != null) {
            log.info("Returning existing simplified polynomial for ID: {}", polynomial.getId());
            return existingSimplifiedPolynomial;
        }

        log.info("Simplifying polynomial expression: {}", polynomial.getExpression());
        String simplifiedExpression = simplifyExpression(polynomial.getExpression());

        SimplifiedPolynomial simplifiedPolynomial = SimplifiedPolynomial.builder()
                .polynomial(polynomial)
                .simplifiedExpression(simplifiedExpression)
                .build();

        log.info("Saving simplified polynomial: {}", simplifiedExpression);
        return simplifiedPolynomialRepository.save(simplifiedPolynomial);
    }

    public Double evaluatePolynomial(Polynomial polynomial, double x) {
        PolynomialEvaluation existingEvaluation = evaluationRepository.findByPolynomialIdAndInput(polynomial.getId(), x);
        if (existingEvaluation != null) {
            log.info("Returning cached evaluation for polynomial ID: {} and input: {}", polynomial.getId(), x);
            return existingEvaluation.getValue();
        }

        log.info("Evaluating polynomial expression: {} at x = {}", polynomial.getExpression(), x);
        double result = calculateValue(polynomial.getExpression(), x);

        PolynomialEvaluation evaluation = PolynomialEvaluation.builder()
                .polynomial(polynomial)
                .input(x)
                .value(result)
                .build();
        evaluationRepository.save(evaluation);
        log.info("Saving new evaluation result: {} for polynomial ID: {} and input: {}", result, polynomial.getId(), x);

        return result;
    }

    private String simplifyExpression(String expression) {
        try {
            IExpr result = evalUtilities.eval(expression, evalEngine);
            String simplified = result.toString();
            log.info("Simplified expression: {}", simplified);
            return sortPolynomialReverse(simplified);
        } catch (Exception e) {
            log.error("Error simplifying expression: {}", e.getMessage());
            return "Error simplifying expression: " + e.getMessage();
        }
    }

    private String sortPolynomialReverse(String expression) {
        String[] terms = expression.split("(?=[+-])");

        Arrays.sort(terms, (a, b) -> {
            int degreeA = getDegree(a);
            int degreeB = getDegree(b);
            return Integer.compare(degreeB, degreeA);
        });

        String finalResult = IntStream.range(0, terms.length)
                .mapToObj(i -> {
                    String term = terms[i];
                    if (i > 0 && !(term.startsWith("-") || term.startsWith("+"))) {
                        return "+" + term;
                    }
                    return term;
                })
                .collect(Collectors.joining());


        if (finalResult.startsWith("+")) {
            finalResult = finalResult.substring(1);
        }

        return finalResult;
    }

    private int getDegree(String term) {
        if (term.contains("x")) {
            if (term.contains("^")) {
                return Integer.parseInt(term.split("\\^")[1]);
            }
            return 1;
        }
        return 0;
    }

    private Double calculateValue(String expression, double x) {
        String evaluatedExpression = expression.replace("x", String.valueOf(x));
        IExpr result = evalUtilities.eval(evaluatedExpression, evalEngine);
        log.info("Calculated value for expression: {} is {}", evaluatedExpression, result.evalDouble());
        return result.evalDouble();
    }

}
