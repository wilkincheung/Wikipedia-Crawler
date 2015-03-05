# Wikipedia-Crawler

Gracenote Wikipedia Crawler exercise. Java implementation. This is the first draft. Most of the requirements are there, but need some polishing and bug fixes. I plan to finish those this evening.

## Pre-requisites
* Mac OSX or Linux
* Java 7 SDK installed
* Maven installed

## How To Run
Type the following at the project root:
* mvn install

Maven (mvn) would download all dependency third-party libraries automatically. The command "mvn install" runs the main program to fetch list of TV shows from wikipedia. Store in a text file in output directory. For each TV show, fetch the 

## Known Issues
This is my first time writing a crawler app. 
* start date and end date not parse yet. 
* plan to use logging framework slf4j over log4j2
* multi-thread support: The program is now single-threaded, and takes about 1.5 minutes to finish on my Mac OSX. Could use a thread-pool to speed it up




