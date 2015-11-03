package concurrent.synchronize;

import java.io.Console;

public class TestSynchronize {
    private static volatile Console cons = null;
    public static Console console() {
        if (cons == null) {
            synchronized (System.class) {
//                cons = sun.misc.SharedSecrets.getJavaIOAccess().console();
            }
        }
        return cons;
    }
}
