package com.almasb.fxglgames.pong;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * InputValidation validates any inputs sent from the user
 * @author Nael Louis & Daniel Lam
 */
public class InputValidation {
    //Setup a Logger for the class
    private static final Logger LOGGER = Logger.getLogger(InputValidation.class.getName());
    
    /**
     * This method validates the user's input when connecting to a server via IP address
     * @param toValidate
     * @return 
     */
    public static String validateIP(String toValidate) {

        toValidate = Normalizer.normalize(toValidate, Normalizer.Form.NFKC);
        toValidate = Normalizer.normalize(toValidate, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        // Accepts any alphabetical letter, number, "." and ":" for IP. Excludes any other character
        Pattern patternObj = Pattern.compile("[^a-zA-Z0-9.:]");
        Matcher matcherObj = patternObj.matcher(toValidate);
        if (matcherObj.find()) {
            LOGGER.log(Level.SEVERE, "Black listed character found in input");
            throw new IllegalArgumentException();
        }
        return toValidate;
    }
}
