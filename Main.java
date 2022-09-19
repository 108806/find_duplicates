import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class Main {
    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException {

        final String path = Walker.getPath();
        System.out.println("[*] Current Path: " + path);

        Set<String> files = Walker.Walk(path);
        Walker.Hashmaykr FnH = new Walker.Hashmaykr(files);

        var dupes = FnH.findAllFileDupes();
        dupes.forEach(System.out::println);
        System.out.println("[*] The end.");
    }
}

