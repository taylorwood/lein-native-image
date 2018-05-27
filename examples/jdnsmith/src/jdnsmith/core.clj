(ns jdnsmith.core
  "Reads JSON from stdin and writes its EDN representation to stdout."
  (:require [clojure.data.json :as json]
            [clojure.edn :as edn])
  (:gen-class))

(defn -main [& args]
  (prn (json/read *in* :key-fn keyword)))
