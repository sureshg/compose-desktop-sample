# Compose Desktop Playground

[![Compose Release][cfd_img]][cfd_url]
[![GitHub Workflow Status][shieldio_img]][gha_url]
[![Kotlin release][kt_img]][kt_url]
[![OpenJDK Version][java_img]][java_url]
[![Style guide][sty_img]][sty_url]

[Jetbrains Compose][0] desktop playground!

```bash
# Build the project
$ ./gradlew clean build

# Create OS specific package
$ ./gradlew clean package

# Run the app
$ ./gradlew clean run

# Run the app image
$ ./gradlew clean runDistributable
```

### Print Java Platform Module deps

```bash
$ ./gradlew suggestRuntimeModules
```

### Troubleshooting

```bash
# Clear all app permission attributes on MacOS. This is required for unsigned app to work on MacOS
$ sudo xattr -cr /Applications/compose-desktop-sample.app

# Stop Gradle daemon after switching the JDK
$ ./gradlew --stop && pkill -f KotlinCompileDaemon

# ARM64 self hosted runner config (Oracle cloud A1 flex)
$ ./config.sh --url https://github.com/sureshg/repo --token xxxxx
./bin/Runner.Listener: /lib64/libstdc++.so.6: version `GLIBCXX_3.4.21' not found (required by ./bin/Runner.Listener)
./bin/Runner.Listener: /lib64/libstdc++.so.6: version `GLIBCXX_3.4.20' not found (required by ./bin/Runner.Listener)

$ sudo yum whatprovides '*libstdc++*' // this provides the oracle-armtoolset location OR "locate libstdc++.so"
$ strings  /opt/oracle/oracle-armtoolset-8/root/usr/lib64/libstdc++.so | grep -i glib

$ export DOTNET_SYSTEM_GLOBALIZATION_INVARIANT=1
$ export LD_LIBRARY_PATH=/opt/oracle/oracle-armtoolset-8/root/usr/lib64:/usr/local/lib:/usr/lib:/usr/local/lib64:/usr/lib64

```

## Tools

- [Compose for Desktop IDE Plugin](https://plugins.jetbrains.com/plugin/16541-compose-for-desktop-ide-support)
- [Compose Modifiers Playground](https://plugins.jetbrains.com/plugin/16417-compose-modifiers-playground)
- [Theme-Configurator](https://github.com/supertechninja/Jetpack-Compose-Theme-Configurator)
- [Intellij Plugin Template](https://github.com/JetBrains/compose-jb/tree/master/examples/intelliJPlugin)

## Resources

- [Compose Doc](https://developer.android.com/jetpack/compose/documentation)
- [Jetpack Compose Source](https://github.com/androidx/androidx/tree/androidx-main/compose)
- [Jetpack Compose AOSP](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/)
- [Compose Release Notes](https://developer.android.com/jetpack/androidx/releases/compose)
- [Compose Awesome](https://github.com/jetpack-compose/jetpack-compose-awesome)

<hr>

 - https://github.com/hfhbd/routing-compose
 - https://github.com/ArTemmey/compose-jb-routing
 - https://github.com/Tlaster/PreCompose
 - https://github.com/arkivanov/Decompose


[0]: https://www.jetbrains.com/lp/compose

[1]: https://filiph.github.io/raytracer/

[2]: https://github.com/filiph/filiphnet/blob/master/tool/spanify.dart

[3]: https://github.com/RayTracing/raytracing.github.io

[github-packages]: https://github.com/sureshg/compose-desktop-sample/packages

[cfd_url]: https://github.com/JetBrains/compose-jb/releases

[cfd_img]: https://img.shields.io/github/v/release/JetBrains/compose-jb?color=3cdc84&include_prereleases&label=Compose%20Desktop&logo=apache-rocketmq&logoColor=3cdc84&style=for-the-badge

[kt_url]: https://github.com/JetBrains/kotlin/releases/latest

[kt_img]: https://img.shields.io/github/v/release/Jetbrains/kotlin?color=7f53ff&label=Kotlin&logo=kotlin&logoColor=7f53ff&style=for-the-badge

[java_url]: https://jdk.java.net/

[java_img]: https://img.shields.io/badge/OpenJDK-jdk--18-ea791d?logo=java&style=for-the-badge&logoColor=ea791d

[gha_url]: https://github.com/sureshg/compose-desktop-sample/actions/workflows/build.yml

[gha_img]: https://github.com/sureshg/compose-desktop-sample/actions/workflows/build.yml/badge.svg?branch=main

[shieldio_img]: https://img.shields.io/github/workflow/status/sureshg/compose-desktop-sample/Compose%20Desktop%20Build/main?color=green&label=Build&logo=Github-Actions&logoColor=green&style=for-the-badge

[sty_url]: https://kotlinlang.org/docs/coding-conventions.html

[sty_img]: https://img.shields.io/badge/style-Kotlin--Official-40c4ff.svg?style=for-the-badge&logo=kotlin&logoColor=40c4ff
