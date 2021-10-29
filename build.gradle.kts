import org.jetbrains.compose.*
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
  id("com.google.devtools.ksp") version "1.5.31-1.0.0"
  kotlin("jvm") version "1.5.31"
  id("org.jetbrains.compose") version "1.0.0-beta5"
  id("com.github.ben-manes.versions") version "0.39.0"
  id("com.diffplug.spotless") version "5.16.0"
}

group = "dev.suresh"
version = "1.3.0"

kotlin {
  // explicitApi()
  sourceSets.all {
    languageSettings.apply {
      progressiveMode = true
      enableLanguageFeature(LanguageFeature.NewInference.name)
      enableLanguageFeature(LanguageFeature.JvmRecordSupport.name)
      optIn("kotlin.RequiresOptIn")
      optIn("kotlin.ExperimentalStdlibApi")
      optIn("kotlin.ExperimentalUnsignedTypes")
      optIn("kotlin.io.path.ExperimentalPathApi")
      optIn("kotlin.time.ExperimentalTime")
      optIn("kotlinx.serialization.ExperimentalSerializationApi")
      optIn("kotlin.ExperimentalMultiplatform")
      // Compose specific
      optIn("androidx.compose.material.ExperimentalMaterialApi")
      optIn("androidx.compose.animation.ExperimentalAnimationApi")
      optIn("androidx.compose.foundation.ExperimentalFoundationApi")
      optIn("androidx.compose.runtime.ExperimentalComposeApi")
      optIn("androidx.compose.ui.ExperimentalComposeUiApi")
    }
  }
}

spotless {
  val ktlintVersion = "0.41.0"
  val ktlintConfig = mapOf(
    "disabled_rules" to "no-wildcard-imports",
    "insert_final_newline" to "true",
    "end_of_line" to "lf",
    "indent_size" to "2",
  )

  kotlin {
    ktlint(ktlintVersion).userData(ktlintConfig)
    targetExclude("$buildDir/**/*.kt", "bin/**/*.kt")
    endWithNewline()
    indentWithSpaces()
    trimTrailingWhitespace()
    // licenseHeader(rootProject.file("gradle/license-header.txt"))
  }

  kotlinGradle {
    ktlint(ktlintVersion).userData(ktlintConfig)
    target("*.gradle.kts")
  }

  format("misc") {
    target("**/*.md", "**/.gitignore")
    trimTrailingWhitespace()
    endWithNewline()
  }
}

tasks {

  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      verbose = true
      jvmTarget = "16"
      javaParameters = true
      incremental = true
      freeCompilerArgs +=
        listOf(
          "-progressive",
          "-Xjsr305=strict",
          "-Xjvm-default=enable",
          "-Xassertions=jvm",
          "-Xallow-result-return-type",
          "-Xstrict-java-nullability-assertions",
          "-Xgenerate-strict-metadata-version",
          "-Xemit-jvm-type-annotations",
          "-Xjavac-arguments=\"--add-exports java.base/sun.nio.ch=ALL-UNNAMED\"",
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
        )
    }
  }

  // JUnit5
  test { useJUnitPlatform() }

  wrapper {
    gradleVersion = "7.2"
    distributionType = Wrapper.DistributionType.ALL
  }

  // Default task
  defaultTasks("clean", "tasks", "--all")
}

/**
 * Sets the Github Action output as package name and path to use in other steps.
 */
gradle.buildFinished {
  val pkgTasks =
    project.gradle.startParameter.taskNames.filter { it.startsWith("package", ignoreCase = true) }

  val pkgFormat =
    compose.desktop.application.nativeDistributions.targetFormats.firstOrNull { it.isCompatibleWithCurrentOS }
  val nativePkg = buildDir.resolve("compose/binaries").findPkg(pkgFormat?.fileExt)
  val jarPkg = buildDir.resolve("compose/jars").findPkg(".jar")
  nativePkg.ghActionOutput("app_pkg")
  jarPkg.ghActionOutput("uber_jar")
}

fun File.findPkg(format: String?) = when (format != null) {
  true -> walk().firstOrNull { it.isFile && it.name.endsWith(format, ignoreCase = true) }
  else -> null
}

fun File?.ghActionOutput(prefix: String) = this?.let {
  when (System.getenv("GITHUB_ACTIONS").toBoolean()) {
    true -> println(
      """
        ::set-output name=${prefix}_name::${it.name}
        ::set-output name=${prefix}_path::${it.absolutePath}
      """.trimIndent()
    )
    else -> println("$prefix: $this")
  }
}

dependencies {
  implementation(compose.desktop.currentOs)
  implementation(compose.uiTooling)
  implementation("app.softwork:routing-compose:0.0.31")
  implementation("app.redwarp.gif:decoder:0.8.0")

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
    implementation("br.com.devsrsouza.compose.icons.jetbrains:$it:1.0.0")
  }

  testImplementation(kotlin("test-junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

  // com.paligot.kighlighter:kighlighter-compose-desktop:1.0.0-SNAPSHOT
  // implementation("moe.tlaster:precompose:0.2.2")
  // implementation(compose.materialIconsExtended)
  // implementation("com.google.accompanist:accompanist-flowlayout:0.7.0")
  // implementation("com.zachklipp:compose-backstack:0.7.0+alpha04")
  // implementation("com.zachklipp.compose-richtext:richtext-ui:0.2.0")
  // implementation("io.github.chozzle:compose-macos-theme-desktop:0.3.1")
  // implementation("com.alialbaali.kamel:kamel-image:0.2.0")
  // https://github.com/ruckustboom/Palette - Material colors
  // https://github.com/app-outlet/karavel  - Navigation
  // https://github.com/DevSrSouza/svg-to-compose
  // https://github.com/TheMrCodes/Compose-Tab-Component
  // https://github.com/tehras/charts
}

compose.desktop {
  application {
    mainClass = "dev.suresh.gameloop.GameLoopKt"
    args(project.version.toString(), kotlin.coreLibrariesVersion)
    jvmArgs(
      "--show-version",
      "--enable-preview",
      "-Xms48m",
      "-XX:+PrintCommandLineFlags",
      "-XX:+UseZGC",
      "-Xlog:gc*:/tmp/gc.log",
      "-XX:StartFlightRecording:settings=default,filename=/tmp/compose-rec.jfr",
      "-XX:FlightRecorderOptions:stackdepth=256",
      "-Djdk.tracePinnedThreads=full",
      "-Djava.security.egd=file:/dev/./urandom",
      "-Dskiko.fps.enabled=true",
      "-Dskiko.fps.periodSeconds=2.0",
      "-Dskiko.fps.longFrames.show=true",
      // "-Dskiko.renderApi=SOFTWARE",
      // "-Dskiko.vsync.enabled=false",
      // "-XX:NativeMemoryTracking=summary",
    )
    nativeDistributions {
      targetFormats(Deb, Rpm, Dmg, Exe)
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
        "java.net.http"
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

      linux { iconFile.set(resRoot.resolve("icons/icon-linux.png")) }

      windows {
        iconFile.set(resRoot.resolve("icons/icon-windows.ico"))
        menuGroup = "Compose Example"
        // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
        upgradeUuid = "18159785-d967-4CD2-8885-77BFA97CFA9F"
      }
    }
  }
}
