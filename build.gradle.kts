import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
    `maven-publish`
    signing
    eclipse
    id("com.github.ben-manes.versions") version "0.52.0"
}

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    mavenLocal()
}

group = "io.calimero"
version = "3.0-M1"

val junitJupiterVersion by rootProject.extra { "5.13.2" }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    withSourcesJar()
    withJavadocJar()
}

tasks.compileJava { options.encoding = "UTF-8" }
tasks.compileTestJava { options.encoding = "UTF-8" }
tasks.javadoc { options.encoding = "UTF-8" }

tasks.compileJava {
    options.compilerArgs = listOf("-Xlint:all,-serial",
        "--limit-modules", "io.calimero.core",
        "--add-reads", "io.calimero.usb.provider.javax=ALL-UNNAMED")
}

tasks.compileJava {
    options.javaModuleVersion.set(provider { project.version.toString() })
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addStringOption("-add-reads", "io.calimero.usb.provider.javax=ALL-UNNAMED")
}

tasks.withType<Jar> {
	from("${projectDir}/LICENSE") {
        into("META-INF")
    }
    if (name == "sourcesJar") {
    	from("${projectDir}/README.md")
    }
    archiveBaseName.set(rootProject.name)
}

dependencies {
    api("io.calimero:calimero-core:$version")
    implementation("org.usb4java:usb4java-javax:1.3.0")

    testRuntimeOnly("org.slf4j:slf4j-jdk-platform-logging:2.0.17")
	testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("${rootProject.extra.get("junitJupiterVersion")}")
        }
    }
}

tasks.test {
    testLogging {
        events("failed") // "standardOut", "passed"
        exceptionFormat = TestExceptionFormat.FULL
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
            val releasesRepoUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    if (project.hasProperty("signing.keyId")) {
        sign(publishing.publications["mavenJava"])
    }
}
