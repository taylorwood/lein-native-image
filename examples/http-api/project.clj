(defproject http-api "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [http-kit "2.3.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [clj-http-lite "0.3.0"]
                 [hickory "0.7.1"]]
  :plugins [[io.taylorwood/lein-native-image "0.3.0"]]
  :target-path "target/%s"
  :native-image {:graal-bin "/path/to/graalvm-1.0.0-rc2/Contents/Home/bin"
                 :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                 :opts ["-H:EnableURLProtocols=http"
                        "--report-unsupported-elements-at-runtime" ;; ignore native-image build errors
                        "--initialize-at-build-time"
                        "--no-server" ;; TODO issue with subsequent builds failing on same server
                        "--verbose"]
                 :name "server"}
  :main http-api.core
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}
             :uberjar {:aot :all}})
