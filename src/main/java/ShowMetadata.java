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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


/**
 * Java Bean that holds TV Show metadata. Also contains Jackson JSON annotation for converting into JSON String.
 *
 * @author Wilkin Cheung
 */
public class ShowMetadata {
    /**
     * JSON Format:
     * <p/>
     * {
     *  title: "",
     *  genre: "",
     *  creators: ["name 1", "name 2", ...],
     *  cast: ["name 1", "name 2", ...],
     *  country_of_origin: "",
     *  seasons: 123,
     *  episodes: 123,
     *  start_date: (ISO-8601 Date String),
     *  end_date: (ISO-8601 Date String)
     * }
     */

    @JsonIgnore
    String filename;

    public String getTitle() {
        return title;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getCreators() {
        return creators;
    }

    public List<String> getCasts() {
        return casts;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin == null ? "" : countryOfOrigin;
    }

    public String getSeasons() {
        return seasons == null ? "" : seasons;
    }

    public String getEpisodes() {
        return episodes == null ? "" : episodes;
    }

    public String getStartDate() {
        return startDate == null ? "" : startDate;
    }

    public String getEndDate() {
        return endDate == null ? "" : endDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setCreators(List<String> creators) {
        this.creators = creators;
    }

    public void setCasts(List<String> casts) {
        this.casts = casts;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public void setSeasons(String seasons) {
        this.seasons = seasons;
    }

    public void setEpisodes(String episodes) {
        this.episodes = episodes;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("title")
    String title;

    @JsonProperty("genre")
    List<String> genres = new ArrayList<String>();

    @JsonProperty("creators")
    List<String> creators = new ArrayList<String>();

    @JsonProperty("cast")
    List<String> casts = new ArrayList<String>();

    @JsonProperty("country_of_origin")
    String countryOfOrigin;

    @JsonProperty("seasons")
    String seasons;

    @JsonProperty("episodes")
    String episodes;

    @JsonProperty("start_date")
    String startDate;

    @JsonProperty("end_date")
    String endDate;



    public String getFilename() {

        if (filename == null) {
            filename = Util.replaceSpecialCharacterWithUnderscore(title);
        }
        return filename + Constants.JSON_FILE_EXTENSION;
    }

    @Override
    public String toString() {
        return "{" +
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
