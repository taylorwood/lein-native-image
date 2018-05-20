(ns leiningen.native-image
  "Builds a native image from project uberjar using GraalVM."
  (:require [clojure.java.io :as io]
            [clojure.string :as cs]
            [leiningen.core.eval :as eval]
            [leiningen.core.main :as main]
            [leiningen.uberjar :refer [uberjar]])
  (:import (java.io File)))

(defn- absolute-path [f & fs]
  (.getAbsolutePath ^File (apply io/file f fs)))

(defn- native-image-path [bin]
  (cond
    (nil? bin)
    "native-image" ;; assumed to be on PATH

    (string? bin)
    (if (cs/ends-with? bin "/native-image")
      bin
      (absolute-path bin "native-image"))

    (keyword? bin)
    (if (= "env" (namespace bin))
      (native-image-path (System/getenv (name bin)))
      (native-image-path (name bin)))

    :else bin))

(defn- build-native-image [native-image-bin jar-path out-path]
  (main/debug "Building native image from uberjar" jar-path "to" out-path)
  (eval/sh native-image-bin
           "-jar" jar-path
           (format "-H:Name=%s" out-path)
           "-H:+ReportUnsupportedElementsAtRuntime"))

(defn native-image
  "Create a native image of your project using GraalVM's native-image."
  [project & _args]
  (let [native-image-path (native-image-path (get-in project [:native-image :graal-bin]))
        out-path (absolute-path
                   (:target-path project)
                   (or (get-in project [:native-image :name])
                       (format "%s-%s" (:name project) (:version project))))
        jar-path (uberjar project)
        exit-code (build-native-image native-image-path jar-path out-path)]
    (if (zero? exit-code)
      (main/info "Created native image" out-path)
      (main/warn "Failed to create native image"))
    (main/exit exit-code)))
