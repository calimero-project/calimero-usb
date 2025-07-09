Calimero USB provider [![CI with Gradle](https://github.com/calimero-project/calimero-usb/actions/workflows/gradle.yml/badge.svg)](https://github.com/calimero-project/calimero-usb/actions/workflows/gradle.yml) [![](https://jitpack.io/v/calimero-project/calimero-usb.svg)](https://jitpack.io/#calimero-project/calimero-usb) [![](https://img.shields.io/badge/jitpack-master-brightgreen?label=JitPack)](https://jitpack.io/#calimero-project/calimero-usb/master)
=============

Calimero-usb provides the KNX USB connection protocol for calimero-core. 
[JDK 17](https://openjdk.org/projects/jdk/17/) (_java.base_) is the minimum required runtime environment.

This implementation uses `org.usb4java:usb4java-javax` to access USB devices, and `System.Logger` for logging.

When using this provider in a modularized setup, Java needs to be started with the option `--add-reads io.calimero.usb.provider.javax=ALL-UNNAMED`, because usb4java-javax is not modularized.

### Building from source
~~~ sh
git clone https://github.com/calimero-project/calimero-usb.git
cd calimero-usb
./gradlew build # or gradlew.bat on Windows
~~~
