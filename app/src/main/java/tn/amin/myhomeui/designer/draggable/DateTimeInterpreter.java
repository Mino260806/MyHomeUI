package tn.amin.myhomeui.designer.draggable;

import android.icu.text.SimpleDateFormat;

import java.util.Locale;

public class DateTimeInterpreter {
    // TODO DOCS
    public static String interpretLiteral(String literal) {
        int count = 0;
        literal = literal.replace("'", "\0'");
        StringBuilder builder = new StringBuilder();

        builder.append('\'');
        char previousChar = '\0';
        char nextChar = '\0';
        boolean found = false;
        for (int i=0; i<literal.length(); i++) {
            char c = literal.charAt(i);
            if (i > 0) previousChar = literal.charAt(i-1);
            if (i < literal.length()-1) nextChar = literal.charAt(i+1);
            else nextChar = '\0';
            char toAdd = c;
            switch (c) {
                case '{':
                    if (nextChar != '{' && previousChar != '{') {
                        count++;
                        found = true;
                        toAdd = '\'';
                    } else {
                        toAdd = '{';
                    }
                    break;
                case '}':
                    if (nextChar != '}' && previousChar != '}') {
                        count--;
                        toAdd = '\'';
                        if (count < 0)
                            return null;
                    } else {
                        toAdd = '}';
                    }
                    break;
                default:
                    break;
            }
            if (toAdd != '\0') builder.append(toAdd);
        }
        if (count != 0) return null;
        builder.append('\'');

        if (! found) return null;

        String pattern = builder.toString()
                .replace("}}", "}")
                .replace("{{", "{")
                .replace("''", "") // Remove redundant ''
                .replace("\0'", "'"); // Restore original quotes
        // Test whether the pattern is valid
        try {
            new SimpleDateFormat(pattern, Locale.getDefault());
        } catch (IllegalArgumentException ignored) {
            return null;
        }

        return pattern;
    }
}
