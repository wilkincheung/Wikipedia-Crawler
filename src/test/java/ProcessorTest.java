import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: wicheung
 * Date: 3/4/15
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessorTest {

    @Test
    public void test_Application_integration_test() {
        new Application();

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
