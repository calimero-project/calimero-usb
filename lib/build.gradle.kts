
plugins {
    `java-library`
    `maven-publish`
    `signing`
    `eclipse`
    id("com.github.ben-manes.versions") version "0.44.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

ext["junitJupiterVersion"] = "5.9.1"
ext["desc"] = "Calimero USB service provider"

group = "io.calimero"
version = "2.6-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }

    withSourcesJar()
    withJavadocJar()
}

tasks.compileJava { options.encoding = "UTF-8" }
tasks.compileTestJava { options.encoding = "UTF-8" }
tasks.javadoc { options.encoding = "UTF-8" }


tasks.compileJava { 
    options.compilerArgs = listOf("-Xlint:all,-serial", "--limit-modules", "java.base")
}

tasks.withType<Jar> {
	from("${projectDir}/../LICENSE") {
        into("META-INF")
    }
    if (name == "sourcesJar") {
    	from("${projectDir}/../README.md")
    }
    archiveBaseName.set(rootProject.name)
}

dependencies {
    api("com.github.calimero:calimero-core:$version")
    api("org.usb4java:usb4java-javax:1.3.0")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.9.1")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["java"])
            pom {
                name.set("Calimero USB service provider")
                description.set("USB communication provider using org.usb4java:usb4java-javax")
                url.set("https://github.com/calimero-project/calimero-usb")
                inceptionYear.set("2006")
                licenses {
                    license {
                        name.set("GNU General Public License, version 2, with the Classpath Exception")
                        url.set("LICENSE")
                    }
                }
                developers {
                    developer {
                        name.set("Boris Malinowsky")
                        email.set("b.malinowsky@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/calimero-project/calimero-usb.git")
                    url.set("https://github.com/calimero-project/calimero-usb.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "maven"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    if (project.hasProperty("signing.keyId")) {
        sign(publishing.publications["mavenJava"])
    }
}
