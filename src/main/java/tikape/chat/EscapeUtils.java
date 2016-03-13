package tikape.chat;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lauri
 */
public class EscapeUtils {

    public static final HashMap m = new HashMap();

    static {
        m.put(60, "&lt;");   // < - less-than
        m.put(62, "&gt;");   // > - greater-than
        m.put(59, "&sc;");  // ; - semicolon
    }

    public static String escapeHtml(String str) {

        try {
            StringWriter writer = new StringWriter((int) (str.length() * 1.5));
            escape(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            return null;
        }
    }

    private static void escape(Writer writer, String str) throws IOException {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            int ascii = (int) c;
            String entityName = (String) m.get(ascii);
            if (entityName == null) {
                if (c > 0xFF) {
                    writer.write("&#");
                    writer.write(Integer.toString(c, 10));
                    writer.write(';');
                } else {
                    writer.write(c);
                }
            } else {
                writer.write(entityName);
            }
        }
    }
}
