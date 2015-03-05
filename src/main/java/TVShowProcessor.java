import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main processor
 *
 * @author Wilkin Cheung
 */
public class TVShowProcessor {

    private List<String> shows = new ArrayList<String>();

    private int limit = Constants.UNLIMITED;

    public TVShowProcessor() {
        init();
    }


    private void init() {
        if (shows.isEmpty()) {
            shows = Util.readLinesFromFile(Constants.TV_SHOW_FILE_LOCATION);
        }
    }

    public void process() {
        try {

            List<String> actualShowList = shows;

            if (actualShowList == null || actualShowList.size() == 0) {
                System.err.println("tvShowFiles is empty");
                System.exit(-1);
            } else {
                info("Number of Shows found: " + actualShowList.size());
            }

            int count = 0;
            for (String show : actualShowList) {
                count++;
                if ((limit != Constants.UNLIMITED) && (count > limit)) {
                    break;
                }
                String url = Constants.WIKIPEDIA_ROOT + show;
                processShow(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> getShows() {
        return shows;
    }

    public void setShows(List<String> shows) {
        this.shows = shows;
    }


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private static void debug(String s) {
        //System.out.println(s);
    }

    public static void main(String[] args) {
        new TVShowProcessor();
    }

    /**
     * JSON Format:
     * <p/>
     * {
     * title: "",
     * genre: "",
     * creators: ["name 1", "name 2", ...],
     * cast: ["name 1", "name 2", ...],
     * country_of_origin: "",
     * seasons: 123,
     * episodes: 123,
     * start_date: (ISO-8601 Date String),
     * end_date: (ISO-8601 Date String)
     * }
     *
     * @throws IOException
     */

    public void processShow(String url) throws IOException {

        debug("Fetching url: " + url);
        Document doc = Jsoup.connect(url).get();

        Elements infobox = doc.select("table.infobox");

        Document doc2 = Jsoup.parseBodyFragment(infobox.html());

        Node rootNode = doc2.childNodes().get(0);

        // 0 is <head>
        // 1 is <body>
        Node bodyNode = rootNode.childNodes().get(1);

        ShowMetadata showMetadata = null;
        try {
            showMetadata = parseOneShow(bodyNode);
        } catch (Throwable e) {
            System.err.println("error processing url: " + url);

            e.printStackTrace();

            return;
        }

        debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        debug(showMetadata.toString());
        debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        if (showMetadata.shouldWriteToFile()) {
            info("=> Writing to file " + showMetadata.getFilename() + " for url " + url);
            Util.toFile(showMetadata.getFilename(), showMetadata);
            debug("done writing to file");
        } else {
            debug("nothing to write. Empty show title for url " + url);
        }
    }

    public ShowMetadata parseOneShow(Node bodyNode) {

        ShowMetadata showMetadata = new ShowMetadata();

        List<Node> mainNodes = bodyNode.childNodes();

        for (int i = 0; i < mainNodes.size() - 1; i++) {
            Node currentNode = mainNodes.get(i);

            //debug("Node #" + i + "(" + currentNode.nodeName() + "):" + currentNode);

            String nodeName = currentNode.nodeName();

            if ("#text".equals(nodeName)) {

                // Set title
                if (!StringUtil.isBlank(currentNode.toString()) && showMetadata.title == null) {
                    showMetadata.title = currentNode.toString();
                }

                // Set genre
                if ("Genre".equals(currentNode.toString()) && showMetadata.genres.isEmpty()) {
                    showMetadata.genres = extractFieldAsList(currentNode, "title");
                }

                if ("Created by".equals(currentNode.toString()) && showMetadata.creators.isEmpty()) {
                    showMetadata.creators = extractFieldAsList(currentNode, "title");
                }

                if ("Developed by".equals(currentNode.toString()) && showMetadata.creators.isEmpty()) {
                    showMetadata.creators = extractFieldAsList(currentNode, "title");
                }

                if ("Starring".equals(currentNode.toString()) && showMetadata.casts.isEmpty()) {
                    showMetadata.casts = extractFieldAsList(currentNode, "title");
                }

                if ("Country of origin".equals(currentNode.toString()) && showMetadata.countryOfOrigin == null) {
                    showMetadata.countryOfOrigin = extractSingleField(currentNode);
                }

                if ("No. of seasons".equals(currentNode.toString()) && showMetadata.seasons == null) {
                    showMetadata.seasons = extractSingleField(currentNode);
                }

                if ("No. of episodes".equals(currentNode.toString()) && showMetadata.episodes == null) {
                    showMetadata.episodes = extractSingleField(currentNode);
                }

                // original run
                // example: Friends
                //<th scope="row" style="text-align:left">Original run</th>
//                <td>September&#160;22,&#160;1994<span style="display:none">&#160;(<span class="bday dtstart published updated">1994-09-22</span>)</span>&#160;– May&#160;6,&#160;2004<span style="display:none">&#160;(<span class="dtend">2004-05-06</span>)</span></td>
//                </tr>

                //TODO: fix this
                if ("Original run".equals(currentNode.toString()) && showMetadata.startDate == null) {
                    extractFieldOriginalRun(currentNode);
                }
            }
        }

        return showMetadata;
    }

    private void extractFieldOriginalRun(Node currentNode) {
        debug("IN: extractFieldOriginalRun");
        Node endNode = findNextNonEmptySiblingNode(currentNode);

        debug("  endNode is " + endNode);
    }

    private String extractSingleField(Node currentNode) {
        // Lookup next few nodes, until hitting text node that is not empty
        // Then between currentNode and that text node, look for hyperlink <a> attribute "title"
        // each <a> represents a genre
        Node endNode = findNextNonEmptySiblingNode(currentNode);

        return endNode.toString();
    }

    private List<String> extractFieldAsList(Node currentNode, String fieldName) {

        List<String> values = new ArrayList<String>();

        // Lookup next few nodes, until hitting text node that is not empty
        // Then between currentNode and that text node, look for hyperlink <a> attribute "title"
        // each <a> represents a genre
        Node endNode = findNextNonEmptySiblingNode(currentNode);

        // go from current index to end index, extract genre
        if (endNode != null && endNode.siblingIndex() > currentNode.siblingIndex()) {
            for (int j = currentNode.siblingIndex(); j < endNode.siblingIndex(); j++) {

                // move pointer to next node
                currentNode = currentNode.nextSibling();

                // value is embedded in hyperlink <a>
                if ("a".equals(currentNode.nodeName())) {
                    values.add(currentNode.attr(fieldName));
                }

                if ("div".equals(currentNode.nodeName())) {
                    //debug("IN:div extractFieldAsList()");

                    Document doc = Jsoup.parseBodyFragment(currentNode.outerHtml());

                    //debug("subdoc:" + doc.toString());

                    Node rootNode = doc.childNodes().get(0);

                    // 0 is <head>
                    // 1 is <body>
                    Node bodyNode = rootNode.childNodes().get(1);


                    /*
                     * <div>
                     *   <ul>
                     *      <li>
                     *      <li>
                     */
                    List<Node> mainNodes = bodyNode.childNodes().get(0).childNodes().get(1).childNodes();

                    for (int i = 0; i < mainNodes.size(); i++) {

                        //<li><a href="/wiki/Andrew_Lincoln" title="Andrew Lincoln">Andrew Lincoln</a></li>
                        Node liNode = mainNodes.get(i);

                        //debug(" SIZE " + liNode.childNodes().size());

                        if (liNode.childNodes().size() == 0) {
                            continue;
                        }

                        liNode = liNode.childNode(0);

                        //debug("  InnerNode #" + i + "(" + liNode.nodeName() + "):" + liNode);


                        values.add(liNode.attr(fieldName));
                    }
                }
            }
        }

        return values;
    }

    private Node findNextNonEmptySiblingNode(Node currentNode) {

        Node nextNode = currentNode.nextSibling();

        while (nextNode != null) {
            String nodeName = nextNode.nodeName();

            if ("#text".equals(nodeName) && !StringUtil.isBlank(nextNode.toString())) {
                return nextNode;
            }

            nextNode = nextNode.nextSibling();
        }
        return null;
    }

    /**
     * Print info to Stdout
     */
    private void info(Object s) {
        System.out.println(s);
    }
}