package aema.skai.skaitest.controller;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.entity.SimplifiedPolynomial;
import aema.skai.skaitest.service.PolynomialService;
import aema.skai.skaitest.validator.PolynomialValidator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/polynomials")
public class PolynomialController {

    @Resource
    private PolynomialService polynomialService;

    @Autowired
    private List<PolynomialValidator> validatorList;

    @GetMapping("/landing")
    public String showLandingPage() {
        return "landing";
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Double> evaluatePolynomial(@RequestParam String expression, @RequestParam double x) {
        log.info("Received request to evaluate polynomial: {}, with value x={}", expression, x);

        Polynomial polynomial = Polynomial.builder()
                .expression(expression)
                .build();

            log.debug("Validating polynomial expression");
            validatorList.forEach(validator -> validator.validate(polynomial));

            log.debug("Saving polynomial to the database");
            Polynomial savedPolynomial = polynomialService.save(polynomial);

            log.debug("Evaluating polynomial");
            Double result = polynomialService.evaluatePolynomial(savedPolynomial, x);

            log.info("Evaluation successful, result: {}", result);
            return ResponseEntity.ok(result);

    }

    @PostMapping("/simplify")
    public ResponseEntity<String> simplifyPolynomial(@RequestParam String expression) {
        log.info("Received request to simplify polynomial: {}", expression);

        Polynomial polynomial = Polynomial.builder()
                .expression(expression)
                .build();


            log.debug("Validating polynomial expression");
            validatorList.forEach(validator -> validator.validate(polynomial));

            log.debug("Saving polynomial to the database");
            Polynomial savedPolynomial = polynomialService.save(polynomial);

            log.debug("Simplifying polynomial");
            SimplifiedPolynomial simplifiedPolynomial = polynomialService.simplifyPolynomial(savedPolynomial);

            log.info("Simplification successful, simplified expression: {}", simplifiedPolynomial.getSimplifiedExpression());
            return ResponseEntity.status(HttpStatus.CREATED).body(simplifiedPolynomial.getSimplifiedExpression());

    }

}
