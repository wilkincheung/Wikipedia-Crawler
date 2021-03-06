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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.Assert.*;

/**
 * Junit test for the Crawler app.
 */
public class ProcessorTest {

    /**
     * Full-fledge test. This actually runs the entire program.
     */
    @Test
    public void application_integration_test() {
        new Application();
    }

    @Test
    public void strip_parenthesis() {
        assertEquals("hello world", Util.stripParenthesis("hello world"));
        assertEquals("hello ", Util.stripParenthesis("hello (world)"));
        assertEquals("hello ", Util.stripParenthesis("hello (world) (another)"));
        assertEquals("hello  XX", Util.stripParenthesis("hello ((world))))) XX"));

    }

    @Test
    public void testDatePattern_startDate() {
        String s = "<td>September&#160;22,&#160;1994<span style=\"display:none\">&#160;(<span class=\"bday dtstart published updated\">1994-09-22</span>)</span>&#160;– May&#160;6,&#160;2004";
        Matcher m = Constants.DATE_PATTERN_ISO_8601.matcher(s);

        assertTrue(m.find());
        assertEquals("1994-09-22", m.group(1));
        assertFalse(m.find());
    }

    @Test
    public void testDatePattern_startDate_and_endDate() {
        String s = "<td>September&#160;22,&#160;1994<span style=\"display:none\">&#160;(<span class=\"bday dtstart published updated\">1994-09-22</span>)</span>2000-01-01&#160;– May&#160;6,&#160;2004";
        Matcher m = Constants.DATE_PATTERN_ISO_8601.matcher(s);

        assertTrue(m.find());
        assertEquals("1994-09-22", m.group(1));
        assertTrue(m.find());
        assertEquals("2000-01-01", m.group(1));
    }

    @Test
    public void extract_title() {
        Matcher m = Constants.TITLE_PATTERN.matcher("<a href=\"aaa.com\" title=\"TITLE\"");
        assertTrue(m.find());
        assertEquals("TITLE", m.group(1));
    }

    @Test
    @Ignore
    public void processor__show1() {
        process_one_show("Breaking_Bad");
    }

    @Test
    @Ignore
    public void processor__show2() {
        process_one_show("240-Robert");
    }

    @Test
    @Ignore
    public void processor__show3() {
        process_one_show("15/Love");
    }

    /**
     * Utility method to process a single show
     * @param path wikipedia URI for a show (eg. /wiki/Breaking_Bad)
     */
    private void process_one_show(String path) {
        TVShowProcessor processor = new TVShowProcessor();

        List<String> shows = new ArrayList<String>();
        shows.add(path);

        processor.setShows(shows);
        processor.process();
    }

    @Test
    public void testReplace() {

        assertEquals("15_Love", Util.replaceSpecialCharacterWithUnderscore("15/Love"));
        assertEquals("Hello_World_", Util.replaceSpecialCharacterWithUnderscore("Hello?World!"));
        assertEquals("__Hello_World_", Util.replaceSpecialCharacterWithUnderscore("((Hello)World)"));

    }

    /**
     * Utility method to load file that contains HTML fragment
     */
    private String loadFile(String filename) throws IOException {
        String path = getClass().getClassLoader().getResource(filename).getPath();

        StringBuilder buffer = Util.getFileContentAsUTF8String(path);

        return buffer.toString();
    }
}
