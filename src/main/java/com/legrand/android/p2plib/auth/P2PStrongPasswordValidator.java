/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth;

import com.legrand.android.p2plib.exceptions.P2PExceptionBadFormat;

import org.passay.CharacterCharacteristicsRule;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import java.util.Arrays;

/**
 * Strong password validator
 * - Length of 8 to 16 characters
 * - Must contain characters from at least 3 of the following: upper, lower, digit, symbol
 * - No whitespace characters
 */
public class P2PStrongPasswordValidator implements P2PPasswordValidator{

    @Override
    public void checkPasswordStrength(String password) throws P2PExceptionBadFormat {
        LengthRule r1 = new LengthRule(8, 16);

        CharacterCharacteristicsRule r2 = new CharacterCharacteristicsRule();

        r2.setNumberOfCharacteristics(3);

        r2.getRules().add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        r2.getRules().add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        r2.getRules().add(new CharacterRule(EnglishCharacterData.Digit, 1));
        r2.getRules().add(new CharacterRule(EnglishCharacterData.Special, 1));

        WhitespaceRule r3 = new WhitespaceRule();

        PasswordValidator validator = new PasswordValidator(Arrays.asList(r1, r2, r3));
        PasswordData passwordData = new PasswordData(password);
        RuleResult result = validator.validate(passwordData);
        if (result.isValid())
            return;
        else
            throw new P2PExceptionBadFormat("Bad format for password: " + getExpectedFormatExplaination());
    }

    @Override
    public String getExpectedFormatExplaination() {
        return "length between 8 and 16, " +
               "chars from at least 3 of the following: upper, lower, digit, symbol " +
               " and no whitespace";
    }
}
