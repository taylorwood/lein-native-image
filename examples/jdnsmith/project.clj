(defproject jdnsmith "0.1.0-SNAPSHOT"
  :plugins [[io.taylorwood/lein-native-image "0.2.0"]]
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.json "0.2.6"]]
  :main jdnsmith.core
  :native-image {:graal-bin :env/GRAALVM_HOME
                 :opts ["--verbose"]
                 :name "jdn"}
  :profiles {:uberjar {:aot :all}})
