# Compose Desktop Playground

[![Compose Release](https://img.shields.io/github/v/release/JetBrains/compose-jb?color=orange&include_prereleases&label=latest%20build&logo=apache-rocketmq&logoColor=orange&style=for-the-badge)](https://github.com/JetBrains/compose-jb/releases)

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
# Stop Gradle daemon after switching the JDK
$ ./gradlew --stop && pkill -f KotlinCompileDaemon

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

[0]: https://www.jetbrains.com/lp/compose
[1]: https://filiph.github.io/raytracer/
[2]: https://github.com/filiph/filiphnet/blob/master/tool/spanify.dart
[3]: https://github.com/RayTracing/raytracing.github.io
