import java.io.File;
import java.nio.file.Path;

public class Test {
    public static void main(String[] args) {
        String str = "r22-798";

        System.out.println(str.charAt(0));

      //  isDigit = (c >= '0' && c <= '9');

        System.out.println(  Character.isDigit(str.charAt(0)) );
    }
}
