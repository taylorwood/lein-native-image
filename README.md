# lein-native-image

A Leiningen plugin for generating GraalVM native images from your project.

The `lein native-image` command builds an uberjar from your project, then uses GraalVM's [`native-image`](https://www.graalvm.org/docs/reference-manual/aot-compilation/) to build a native image from the uberjar.

[![Clojars Project](https://img.shields.io/clojars/v/io.taylorwood/lein-native-image.svg)](https://clojars.org/io.taylorwood/lein-native-image)

## Prerequisites

* This plugin depends on [GraalVM](https://www.graalvm.org/downloads/) to build native images.
* Your project.clj must specify a `:main` namespace w/entrypoint and support AOT compilation.
    ```clojure
    (defproject my-app "0.1.0-SNAPSHOT"
      :main ^:skip-aot app.core
      :profiles {:uberjar {:aot :all}})
    ```

## Usage

1. Put `[io.taylorwood/lein-native-image "0.1.0"]` into the `:plugins` vector of your project.clj, or in your Leiningen user profile.

1. Optionally specify a custom image name and/or path to GraalVM's `bin` directory or `native-image` path in your project.clj:
    ```clojure
    (defproject my-app "0.1.0-SNAPSHOT"
      :native-image {:name "optional_custom_image_name"
                     :graal-bin "/path/to/graalvm-1.0.0-rc1/bin"})
    ```
    
    The `:graal-bin` path can also be resolved from an environment variable using a keyword e.g. `:env/GRAAL_HOME`.
    
    If `:graal-bin` is unspecified, GraalVM's `native-image` is assumed to be on your PATH.

1. Build a native image from your project:

    ```
    $ lein native-image
    Compiling my-app.core
    Created /path/to/my-app/target/my-app-0.1.0-SNAPSHOT.jar
    Created /path/to/my-app/target/my-app-0.1.0-SNAPSHOT-standalone.jar
    Build on Server(pid: 24379, port: 26681)
       classlist:     532.79 ms
           (cap):   1,434.35 ms
           setup:   1,701.49 ms
      (typeflow):   2,467.03 ms
       (objects):   1,044.98 ms
      (features):      30.09 ms
        analysis:   3,618.56 ms
        universe:     196.60 ms
         (parse):     340.95 ms
        (inline):     547.67 ms
       (compile):   2,678.98 ms
         compile:   3,880.85 ms
           image:     572.62 ms
           write:     879.95 ms
         [total]:  11,423.27 ms
    Created native image /path/to/my-app/target/my-app-0.1.0-SNAPSHOT-standalone
    ```

1. Execute the native image:
    ```
    $ ./target/my-app-0.1.0-SNAPSHOT-standalone with optional args
    Hello, World!
    ```

## Notes

`-H:+ReportUnsupportedElementsAtRuntime` flag is used with `native-image`, causing some native image build issues to be deferred as runtime exceptions.

## License

Copyright © 2018 Taylor Wood.

Distributed under the MIT License.
