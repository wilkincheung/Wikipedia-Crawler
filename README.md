# Wikipedia-Crawler

Wikipedia Crawler. Java implementation. 

## What does this program do?
The command "mvn install" runs the main program to filter a list of TV shows from wikipedia. See this link: http://en.wikipedia.org/wiki/List_of_television_programs_by_name. Store the list of shows in a text file in output directory. For each TV show, fetch the show metadata (such as title, casts, creators) and saves as JSON file.

NOTE: ```mvn install``` usually uses to install software. I kinda uses this command to run the whole thing for convenience.

## Pre-requisites
* Mac OSX or Linux
* Java 7 SDK installed
* Maven installed
* $JAVA_HOME must be set

### Setting $JAVA_HOME for Mac OSX

    # if this command returns blank line, then run the following line to set JAVA_HOME, before running mvn install
    echo $JAVA_HOME  
    

    JAVA_HOME=/Library/Java/JavaVirtualMachines/{your Java 7 JDK}/Contents/Home
    mvn install

## How To Run
Type the following at the project root:
* mvn install

Maven (mvn) would download all dependency third-party libraries automatically. 

## Known Issues
This is my first time writing a crawler app. 
* Currently, there are fewer than 3,000 shows fetched. I am assuming your filesystem support at least that number of files per directory.

## Future
* Use logging framework slf4j + log4j2 instead of System.out
* Multi-thread support: The program is now single-threaded, and takes about 5 minutes to finish on my Macbook Pro. It could use a thread-pool to speed up the processing.





