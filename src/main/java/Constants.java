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

import java.util.regex.Pattern;

/**
 * Class that holds all Constants for this project.
 *
 * @author Wilkin Cheung
 */
public class Constants {

    // Wikipedia Base URL
    public static final String WIKIPEDIA_ROOT = "http://en.wikipedia.org";

    public static final String WIKI = "/wiki/";

    // Crawler entry point
    public static final String CRAWLER_URL = WIKIPEDIA_ROOT + WIKI + "List_of_television_programs_by_name";

    // File that temporary holds a list of TV Shows
    public static final String FILE_LIST_OF_TV_SHOWS = "list_of_tv_shows.txt";

    // Json file extension
    public static final String JSON_FILE_EXTENSION = ".json";

    // For limit comparison
    public static final int UNLIMITED = -1;

    // ISO-8601 Date Pattern (ie. YYYY-MM-DD)
    public static final Pattern DATE_PATTERN_ISO_8601 = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})");

    // title Pattern
    public static final Pattern TITLE_PATTERN = Pattern.compile("title=\"(.*)\"");

    // The following are nodeNames
    public static final String TEXT = "#text";
    public static final String SPAN = "span";
    public static final String A = "a";

    // The following are identifiers on TV Show page
    public static final String NO_OF_SEASONS = "No. of seasons";
    public static final String NO_OF_EPISODES = "No. of episodes";
    public static final String COUNTRY_OF_ORIGIN = "Country of origin";
    public static final String STARRING = "Starring";
    public static final String DEVELOPED_BY = "Developed by";
    public static final String CREATED_BY = "Created by";
    public static final String GENRE = "Genre";
    public static final String DIV = "div";

    // Application level parsing identifier
    public static final String HREF = "href";
    public static final String A_HREF = "a[href]";
}
