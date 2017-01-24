package com.example.shashankreddy.fooddoor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ricky on 2017/1/16.
 */

public class PasswordValidator {

    private Pattern pattern;
    private Matcher matcher;
    // 6 to 10 characters string with at least one digit, one upper case letter, and one lower case letter
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,10})";

    public final String PASSWORD_DISCRIPTION = "Password must be 6 to 10 characters with at least one digit, one upper case letter, and one lower case letter";

    public PasswordValidator()
    {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    /**
     * Validate password with regular expression
     * @param password password for validation
     * @return true valid password, false invalid password
     */
    public boolean validate(final String password)
    {
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
