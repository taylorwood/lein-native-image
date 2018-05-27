(defproject jdnsmith "0.1.0-SNAPSHOT"
  :plugins [[io.taylorwood/lein-native-image "0.2.0"]]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot jdnsmith.core
  :native-image {:graal-bin "/path/to/graalvm-1.0.0-rc1/Contents/Home/bin/native-image"
                 :opts ["--verbose"]
                 :name "jdn"}
  :profiles {:uberjar {:aot :all}})
