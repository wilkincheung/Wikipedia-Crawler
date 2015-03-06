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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;


/**
 * Crawler App entry point
 *
 * @author Wilkin Cheung
 */
public class Application {

    /**
     * Constructor
     */
    public Application() {
        readFromCrawlerUrl();

        debug("Processing shows...");
        new TVShowProcessor().process();
        debug("Done processing shows...");
    }

    /**
     * Connect to Wikipedia URL, fetch and parse its HTML, write a list of TV Shows to disk
     */
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

    /**
     * Debug utility for this class
     * @param s Object
     */
    private void debug(Object s) {
        System.out.println(s);
    }
}
