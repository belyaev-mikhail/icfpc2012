package Vis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/14/12
 * Time: 6:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadField {

    public static String readField() {
        String str = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String s = in.readLine();
            while (s != null) {
                str += s + "\n";
                s = in.readLine();
            }
        } catch (IOException e) {
        }
        System.out.println(str);

        return str;
    }

    public static void main(String[] args) {
        final FieldControl fs = new FieldControl(readField());

        System.out.println(fs);
    }
}
