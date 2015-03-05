import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods
 *
 * @author Wilkin Cheung
 */
public class Util {

    public static String replaceSpecialCharacterWithUnderscore(String in) {
        if (in == null) {
            return null;
        }
        return in.replaceAll("\\W", "_");
    }

    public static List<String> readLinesFromFile(String filepath) {
        try {
            BufferedReader reader =
                    Files.newBufferedReader(
                            FileSystems.getDefault().getPath("", filepath),
                            Charset.defaultCharset());

            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = reader.readLine()) != null)
                lines.add(line);

            return lines;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static void toFile(String filename, Object obj) throws IOException {
        Files.write(Paths.get(filename), obj.toString().getBytes());
    }
}
