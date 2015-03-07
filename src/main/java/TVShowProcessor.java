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
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Core Parser and processor for TV Show. This is the workhorse.
 * <p/>
 * Convert HTML fragment into Jsoup objects (DOM). Walk the DOM. Extract TV Show metadata. Save each TV Show as
 * JSON to disk.
 *
 * @author Wilkin Cheung
 */
public class TVShowProcessor {


    // List of TV Shows to be processed
    private List<String> shows = new ArrayList<String>();

    // Max number of TV Shows to be processed
    private int limit = Constants.UNLIMITED;

    // Has this class been initialized already?
    private boolean isInitialized = false;

    // Statistics for the processor
    private Stat stat = new Stat();


    /**
     * Constructor
     */
    public TVShowProcessor() {
    }

    /**
     * Overloaded Constructor for JUnit testing
     *
     * @param shows List of shows as String
     */
    public TVShowProcessor(List<String> shows) {
        this.shows = shows;
        this.isInitialized = true;
    }

    /**
     * Utility method to print a debug statement to STDOUT
     *
     * @param s String
     */
    private static void debug(String s) {
        //System.out.println(s);
    }

    /**
     * Utility method to print info to Stdout
     */
    private static void info(Object s) {
        System.out.println(s);
    }

    /**
     * Initialize Show. Optionally read the list of TV Shows from disk.
     */
    private void init() {
        if (shows.isEmpty()) {
            shows = Util.readLinesFromFile(Constants.FILE_LIST_OF_TV_SHOWS);
        }
        this.isInitialized = true;
    }

    /**
     * Main process loop for all TV Shows.
     */
    public void process() {
        if (!isInitialized) {
            init();
        }

        List<String> actualShowList = shows;

        if (actualShowList == null || actualShowList.size() == 0) {
            System.err.println("tvShowFiles is empty");
            System.exit(-1);
        } else {
            info("Number of Shows found: " + actualShowList.size());
        }

        int count = 0;

        stat.startTime = System.currentTimeMillis();

        for (String show : actualShowList) {
            count++;
            if ((limit != Constants.UNLIMITED) && (count > limit)) {
                break;
            }
            String url = Constants.WIKIPEDIA_ROOT + Constants.WIKI + show;
            try {
                ShowMetadata showMetadata = new ShowMetadata();
                showMetadata.filename = Util.replaceSpecialCharacterWithUnderscore(show);
                processShow(url, showMetadata);
            } catch (IOException e) {
                ++stat.errorCount;
                System.err.println("Cannot process url " + url);
                e.printStackTrace();
            }
        }

        info(" Time taken: " + (System.currentTimeMillis() - stat.startTime) / 1000 + " seconds");
        info(" Number of TV Show files created: " + stat.successCount);
        info(" Number of errors: " + stat.errorCount);
    }

    /**
     * Fetch and process a single TV Show.
     *
     * @param url a TV Show URL
     * @throws IOException Fail to fetch from URL
     */
    public void processShow(String url, ShowMetadata showMetadata) throws IOException {

        debug("Fetching url: " + url);

        Document doc = Jsoup.connect(url).get();

        processShow(doc, showMetadata);
    }

    /**
     * Process a Jsoup Document.
     *
     * @param fullDocument Jsoup Document
     * @throws IOException Fail to parse Document
     */
    public void processShow(Document fullDocument, ShowMetadata showMetadata) throws IOException {

        // rely on table.infobox, which is the wikipedia box on the right side on each TV Show page.
        Elements infobox = fullDocument.select("table.infobox");

        Document innerDocument = Jsoup.parseBodyFragment(infobox.html());

        Node rootNode = innerDocument.childNodes().get(0);

        // 0 is <head>
        // 1 is <body>
        Node bodyNode = rootNode.childNodes().get(1);

        try {
            parseOneShow(bodyNode, showMetadata);
        } catch (Throwable e) {
            ++stat.errorCount;
            e.printStackTrace();
            return;
        }

        debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        debug(showMetadata.toString());
        debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");


        if (showMetadata.shouldWriteToFile()) {
            info("=> Writing to file: " + showMetadata.getFilename());
            Util.toFile(showMetadata.getFilename(), showMetadata);
            ++stat.successCount;

            //Util.toStdout(showMetadata);
            debug("done writing to file");
        } else {
            debug("nothing to write. Empty show title");
        }
    }

    /**
     * Parse one TV Show into Java Bean
     *
     * @param bodyNode Jsoup Node
     * @return ShowMetadata object
     */
    public void parseOneShow(Node bodyNode, ShowMetadata showMetadata) {

        List<Node> mainNodes = bodyNode.childNodes();

        for (int i = 0; i < mainNodes.size() - 1; i++) {
            Node currentNode = mainNodes.get(i);

            debug("Node #" + i + "(" + currentNode.nodeName() + "):" + currentNode);

            String nodeName = currentNode.nodeName();

            if (Constants.TEXT.equals(nodeName)) {

                // Set title
                if (!StringUtil.isBlank(currentNode.toString()) && showMetadata.title == null) {
                    showMetadata.title = currentNode.toString();
                }

                // Set genre
                if (Constants.GENRE.equals(currentNode.toString()) && showMetadata.genres.isEmpty()) {
                    showMetadata.genres = Util.stripParenthesis(extractFieldAsList(currentNode, "title"));
                }

                if (Constants.CREATED_BY.equals(currentNode.toString()) && showMetadata.creators.isEmpty()) {
                    showMetadata.creators = Util.stripParenthesis(extractFieldAsList(currentNode, "title"));
                }

                if (Constants.DEVELOPED_BY.equals(currentNode.toString()) && showMetadata.creators.isEmpty()) {
                    showMetadata.creators = Util.stripParenthesis(extractFieldAsList(currentNode, "title"));
                }

                if (Constants.STARRING.equals(currentNode.toString()) && showMetadata.casts.isEmpty()) {
                    showMetadata.casts = Util.stripParenthesis(extractFieldAsList(currentNode, "title"));
                }

                // country of origin can be hyperlink (eg. Canada) OR text (eg. United States)
                if (Constants.COUNTRY_OF_ORIGIN.equals(currentNode.toString()) && showMetadata.countryOfOrigin == null) {
                    List<String> nodeTypes = new ArrayList<String>();
                    nodeTypes.add(Constants.TEXT);
                    nodeTypes.add(Constants.A);

                    showMetadata.countryOfOrigin = extractSingleField(currentNode, nodeTypes);

                    // if countryOfOrigin contains <a> tag then it needs filtering
                    if (!showMetadata.countryOfOrigin.isEmpty() && showMetadata.countryOfOrigin.contains("title=")) {
                        Matcher m = Constants.TITLE_PATTERN.matcher(showMetadata.countryOfOrigin);
                        if (m.find()) {
                            showMetadata.countryOfOrigin = m.group(1);
                        }
                    }
                }


                if (Constants.NO_OF_SEASONS.equals(currentNode.toString()) && showMetadata.seasons == null) {
                    showMetadata.seasons = Util.stripParenthesis(extractSingleField(currentNode, Constants.TEXT));
                }

                if (Constants.NO_OF_EPISODES.equals(currentNode.toString()) && showMetadata.episodes == null) {
                    showMetadata.episodes = Util.stripParenthesis(extractSingleField(currentNode, Constants.TEXT));
                }

                // Original run
                // example: Friends
                //<th scope="row" style="text-align:left">Original run</th>
//                <td>September&#160;22,&#160;1994<span style="display:none">&#160;(<span class="bday dtstart published updated">1994-09-22</span>)</span>&#160;– May&#160;6,&#160;2004<span style="display:none">&#160;(<span class="dtend">2004-05-06</span>)</span></td>
//                </tr>

                //TODO: fix this
                if ("Original run".equals(currentNode.toString()) && showMetadata.startDate == null) {
                    // special case (hack)
                    extractFieldOriginalRun(currentNode, showMetadata);
                }
            }
        }
    }

    /**
     * Extract original run (start date, end date) from a node
     *
     * @param currentNode  Jsoup Node
     * @param showMetadata Java bean
     */
    private void extractFieldOriginalRun(Node currentNode, ShowMetadata showMetadata) {
        Node spanNode = findNextSpanSiblingNode(currentNode);

        if (spanNode == null) {
            return;
        }

        // This is uber hack
        // search for Date pattern YYYY-MM-DD (ISO 8601) inside <span> node
        // first match is start date
        // second match is end date
        Matcher m = Constants.DATE_PATTERN_ISO_8601.matcher(spanNode.toString());

        // TODO: clean this up
        if (m.find()) {
            showMetadata.startDate = m.group(1);

            spanNode = findNextSpanSiblingNode(spanNode);
            if (spanNode != null) {
                m = Constants.DATE_PATTERN_ISO_8601.matcher(spanNode.toString());
                if (m.find()) {
                    showMetadata.endDate = m.group(1);
                }
            }
        }
    }

    /**
     * Find next non-text sibling from Jsoup Node
     *
     * @param currentNode Jsoup Node
     * @return Node as String
     */
    private String extractSingleField(Node currentNode, String nodeType) {
        // Lookup next few nodes, until hitting text node that is not empty
        // Then between currentNode and that text node, look for hyperlink <a> attribute "title"
        // each <a> represents a genre
        List<String> nodeTypes = new ArrayList<String>();
        nodeTypes.add(nodeType);
        Node endNode = findNextNonEmptySiblingNode(currentNode, nodeTypes);

        return endNode.toString();
    }

    private String extractSingleField(Node currentNode, List<String> nodeTypes) {

        Node endNode = findNextNonEmptySiblingNode(currentNode, nodeTypes);

        return endNode.toString();
    }

    /**
     * Find next sibling for fieldName (such as text) from Jsoup Node
     *
     * @param currentNode Jsoup Node
     * @param fieldName   String
     * @return List of Nodes as String
     */
    private List<String> extractFieldAsList(Node currentNode, String fieldName) {

        // save a pointer to currentNode, because the algorithm will alter it, and we have a need to use the original
        Node originalCurrentNode = currentNode;

        List<String> values = new ArrayList<String>();

        // Lookup next few nodes, until hitting text node that is not empty
        // Then between currentNode and that text node, look for hyperlink <a> attribute "title"
        // each <a> represents a genre
        Node endNode = findNextTextSiblingNode(currentNode);

        // go from current index to end index, extract genre
        if (endNode != null && endNode.siblingIndex() > currentNode.siblingIndex()) {
            for (int j = currentNode.siblingIndex(); j < endNode.siblingIndex(); j++) {

                // move pointer to next node
                currentNode = currentNode.nextSibling();

                // value is embedded in hyperlink <a>
                if (Constants.A.equals(currentNode.nodeName())) {
                    values.add(currentNode.attr(fieldName));
                } else if (Constants.DIV.equals(currentNode.nodeName())) {

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
                    //List<Node> mainNodes = bodyNode.childNodes().get(0).childNodes().get(1).childNodes();


                    Node oneUnderDiv = bodyNode.childNodes().get(0);

                    if (oneUnderDiv == null) {
                        continue;
                    }

                    int size = oneUnderDiv.childNodes().size();

                    if (size < 2) {
                        ++stat.skipCount;
                        continue;
                    }

                    Node twoUnderDiv = oneUnderDiv.childNodes().get(1);

                    List<Node>twoUnderDivNodes = twoUnderDiv.childNodes();

                    for (int i = 0; i < twoUnderDivNodes.size(); i++) {

                        //<li><a href="/wiki/Andrew_Lincoln" title="Andrew Lincoln">Andrew Lincoln</a></li>
                        Node liNode = twoUnderDivNodes.get(i);

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

        // if values is still empty, that could mean the field is singular (not list).
        // so search as a singular value
        if (values.isEmpty()) {
            String value = extractSingleField(originalCurrentNode, Constants.TEXT);
            if (value != null && !value.isEmpty()) {
                values.add(value);
            }
        }

        return values;
    }

    /**
     * Find next span sibling node
     *
     * @param currentNode Jsoup Node
     * @return Next span sibling node
     */
    private Node findNextSpanSiblingNode(Node currentNode) {
        List<String> nodeTypes = new ArrayList<String>();
        nodeTypes.add(Constants.SPAN);
        return findNextNonEmptySiblingNode(currentNode, nodeTypes);
    }

    /**
     * Find next text sibling node
     *
     * @param currentNode Jsoup Node
     * @return Next text sibling node
     */
    private Node findNextTextSiblingNode(Node currentNode) {
        List<String> nodeTypes = new ArrayList<String>();
        nodeTypes.add(Constants.TEXT);
        return findNextNonEmptySiblingNode(currentNode, nodeTypes);
    }

    /**
     * Find next non empty sibling node, if findNodeTypes contains more than one value.
     * For example, TEXT and SPAN, then the first value gets hit will win.
     *
     * @param currentNode   Jsoup Node
     * @param findNodeTypes Node types (for example, text or span)
     * @return Jsoup Node if found; null otherwise
     */
    private Node findNextNonEmptySiblingNode(Node currentNode, List<String> findNodeTypes) {

        Node nextNode = currentNode.nextSibling();

        while (nextNode != null) {
            String nodeName = nextNode.nodeName();

            if (findNodeTypes.contains(nodeName) && !StringUtil.isBlank(nextNode.toString())) {
                return nextNode;
            }

            nextNode = nextNode.nextSibling();
        }
        return null;
    }

    /**
     * Getter for list of TV Shows
     *
     * @return List of String
     */
    public List<String> getShows() {
        return shows;
    }

    /**
     * Getter for list of TV Shows
     *
     * @param shows List of String
     */
    public void setShows(List<String> shows) {
        this.shows = shows;
    }

    /**
     * Getter for limit
     *
     * @return int
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Setter for limit
     *
     * @param limit int
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Utility class to store stats.
     */
    private static class Stat {
        int errorCount;
        int successCount;
        int skipCount;
        long startTime;
    }


}
