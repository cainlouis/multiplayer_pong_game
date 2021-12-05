/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.almasb.fxglgames.pong;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Nael Louis & Daniel Lam
 */
public class InputValidation {
    //Setup a Logger for the class
    private static final Logger LOGGER = Logger.getLogger(InputValidation.class.getName());
    
    /**
     * This method validates the input from outside of the project i.e.user input,
     * data from the json.
     * @param toValidate
     * @return 
     */
    public static String validateString(String toValidate) {

        toValidate = Normalizer.normalize(toValidate, Normalizer.Form.NFKC);
        toValidate = Normalizer.normalize(toValidate, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        Pattern patternObj = Pattern.compile("[<>]");
        Matcher matcherObj = patternObj.matcher(toValidate);
        if (matcherObj.find()) {
            LOGGER.log(Level.SEVERE, "Black listed character found in input");
        }
        return toValidate.replaceAll("[^A-Za-z0-9. ]", "");
    }
}
