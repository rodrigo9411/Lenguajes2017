/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenguajes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rodrigo
 */
public class Methods {

    public boolean isACCIONES(String line) {
        return line.equalsIgnoreCase("acciones");
    }

    public boolean isTOKENS(String line) {
        String pattern = "(T|t)(O|o)(K|k)(E|e)(N|n)(S|s)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(line);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTOKEN(String line) {
        String pattern = "(T|t)(O|o)(K|k)(E|e)(N|n)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(line);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isSpace(char chr) {
        String pattern = "\\s";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(String.valueOf(chr));

        if (m.find()) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isDigit(char chr) {
        String pattern = "\\d";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(String.valueOf(chr));

        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

}
