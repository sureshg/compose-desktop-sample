import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm") version "1.4.21-2"
    id("org.jetbrains.compose") version "0.3.0-build146"
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
                    "-parameters",
                    "--add-opens",
                    "java.base/java.util=ALL-UNNAMED"
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
        gradleVersion = "6.8.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    // Default task
    defaultTasks("clean", "tasks", "--all")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("net.redwarp.gif:decoder:0.2.2")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")

    // implementation("com.zachklipp:compose-backstack:0.7.0+alpha04")
    // implementation("io.github.chozzle:compose-macos-theme-desktop:0.2.0")
    // implementation("org.jxmapviewer:jxmapviewer2:2.5")
}

compose.desktop {
    application {
        mainClass = "dev.suresh.gameloop.GameLoopKt"
        args(
            project.version.toString(),
            kotlin.coreLibrariesVersion
        )
        jvmArgs(
            "--show-version",
            "--enable-preview",
            "--add-opens",
            "java.base/java.util=ALL-UNNAMED",
            "-Xms48m",
            "-XX:+PrintCommandLineFlags",
            "-XX:+UseZGC",
            "-Xlog:gc*:/tmp/gc.log",
            "-XX:StartFlightRecording:settings=profile,filename=/tmp/compose-rec.jfr",
            "-XX:FlightRecorderOptions:stackdepth=256",
            "-Djdk.tracePinnedThreads=full",
            "-Djava.security.egd=file:/dev/./urandom"
            // "-XX:NativeMemoryTracking=summary"
        )
        nativeDistributions {
            targetFormats(AppImage, Exe, Deb)
            packageName = "compose-desktop-sample"
            version = project.version.toString()
            description = "Compose desktop playground!"
            copyright = "© 2020 Suresh"
            vendor = "Suresh"
            modules(
                "jdk.jfr",
                "jdk.management.jfr",
                "jdk.management.agent",
                "jdk.crypto.ec",
                "java.xml"
            )

            val resRoot = sourceSets.main.get().resources.srcDirs.first()
            macOS {
                iconFile.set(resRoot.resolve("icons/icon-mac.icns"))
            }

            linux {
                iconFile.set(resRoot.resolve("icons/icon-linux.png"))
            }

            windows {
                iconFile.set(resRoot.resolve("icons/icon-windows.ico"))
                menuGroup = "Compose Example"
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "18159785-d967-4CD2-8885-77BFA97CFA9F"
            }
        }
    }
}
