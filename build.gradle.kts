import org.jetbrains.compose.*
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    idea
    kotlin("jvm") version "1.4.20"
    id("org.jetbrains.compose") version "0.3.0-build135"
    id("com.github.ben-manes.versions") version "0.36.0"
    // id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "dev.suresh"
version = "1.0.0"

tasks {

    idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }

    withType<JavaCompile>().configureEach {
        options.apply {
            encoding = "UTF-8"
            release.set(15)
            isIncremental = true
            isFork = true
            compilerArgs.addAll(
                listOf(
                    "-Xlint:all",
                    "-parameters"
                )
            )
        }
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            verbose = true
            jvmTarget = "15"
            javaParameters = true
            incremental = true
            freeCompilerArgs += listOf(
                "-progressive",
                "-Xjsr305=strict",
                "-Xjvm-default=enable",
                "-Xassertions=jvm",
                "-Xinline-classes",
                "-Xstring-concat=indy-with-constants",
                "-XXLanguage:+NewInference",
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.ExperimentalStdlibApi",
                "-Xopt-in=kotlin.ExperimentalUnsignedTypes",
                "-Xopt-in=kotlin.time.ExperimentalTime",
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
            )
        }
    }

    // JUnit5
    test {
        useJUnitPlatform()
    }

    wrapper {
        gradleVersion = "6.8-rc-4"
        distributionType = Wrapper.DistributionType.ALL
    }

    // Default task
    defaultTasks("clean", "tasks", "--all")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    // maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    jcenter()
}

dependencies {
    implementation(compose.desktop.currentOs)
    // implementation("org.jxmapviewer:jxmapviewer2:2.5")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

compose.desktop {
    application {
        mainClass = "dev.suresh.gameloop.GameLoopKt"
        jvmArgs(
            "--show-version",
            "--enable-preview",
            "-XX:+PrintCommandLineFlags",
            "-XX:+UseZGC",
            "-XX:StartFlightRecording:settings=profile,filename=/tmp/compose-rec.jfr",
            "-XX:FlightRecorderOptions:stackdepth=256",
            "-XX:NativeMemoryTracking=summary",
            "-Djava.security.egd=file:/dev/./urandom"
        )
        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "compose-desktop-sample"
            version = project.version.toString()
            description = "Compose desktop playground!"
            copyright = "© 2020 Suresh"
            vendor = "Suresh"
            modules("jdk.jfr", "jdk.management.jfr", "jdk.management.agent", "java.xml")
        }
    }
}
