import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


/**
 * Java Bean
 *
 * @author Wilkin Cheung
 */
public class ShowMetadata {
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
     */

    String filename;

    @JsonProperty("title")
    String title;

    @JsonProperty("genres")
    List<String> genres = new ArrayList<String>();

    @JsonProperty("creators")
    List<String> creators = new ArrayList<String>();

    @JsonProperty("casts")
    List<String> casts = new ArrayList<String>();

    @JsonProperty("countryOfOrigin")
    String countryOfOrigin;

    @JsonProperty("seasons")
    String seasons;

    @JsonProperty("episodes")
    String episodes;

    @JsonProperty("startDate")
    String startDate;

    @JsonProperty("endDate")
    String endDate;

    public String getFilename() {

        if (filename == null) {
            filename = Util.replaceSpecialCharacterWithUnderscore(title);
        }
        return filename + Constants.JSON_FILE_EXTENSION;
    }

    @Override
    public String toString() {
        return "ShowMetadata{" +
                "\ntitle='" + title + '\'' +
                ", \ngenres=" + genres +
                ", \ncreators=" + creators +
                ", \ncasts=" + casts +
                ", \ncountryOfOrigin='" + countryOfOrigin + '\'' +
                ", \nseasons=" + seasons +
                ", \nepisodes=" + episodes +
                ", \nstartDate='" + startDate + '\'' +
                ", \nendDate='" + endDate + '\'' +
                '}';
    }

    /**
     * Should write to file or not?
     * If title is null, then no point writing, because that implies the entire ShowMetadata is invalid.
     *
     * @return true if should write to file; false otherwise
     */
    public boolean shouldWriteToFile() {
        return !(title == null);
    }
}
