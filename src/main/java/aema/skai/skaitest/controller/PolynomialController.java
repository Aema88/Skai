package aema.skai.skaitest.controller;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.entity.SimplifiedPolynomial;
import aema.skai.skaitest.service.PolynomialService;
import aema.skai.skaitest.validator.PolynomialValidator;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polynomials")
public class PolynomialController {
    @Resource
    private PolynomialService polynomialService;

    @Autowired
    private List<PolynomialValidator> validatorList;


    @PostMapping("/evaluate")
    public ResponseEntity<Double> evaluatePolynomial(@RequestParam String expression, @RequestParam double x) {
        Polynomial polynomial = Polynomial.builder()
                .expression(expression)
                .build();

        validatorList.forEach(validator -> validator.validate(polynomial));
        Polynomial savedPolynomial = polynomialService.save(polynomial);

        Double result = polynomialService.evaluatePolynomial(savedPolynomial, x);
        return ResponseEntity.ok(result);
    }

    
    @PostMapping("/simplify")
    public ResponseEntity<String> simplifyPolynomial(@RequestParam String expression) {
        Polynomial polynomial = Polynomial.builder()
                .expression(expression)
                .build();

        validatorList.forEach(validator -> validator.validate(polynomial));
        Polynomial savedPolynomial = polynomialService.save(polynomial);

        SimplifiedPolynomial simplifiedPolynomial = polynomialService.simplifyPolynomial(savedPolynomial);

        return ResponseEntity.status(HttpStatus.CREATED).body(simplifiedPolynomial.getSimplifiedExpression());
    }

}
