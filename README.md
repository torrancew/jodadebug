# jodadbg

A webapp for debugging the Logstash date filter

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

To produce a runnable archive, run:

    lein uberjar

The resulting jar will be directly runnable with no dependencies other than a JRE:

    java -jar target/uberjar/jodadbg-standalone.jar

## License

Copyright © 2016 Tray Torrance <torrancew@gmail.com>
