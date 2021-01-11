# Compose Desktop Playground

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

### Find JPackage modules for the App

```bash
$ ./gradlew clean packageUberJarForCurrentOS

# Use it in modules() section.
$ jdeps --ignore-missing-deps --print-module-deps build/compose/jars/compose-desktop-sample-macos-x64-1.0.0.jar
```

### Troubleshooting

```bash
# Stop Gradle daemon after switching the JDK
$ ./gradlew --stop

```
## Misc 

 - [Jetpack Compose Source](https://github.com/androidx/androidx/tree/androidx-main/compose)
 - [Jetpack Compose AOSP](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/)
 - [Compose Release Notes](https://developer.android.com/jetpack/androidx/releases/compose)
 - [Compose Awesome](https://github.com/jetpack-compose/jetpack-compose-awesome)  
 - [Flappy Bird](https://elye-project.medium.com/android-jetpack-compose-flappy-bird-9ac4b1d223df)

[0]: https://www.jetbrains.com/lp/compose
[1]: https://filiph.github.io/raytracer/
[2]: https://github.com/filiph/filiphnet/blob/master/tool/spanify.dart
[3]: https://github.com/RayTracing/raytracing.github.io