package nl.giphouse.propr.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * TODO: valideer email a.d.h.v. sturen van mail
 * @author haye.
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

	private static final String VALID_EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

	@Override
	public void initialize(final ValidEmail validEmail) {
	}

	@Override
	public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {

		return Pattern.compile(VALID_EMAIL_PATTERN).matcher(s).matches();
	}
}
