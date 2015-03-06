import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.Assert.*;


public class ProcessorTest {


    /**
     * Full-fledge test. This actually runs the entire program.
     */
    @Test
    public void test_Application_integration_test() {
        new Application();
    }

    /**
     * Utility method to load file that contains HTML fragment
     */
    private String loadFile(String filename) throws IOException {
        String path = getClass().getClassLoader().getResource(filename).getPath();

        StringBuilder buffer = Util.getFileContentAsUTF8String(path);

        return buffer.toString();
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
    public void testJson() throws IOException {
        ShowMetadata show = new ShowMetadata();
        show.setTitle("Walking DEAD");
        show.setStartDate("3000-01-01");

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(show));

        File f = new File("WD.json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(f, show);
        System.out.println(f.getAbsolutePath());
    }

    @Test
    public void test_original_run() throws IOException {
        String html = loadFile("wd_alternate_original_run.html");

        Document doc = Jsoup.parseBodyFragment(html);

        new TVShowProcessor().processShow(doc);
    }

    @Ignore
    @Test
    public void test_processor__breaking_bad() {
        TVShowProcessor processor = new TVShowProcessor();

        List<String> shows = new ArrayList<String>();
        shows.add("/wiki/Breaking_Bad");

        processor.setShows(shows);
        processor.process();
    }

    @Test
    @Ignore
    public void test_processor__ALL_shows() {
        TVShowProcessor processor = new TVShowProcessor();

        processor.process();
    }

    @Test
    @Ignore
    public void testReplace() {

        assertEquals("15_Love", Util.replaceSpecialCharacterWithUnderscore("15/Love"));
        assertEquals("Hello_World_", Util.replaceSpecialCharacterWithUnderscore("Hello?World!"));
        assertEquals("__Hello_World_", Util.replaceSpecialCharacterWithUnderscore("((Hello)World)"));

    }
}
