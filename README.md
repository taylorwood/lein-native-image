# lein-native-image

A Leiningen plugin for generating [GraalVM](https://www.graalvm.org) native images from your project.

The `lein native-image` command compiles your project then uses GraalVM's [`native-image`](https://www.graalvm.org/docs/reference-manual/aot-compilation/) to build a native image.

[![Clojars Project](https://img.shields.io/clojars/v/io.taylorwood/lein-native-image.svg)](https://clojars.org/io.taylorwood/lein-native-image)

## Prerequisites

* This plugin depends on [GraalVM](https://www.graalvm.org/downloads/) to build native images.
* Your project.clj must specify a `:main` namespace w/entrypoint and support AOT compilation.
  Here's an example:
    ```clojure
    (defproject my-app "0.1.0"
      :dependencies [[org.clojure/clojure "1.9.0"]]
      :plugins [[io.taylorwood/lein-native-image "0.2.0"]]
      :native-image {:graal-bin "/path/to/graalvm-1.0.0-rc1/Contents/Home/bin"
                     :name "my-app"}
      :main ^:skip-aot my-app.core
      :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}
                 :test ;; e.g. lein with-profile +test native-image
                 {:native-image {:opts ["-H:+ReportUnsupportedElementsAtRuntime"
                                        "--verbose"]}}
                 :uberjar {:aot :all
                           :native-image {:opts ["-Dclojure.compiler.direct-linking=true"]}}})
    ```

## Usage

See the [examples](https://github.com/taylorwood/lein-native-image/tree/master/examples) directory for apps
that can be compiled to native images with GraalVM.

1. Put `[io.taylorwood/lein-native-image "0.2.0"]` into the `:plugins` vector of your project.clj, or in your Leiningen user profile.

1. Optionally specify a custom image name, path to GraalVM's `bin` directory or `native-image` path, or `native-image` CLI options in your project.clj:
    ```clojure
    (defproject my-app "0.1.0"
      :native-image {:name "my-app"
                     :graal-bin "/path/to/graalvm-1.0.0-rc1/bin"
                     :opts ["--verbose"]})
    ```

    - `:name` is an optional name for the generated native image. 
    - The `:graal-bin` path can also be resolved from an environment variable using a keyword e.g. `:env/GRAALVM_HOME`.
    - If `:graal-bin` is unspecified, GraalVM's `native-image` is assumed to be on your PATH.
    - `:opts` is an optional vector of arguments to `native-image`; see its [documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#image-generation-options) for more.

    The plugin will merge the task-specific profile `:native-image`, or `:uberjar` if that doesn't exist.

    You can also specify these in your Leiningen user profile `~/.lein/profiles.clj`:
    ```clojure
    {:user {:plugins [[io.taylorwood/lein-native-image "0.2.0"]]
            :native-image {:graal-bin "/path/to/graalvm-1.0.0-rc1/Contents/Home/bin"}}}
    ```

1. Build a native image from your project:
    ```
    ➜ lein native-image
    Compiling my-app.core
    Build on Server(pid: 36212, port: 26681)
       classlist:     332.89 ms
           (cap):   1,289.90 ms
       8<----------------------
           write:     932.61 ms
         [total]:  11,789.08 ms
    Created native image /Users/Taylor/Projects/my-app/target/my-app
    ```

1. Execute the native image:
    ```
    ➜ ./target/my-app with optional args
    Hello, World!
    ```

## Notes

GraalVM and Substrate VM's support for AOT compilation and native images is evolving.
There are [limitations](https://github.com/oracle/graal/blob/master/substratevm/LIMITATIONS.md) and [unsupported features](https://github.com/oracle/graal/blob/master/substratevm/REFLECTION.md) you will likely encounter.
This release was tested with GraalVM 1.0.0-RC1 EE; some of these notes may not apply to future releases.
At least one AOT issue has been fixed since 1.0.0-RC1, but you must build Substrate VM locally to get unreleased fixes.

When the `:opts ["-H:+ReportUnsupportedElementsAtRuntime"]` flag is set, some `native-image` build issues will be deferred as runtime exceptions.
To avoid spurious runtime errors, don't use this flag for "production" builds.

You may need to specify `:opts ["-H:EnableURLProtocols=http"]` to use HTTP libraries.
_HTTPS is currently in development and unsupported!_

Specifying `:jvm-opts ["-Dclojure.compiler.direct-linking=true"]` might allow for better optimizations by GraalVM.

This plugin doesn't shutdown GraalVM `native-image` build servers after builds, so that subsequent builds are slightly faster.
You can specify `:opts ["--no-server"]` to not spawn a build server at all, or use other `native-image` commands to manage build server(s).

### References

[GraalVM Native Image AOT Compilation](https://www.graalvm.org/docs/reference-manual/aot-compilation/)

[Native Clojure with GraalVM](https://www.innoq.com/en/blog/native-clojure-and-graalvm/)

[Instant Netty Startup using GraalVM](https://medium.com/graalvm/instant-netty-startup-using-graalvm-native-image-generation-ed6f14ff7692) (and [source](https://github.com/cstancu/netty-native-demo))

## Contributing

You'll need Leiningen and GraalVM installed to build and test the plugin.

Issues, PRs, and suggestions are welcome!

## License

Copyright © 2018 Taylor Wood.

Distributed under the MIT License.
