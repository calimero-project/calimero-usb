Calimero USB provider [![CI with Gradle](https://github.com/calimero-project/calimero-usb/actions/workflows/gradle.yml/badge.svg)](https://github.com/calimero-project/calimero-usb/actions/workflows/gradle.yml)
=============

Calimero-usb provides the KNX USB connection protocol for calimero-core. 
[Java SE 11](https://jdk.java.net/archive/) (_java.base_) is the minimum required runtime environment.

This implementation uses `org.usb4java:usb4java-javax` to access USB devices, and `System.Logger` for logging.

When using this provider in a modularized setup, Java needs to be started with the option `--add-reads io.calimero.usb=ALL-UNNAMED`, because usb4java-javax is not modularized.

### Building from source
~~~ sh
git clone https://github.com/calimero-project/calimero-usb.git
~~~

    ./gradlew build
