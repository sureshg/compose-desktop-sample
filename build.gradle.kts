import org.gradle.jvm.tasks.Jar
import org.jetbrains.compose.*
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import org.jetbrains.kotlin.gradle.tasks.*
import java.io.*
import java.util.spi.*

plugins {
    idea
    kotlin("jvm") version "1.4.31"
    id("org.jetbrains.compose") version "0.3.2"
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
        gradleVersion = "6.8.3"
        distributionType = Wrapper.DistributionType.ALL
    }

    /**
     * A task to print all the module dependencies of the compose application.
     */
    val printModuleDeps by creating {
        this.description = ""
        this.group = ""
        doLast {
            val uberJar = named("packageUberJarForCurrentOS", Jar::class)
            val jarFile = uberJar.get().archiveFile.get().asFile

            val jdeps = ToolProvider.findFirst("jdeps").orElseGet { error("") }
            val out = StringWriter()
            val pw = PrintWriter(out)
            jdeps.run(pw, pw, "--print-module-deps", "--ignore-missing-deps", jarFile.absolutePath)

            val modules = out.toString()
            println(modules)
            // compose.desktop.application.nativeDistributions.modules.addAll(modules.split(","))
        }
        dependsOn("packageUberJarForCurrentOS")
    }

    // Default task
    defaultTasks("clean", "tasks", "--all")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation("app.redwarp.gif:decoder:0.4.0")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")

    // implementation("com.zachklipp:compose-backstack:0.7.0+alpha04")
    // implementation("io.github.chozzle:compose-macos-theme-desktop:0.2.0")
    // https://github.com/ruckustboom/Palette - Material colors
    // https://github.com/app-outlet/karavel  - Navigation
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