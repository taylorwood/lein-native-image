# lein-native-image

A Leiningen plugin for generating [GraalVM](https://www.graalvm.org) native images from your project.

The `lein native-image` command compiles your project then uses GraalVM's
[`native-image`](https://www.graalvm.org/docs/reference-manual/aot-compilation/) to build a native image.

[![Clojars Project](https://img.shields.io/clojars/v/io.taylorwood/lein-native-image.svg)](https://clojars.org/io.taylorwood/lein-native-image)

<sup>For deps.edn projects, try [clj.native-image](https://github.com/taylorwood/clj.native-image).</sup>

## Prerequisites

* This plugin depends on [GraalVM](https://www.graalvm.org/downloads/) to build native images.

  **NOTE:** As of GraalVM 19.0.0, `native-image` is no longer included by default:
  > Native Image was extracted from the base GraalVM distribution. Currently it is available as an early adopter plugin. To install it, run: `gu install native-image`. After this additional step, the `native-image` executable will be in the `bin` directory, as for the previous releases.

  ```
  ➜ $GRAALVM_HOME/bin/gu install native-image
  Downloading: Component catalog from www.graalvm.org
  Processing component archive: Native Image
  Downloading: Component native-image: Native Image  from github.com
  Installing new component: Native Image licence files (org.graalvm.native-image, version 19.0.0)
  ```
  
* Your project.clj must set a `:main` namespace w/entrypoint and support AOT compilation:
  ```clojure
  :main ^:skip-aot my-app.core
  ```

## Examples

See the [examples](examples) directory for projects that can be compiled to native images with GraalVM:

* [jdnsmith](examples/jdnsmith) - CLI command to read JSON from stdin and write EDN to stdout.
* [http-api](examples/http-api) - Basic HTTP server using Ring, Compojure, http-kit.
* [nlp](examples/nlp) - CLI command to analyze sentiment of text using StanfordNLP. Includes examples of reflection hints and delaying class initialization.
* [clojurl](https://github.com/taylorwood/clojurl) - cURL-like tool using clojure.spec, HTTPS, hiccup.

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
                 {:native-image {:opts ["--report-unsupported-elements-at-runtime"
                                        "--initialize-at-build-time"
                                        "--verbose"]}}
    
                 :uberjar ;; used by default
                 {:aot :all
                  :native-image {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}})
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
GraalVM Community Edition and Enterprise Edition also have different performance characteristics.

GraalVM's native image capabilities have evolved across many release candidates. Several AOT issues have been fixed since 1.0.0-RC1.
GraalVM and Substrate VM's support for AOT compilation and native images has [limitations](https://github.com/oracle/graal/blob/master/substratevm/LIMITATIONS.md),
and there are [unsupported features](https://github.com/oracle/graal/blob/master/substratevm/REFLECTION.md).
This release and its example projects were tested with GraalVM 19.0.0 Community Edition.

GraalVM 19.0.0 (first non-RC release) changes the default class-initialization behavior of `native-image`.
Now you must specify `--initialize-at-build-time` explicitly in your `native-image` options.

There is a [known issue](https://dev.clojure.org/jira/browse/CLJ-1472) where usages of `clojure.core/locking` macro will fail compilation.
Clojure 1.10 depends on a version of clojure.spec that uses `locking`.
See [this commit](https://github.com/taylorwood/clojurl/commit/12b96b5e9a722b372f153436b1f6827709d0f2ab) for an example workaround.

When the `--report-unsupported-elements-at-runtime` flag is set,
some `native-image` AOT compilation issues will be deferred as runtime exceptions.
You can try specifying this flag if `native-image` compilation fails.
To avoid unexpected errors at runtime, don't use this flag for "production" builds.

Set `--enable-url-protocols=http` to use HTTP libraries.
HTTPS is available as of 1.0.0-RC7 (e.g. `--enable-url-protocols=http,https`)
but [requires additional configuration](https://github.com/oracle/graal/blob/master/substratevm/URL-PROTOCOLS.md#https-support).

Specifying `:jvm-opts ["-Dclojure.compiler.direct-linking=true"]` might allow for better
compile-time optimizations.

This plugin doesn't shutdown GraalVM `native-image` build servers after builds, so that subsequent
builds are slightly faster. You can set `:opts ["--no-server"]` to not spawn a build server at
all, or use GraalVM's `native-image` command directly to manage build server(s).

### References

[GraalVM Native Image AOT Compilation](https://www.graalvm.org/docs/reference-manual/aot-compilation/)

[Native Clojure with GraalVM](https://www.innoq.com/en/blog/native-clojure-and-graalvm/)

[Instant Netty Startup using GraalVM](https://medium.com/graalvm/instant-netty-startup-using-graalvm-native-image-generation-ed6f14ff7692) (and [source](https://github.com/cstancu/netty-native-demo))

## Contributing

You'll need Leiningen and GraalVM installed to build and test the plugin.

Issues and PRs are welcome!

## License

Copyright © 2019 Taylor Wood.

Distributed under the MIT License.
