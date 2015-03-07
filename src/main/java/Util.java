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

    // Jackson ObjectMapper is thread-safe, so make it static for sharing among threads
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Replace non-word character (alphanumeric) with underscore
     * @param in input String
     * @return converted String
     */
    public static String replaceSpecialCharacterWithUnderscore(String in) {
        if (in == null) {
            return null;
        }
        return in.replaceAll("\\W", "_");
    }

    /**
     * Read a file from disk as a list of String
     * @param filepath path on disk
     * @return List of String
     */
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

    /**
     * Write JSON object to disk with Jackson writer.
     * @param filename path on disk
     * @param obj JSON object with Jackson annotation
     * @throws IOException Fail to write to disk
     */
    public static void toFile(String filename, Object obj) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), obj);
    }

    public static void toStdout(Object obj) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, obj);
    }

    /**
     * Get file content as UTF-8 string
     * @param path path on disk
     * @return StringBuilder (for later append)
     * @throws IOException Fail to read from disk
     */
    public static StringBuilder getFileContentAsUTF8String(String path) throws IOException {

        BufferedReader reader = newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);

        String line;
        StringBuilder buffer = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        return buffer;
    }

    public static List<String> stripParenthesis(List<String> inputs) {
        List<String> outputs = new ArrayList<String>();
        for (String in : inputs) {
            outputs.add(stripParenthesis(in));
        }
        return outputs;
    }

    /**
     * Strip all parenthesis and everything inclusive
     * @param in String
     * @return converted String without parenthesis
     */
    public static String stripParenthesis(String in) {
        return in.replaceAll("\\(.*\\)", "");
    }
}
