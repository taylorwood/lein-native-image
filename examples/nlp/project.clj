(defproject nlp "0.1.0-SNAPSHOT"
  :plugins [[io.taylorwood/lein-native-image "0.3.0"]]
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [edu.stanford.nlp/stanford-corenlp "3.9.1"]
                 [edu.stanford.nlp/stanford-corenlp "3.9.1" :classifier "models"]
                 [org.slf4j/slf4j-nop "1.7.12"]]
  :main ^:skip-aot nlp.core
  :native-image {:name "nlp"
                 :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                 :opts ["-H:ReflectionConfigurationFiles=reflection.json"
                        "--delay-class-initialization-to-runtime=edu.stanford.nlp.trees.international.pennchinese.ChineseEnglishWordMap$SingletonHolder,edu.stanford.nlp.process.WordShapeClassifier$DistributionalClusters"]}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
