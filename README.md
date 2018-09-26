# lein-native-image

A Leiningen plugin for generating [GraalVM](https://www.graalvm.org) native images from your project.

The `lein native-image` command compiles your project then uses GraalVM's
[`native-image`](https://www.graalvm.org/docs/reference-manual/aot-compilation/) to build a native image.

[![Clojars Project](https://img.shields.io/clojars/v/io.taylorwood/lein-native-image.svg)](https://clojars.org/io.taylorwood/lein-native-image)

<sup>For deps.edn projects, try [clj.native-image](https://github.com/taylorwood/clj.native-image).</sup>

## Prerequisites

* This plugin depends on [GraalVM](https://www.graalvm.org/downloads/) to build native images.
* Your project.clj must set a `:main` namespace w/entrypoint and support AOT compilation:
    ```clojure
    :main ^:skip-aot my-app.core
    ```

## Examples

See the [examples](examples) directory for projects that can be compiled to native images with GraalVM:

* [jdnsmith](examples/jdnsmith) - CLI command to read JSON from stdin and write EDN to stdout.
* [http-api](examples/http-api) - Basic HTTP server using Ring, Compojure, http-kit.
* [nlp](examples/nlp) - CLI command to analyze sentiment of text using StanfordNLP. Includes examples of reflection hints and delaying static initialization.

## Usage

1. Configure your project with a custom image name, path to GraalVM's home directory or `native-image` path,
   or `native-image` CLI options:
    ```clojure
    (defproject my-app "0.1.0"
      :plugins [[io.taylorwood/lein-native-image "0.3.0"]]    ;; or in ~/.lein/profiles.clj

      :native-image {:name "my-app"                 ;; name of output image, optional
                     :graal-bin "/path/to/graalvm/" ;; path to GraalVM home, optional
                     :opts ["--verbose"]}           ;; pass-thru args to GraalVM native-image, optional

      ;; optionally set profile-specific :native-image overrides
      :profiles {:test    ;; e.g. lein with-profile +test native-image
                 {:native-image {:opts ["-H:+ReportUnsupportedElementsAtRuntime"
                                        "--verbose"]}}
    
                 :uberjar ;; used by default
                 {:aot :all
                  :native-image {:opts ["-Dclojure.compiler.direct-linking=true"]}}})
    ```

    `:native-image` config keys:
    - `:name` is an optional name for the generated native image. 
    - The `:graal-bin` path can be specified as a string or resolved from an environment variable
      using a keyword e.g. `:env/GRAALVM_HOME`.
      If `:graal-bin` is unspecified, the `GRAALVM_HOME` environment variable will be used by default.
    - `:opts` is an optional vector of arguments to `native-image`; see its
      [documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#image-generation-options) for more.

    Note: task-specific `:native-image` profile will be merged in by default, or the `:uberjar` profile
    if that doesn't exist.

    You can also specify these in your Leiningen user profile `~/.lein/profiles.clj`:
    ```clojure
    {:user {:plugins [[io.taylorwood/lein-native-image "0.3.0"]]
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
    Created native image /path/to/my-app/target/my-app
    ```

1. Execute the native image:
    ```
    ➜ ./target/my-app with optional args
    Hello, World!
    ```

## Caveats

The primary benefits to using a GraalVM native image are faster startup, lower memory requirements,
and smaller distribution footprint (no JDK/JRE required). This doesn't necessarily mean the same code
will _run_ faster than it would on the JVM.

GraalVM and Substrate VM's support for AOT compilation and native images is evolving.
There are [limitations](https://github.com/oracle/graal/blob/master/substratevm/LIMITATIONS.md)
and [unsupported features](https://github.com/oracle/graal/blob/master/substratevm/REFLECTION.md)
you will likely encounter. This release was tested with GraalVM 1.0.0-RC1 EE; some of these notes
may not apply to future releases. At least one AOT issue has been fixed since 1.0.0-RC1, but you
must build Substrate VM locally to get unreleased fixes.

When the `:opts ["-H:+ReportUnsupportedElementsAtRuntime"]` flag is set, some `native-image` build
issues will be deferred as runtime exceptions.
To avoid unexpected errors at runtime, don't use this flag for "production" builds.

You may need to set `:opts ["-H:EnableURLProtocols=http"]` to use HTTP libraries.
HTTPS is currently [unavailable but upcoming](https://github.com/oracle/graal/issues/392#issuecomment-385814898).

Specifying `:jvm-opts ["-Dclojure.compiler.direct-linking=true"]` might allow for better
compile-time optimizations.

This plugin doesn't shutdown GraalVM `native-image` build servers after builds, so that subsequent
builds are slightly faster. You can set `:opts ["--no-server"]` to not spawn a build server at
all, or use GraalVM's `native-image` directly to manage build server(s).

### References

[GraalVM Native Image AOT Compilation](https://www.graalvm.org/docs/reference-manual/aot-compilation/)

[Native Clojure with GraalVM](https://www.innoq.com/en/blog/native-clojure-and-graalvm/)

[Instant Netty Startup using GraalVM](https://medium.com/graalvm/instant-netty-startup-using-graalvm-native-image-generation-ed6f14ff7692) (and [source](https://github.com/cstancu/netty-native-demo))

## Contributing

You'll need Leiningen and GraalVM installed to build and test the plugin.

Issues and PRs are welcome!

## License

Copyright © 2018 Taylor Wood.

Distributed under the MIT License.
