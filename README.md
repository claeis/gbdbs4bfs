# gbdbs4bfs - reduces a GBDBS transfer file

## Features
- converts a full GBDBS file to a reduced, still valid, and for BfS requirements complete one

## License
gbdbs4bfs is licensed under the LGPL (Lesser GNU Public License).

## System Requirements
For the current version of gbdbs4bfs, you will need a JRE (Java Runtime Environment) installed on your system, version 1.8 or later.
The JRE (Java Runtime Environment) can be downloaded for free from the Website <http://www.java.com/>.

## Software Download 
<http://http://jars.umleditor.org/ch/ehi/gbdbs4bfs>

## Installing gbdbs4bfs
To install the gbdbs4bfs, choose a directory and extract the distribution gbdbs4bfs-{version}-bin.zip file there. 

## Running gbdbs4bfs
The gbdbs4bfs tool can be started with

    java -jar gbdbs4bfs.jar [--trace] [--log logfile.txt] full-gbdbs.xml reduced-gbdbs.xml

## Building from source
To build the `gbdbs4bfs.jar`, use

    gradle build

To build a binary distribution, use

    gradle bindist


