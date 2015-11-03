/*
 * Copyright (c) 2015 SwAT for Legrand
 * All rights reserved
 *
 * P2P library for Android. Provides control/monitor functionalities to Legrand Things
 */

package com.legrand.android.p2plib.auth;

import com.legrand.android.p2plib.core.exceptions.P2PExceptionBadFormat;

import org.passay.IllegalCharacterRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import java.util.Arrays;

/**
 * Standard username validator
 */
public class P2PStandardUsernameValidator implements P2PUsernameValidator {

    @Override
    public void checkUsernameFormat(String username) throws P2PExceptionBadFormat {
        WhitespaceRule r1 = new WhitespaceRule();
        LengthRule r2 = new LengthRule(5, 20);
        char illegalChars[] = {'@'};
        IllegalCharacterRule r3 = new IllegalCharacterRule(illegalChars);

        PasswordValidator validator = new PasswordValidator(Arrays.asList(r1, r2, r3));
        PasswordData data = new PasswordData(username);
        RuleResult result = validator.validate(data);
        if (result.isValid())
            return;
        else
            throw new P2PExceptionBadFormat("Bad format for username: " + getExpectedFormatExplaination());
    }

    @Override
    public String getExpectedFormatExplaination() {
        return "length between 5 and 20, no @, " +
                " and no whitespace";
    }
}
