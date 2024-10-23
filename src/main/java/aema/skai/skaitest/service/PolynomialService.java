package aema.skai.skaitest.service;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.entity.PolynomialEvaluation;
import aema.skai.skaitest.entity.SimplifiedPolynomial;
import aema.skai.skaitest.repository.PolynomialEvaluationRepository;
import aema.skai.skaitest.repository.PolynomialRepository;
import aema.skai.skaitest.repository.SimplifiedPolynomialRepository;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IExpr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class PolynomialService {

    @Autowired
    private PolynomialRepository polynomialRepository;

    @Autowired
    private SimplifiedPolynomialRepository simplifiedPolynomialRepository; 

    @Autowired
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
            return polynomialRepository.save(polynomial);
        }
        else return polynomialRepository.findByExpression(polynomial.getExpression());
    }

    public SimplifiedPolynomial simplifyPolynomial(Polynomial polynomial) {
        SimplifiedPolynomial existingSimplifiedPolynomial = simplifiedPolynomialRepository.findByPolynomialId(polynomial.getId());
        if (existingSimplifiedPolynomial != null) {
            return existingSimplifiedPolynomial;
        }

        String simplifiedExpression = simplifyExpression(polynomial.getExpression());

        SimplifiedPolynomial simplifiedPolynomial = SimplifiedPolynomial.builder()
                .polynomial(polynomial)
                .simplifiedExpression(simplifiedExpression)
                .build();

        return simplifiedPolynomialRepository.save(simplifiedPolynomial); 
    }

    public Double evaluatePolynomial(Polynomial polynomial, double x) {
        PolynomialEvaluation existingEvaluation = evaluationRepository.findByPolynomialIdAndInput(polynomial.getId(), x);
        if (existingEvaluation != null) {
            return existingEvaluation.getValue();
        }

        double result = calculateValue(polynomial.getExpression(), x);

        PolynomialEvaluation evaluation = PolynomialEvaluation.builder()
                .polynomial(polynomial)
                .input(x)
                .value(result)
                .build();
        evaluationRepository.save(evaluation);

        return result;
    }

    private String simplifyExpression(String expression) {
        try {
            IExpr result = evalUtilities.eval(expression, evalEngine);
            String simplified = result.toString();
            return sortPolynomialReverse(simplified);
        } catch (Exception e) {
            e.printStackTrace();
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
        return result.evalDouble(); 
    }
}

