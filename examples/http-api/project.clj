(defproject http-api "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [http-kit "2.3.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [clj-http-lite "0.3.0"]
                 [hickory "0.7.1"]]
  :plugins [[io.taylorwood/lein-native-image "0.2.0-SNAPSHOT"]]
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
  :native-image {:graal-bin "/Users/Taylor/Projects/graalvm-1.0.0-rc1/Contents/Home/bin"
                 :opts ["-H:EnableURLProtocols=http"
                        "-H:+ReportUnsupportedElementsAtRuntime" ;; ignore native-image build errors
                        "--no-server" ;; TODO issue with subsequent builds failing on same server
                        "--verbose"]
                 :name "server"}
  :main http-api.core
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}
             :uberjar {:aot :all}})
