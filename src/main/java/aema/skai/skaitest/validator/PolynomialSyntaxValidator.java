package aema.skai.skaitest.validator;

import aema.skai.skaitest.entity.Polynomial;
import aema.skai.skaitest.exception.InvalidPolynomialException;
import org.springframework.stereotype.Service;

@Service
public class PolynomialSyntaxValidator implements PolynomialValidator {
    @Override
    public void validate(final Polynomial polynomial) throws InvalidPolynomialException {
        String polynomialPattern =
                "^([+-]?\\d+(\\*x(\\^\\d+)?)?|x(\\^\\d+)?)([+-]\\d+(\\*x(\\^\\d+)?)?|[+-]x(\\^\\d+)?)*([+-]\\d+)?$";
        String trimmedExpression = polynomial.getExpression().replaceAll("\\s+", "");

        if (!trimmedExpression.matches(polynomialPattern)) {
            throw new InvalidPolynomialException("Invalid polynomial expression: " + polynomial.getExpression());
        }
    }
}
