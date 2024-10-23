package aema.skai.skaitest.validator;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.exception.InvalidPolynomialException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

class PolynomialSyntaxValidatorTest {

    private PolynomialSyntaxValidator polynomialSyntaxValidator;

    @BeforeEach
    void setUp() {
        polynomialSyntaxValidator = new PolynomialSyntaxValidator();
    }

    @Test
    void validate_ShouldPassForValidPolynomial() {
        Polynomial validPolynomial = new Polynomial();
        validPolynomial.setExpression("3*x^2+2*x-5");
        assertThatNoException().isThrownBy(() -> polynomialSyntaxValidator.validate(validPolynomial));
    }

    @Test
    void validate_ShouldThrowExceptionForInvalidPolynomial() {
        Polynomial invalidPolynomial = new Polynomial();
        invalidPolynomial.setExpression("3x + + 1");

        assertThatExceptionOfType(InvalidPolynomialException.class)
                .isThrownBy(() -> polynomialSyntaxValidator.validate(invalidPolynomial))
                .withMessage("Invalid polynomial expression: 3x + + 1");
    }

    @Test
    void validate_ShouldThrowExceptionForEmptyExpression() {
        Polynomial emptyPolynomial = new Polynomial();
        emptyPolynomial.setExpression("");

        assertThatExceptionOfType(InvalidPolynomialException.class)
                .isThrownBy(() -> polynomialSyntaxValidator.validate(emptyPolynomial))
                .withMessage("Invalid polynomial expression: ");
    }

    @Test
    void validate_ShouldThrowExceptionForInvalidCharacters() {
        Polynomial invalidCharactersPolynomial = new Polynomial();
        invalidCharactersPolynomial.setExpression("3*x^2 + 2x + abc");

        assertThatExceptionOfType(InvalidPolynomialException.class)
                .isThrownBy(() -> polynomialSyntaxValidator.validate(invalidCharactersPolynomial))
                .withMessage("Invalid polynomial expression: 3*x^2 + 2x + abc");
    }
}
