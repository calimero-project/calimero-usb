Calimero USB provider [![CI with Gradle](https://github.com/calimero-project/calimero-usb/actions/workflows/gradle.yml/badge.svg)](https://github.com/calimero-project/calimero-usb/actions/workflows/gradle.yml)
=============

Calimero-usb provides the KNX USB connection protocol for calimero-core. 
[JDK 17](https://openjdk.org/projects/jdk/17/) (_java.base_) is the minimum required runtime environment.

This implementation uses `org.usb4java:usb4java-javax` to access USB devices, and `System.Logger` for logging.


### Building from source
~~~ sh
git clone https://github.com/calimero-project/calimero-usb.git
cd calimero-usb
./gradlew build # or gradlew.bat on Windows
~~~
