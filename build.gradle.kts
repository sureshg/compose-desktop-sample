import org.jetbrains.compose.*
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    idea
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.compose") version "0.4.0-build179"
    id("com.github.ben-manes.versions") version "0.38.0"
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
            useIR = true
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
        gradleVersion = "7.0-rc-2"
        distributionType = Wrapper.DistributionType.ALL
    }

    // Default task
    defaultTasks("clean", "tasks", "--all")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation("app.redwarp.gif:decoder:0.5.1")
    implementation("moe.tlaster:precompose:0.1.1")
    // Icons Packs
    listOf(
        "simple-icons",
        "feather",
        "tabler-icons",
        "eva-icons",
        "font-awesome",
        "octicons",
        "linea",
        "line-awesome",
        "erikflowers-weather-icons",
        "css-gg"
    ).forEach {
        implementation("br.com.devsrsouza.compose.icons.jetbrains:$it-desktop:0.2.0")
    }

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")

    // implementation("com.google.accompanist:accompanist-flowlayout:0.7.0")
    // implementation("com.zachklipp:compose-backstack:0.7.0+alpha04")
    // implementation("com.zachklipp.compose-richtext:richtext-ui:0.2.0")
    // implementation("io.github.chozzle:compose-macos-theme-desktop:0.2.0")
    // https://github.com/ruckustboom/Palette - Material colors
    // https://github.com/app-outlet/karavel  - Navigation
    // com.github.Tlaster.PreCompose:precompose:1.0.0
    // https://github.com/DevSrSouza/svg-to-compose
    // https://github.com/TheMrCodes/Compose-Tab-Component
    // https://github.com/tehras/charts
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
            "-XX:StartFlightRecording:settings=default,filename=/tmp/compose-rec.jfr",
            "-XX:FlightRecorderOptions:stackdepth=256",
            "-Djdk.tracePinnedThreads=full",
            "-Djava.security.egd=file:/dev/./urandom",
            "-Dskiko.fps.enabled=true",
            "-Dskiko.fps.count=200"
            // "skiko.vsync.enabled=false",
            // "-XX:NativeMemoryTracking=summary"
        )
        nativeDistributions {
            targetFormats(Dmg, Exe, Deb)
            packageName = "compose-desktop-sample"
            packageVersion = project.version.toString()
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

            // sourceSets.main.get().resources.srcDirs.first()
            val resRoot = project.file("src/main/resources")

            macOS {
                iconFile.set(resRoot.resolve("icons/icon-mac.icns"))
                setDockNameSameAsPackageName = true
                bundleID = "${project.group}.${project.name}"

                notarization {
                    appleID.set("test.app@example.com")
                    password.set("@keychain:NOTARIZATION_PASSWORD")
                }
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