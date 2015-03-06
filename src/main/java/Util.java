/**
 * The MIT License (MIT)

 Copyright (c) 2015 Wilkin Cheung

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.newBufferedReader;

/**
 * Utility methods
 *
 * @author Wilkin Cheung
 */
public class Util {

    // ObjectMapper is thread-safe
    private static final ObjectMapper mapper = new ObjectMapper();


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
        //Files.write(Paths.get(filename), obj.toString().getBytes());
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), obj);
    }

    public static StringBuilder getFileContentAsUTF8String(String path) throws IOException {

        BufferedReader reader = newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);

        String line;
        StringBuilder buffer = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        return buffer;
    }
}
