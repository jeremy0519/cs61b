package flik;

import java.util.Objects;

/** An Integer tester created by Flik Enterprises.
 * @author Josh Hug
 * */
public class Flik {
    /** @param a Value 1
     *  @param b Value 2
     *  @return Whether a and b are the same */
    public static boolean isSameNumber(Integer a, Integer b) {
        // Integer must be compared with .equals(), since == compares memory address which is not desired
        return Objects.equals(a, b);
    }
}
