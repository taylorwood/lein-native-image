(defproject nlp "0.1.0-SNAPSHOT"
  :plugins [[io.taylorwood/lein-native-image "0.3.1"]]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.9.1"]
                 [edu.stanford.nlp/stanford-corenlp "3.9.1" :classifier "models"]
                 [org.slf4j/slf4j-nop "1.7.12"]]
  :main ^:skip-aot nlp.core
  :native-image {:name "nlp"
                 :opts ["-H:ReflectionConfigurationFiles=reflection.json"
                        "--report-unsupported-elements-at-runtime"
                        "--initialize-at-run-time=edu.stanford.nlp.trees.international.pennchinese.ChineseEnglishWordMap$SingletonHolder,edu.stanford.nlp.process.WordShapeClassifier$DistributionalClusters"
                        "--initialize-at-build-time"]}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :native-image {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
