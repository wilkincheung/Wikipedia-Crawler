import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;


/**
 * Main entry point for this Crawler App.
 *
 * @author Wilkin Cheung
 */
public class Application {

    public Application() {
        readFromCrawlerUrl();

        debug("Processing shows...");
        new TVShowProcessor().process();
        debug("Done processing shows...");
    }

    public void readFromCrawlerUrl() {
        try {
            Document doc = Jsoup.connect(Constants.CRAWLER_URL).get();

            // fetches all links
            Elements elements = doc.select("a[href]");

            debug("Total #links: " + elements.size());

            // I am guessing the proper TV show contains /wiki/ in URL
            // This is a hack!
            Iterator<Element> iterator = elements.iterator();

            StringBuilder builder = new StringBuilder();

            while (iterator.hasNext()) {
                Element element = iterator.next();
                String showId = element.attr("href");
                if (showId.startsWith("/wiki/")) {
                    debug(showId);
                    builder.append(showId).append("\n");
                }
            }


            debug("Writing to file ...");

            Files.write(Paths.get(Constants.FILE_LIST_OF_TV_SHOWS), builder.toString().getBytes());

            debug("Done writing to file...");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exiting program. Cannot load link: " + Constants.CRAWLER_URL);
            System.exit(-1);
        }
    }

    private void debug(Object s) {
        System.out.println(s);
    }

    public static void main(String[] args) {

        new Application();
    }

}
