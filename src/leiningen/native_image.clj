(ns leiningen.native-image
  "Builds a native image from project uberjar using GraalVM."
  (:require [clojure.java.io :as io]
            [clojure.string :as cs]
            [leiningen.compile :as compile]
            [leiningen.core.classpath :as classpath]
            [leiningen.core.eval :as eval]
            [leiningen.core.main :refer [debug info warn exit]]
            [leiningen.core.project :as project])
  (:import (java.io File)))

(defn- absolute-path [f & fs]
  (.getAbsolutePath ^File (apply io/file f fs)))

(defn- native-image-path [bin]
  (cond
    (nil? bin)
    "native-image" ;; assumed to be on PATH

    (keyword? bin)
    (if (= "env" (namespace bin))
      (native-image-path (System/getenv (name bin)))
      (native-image-path (name bin)))

    (string? bin)
    (if (cs/ends-with? bin "/native-image")
      bin
      (->> [(io/file bin "bin/native-image")
            (io/file bin "native-image")]
           (filter #(.exists %))
           (first)
           (absolute-path)))

    :else bin))

(defn- build-native-image
  "Executes native-image (bin-path) with opts, specifying a classpath,
   main/entrypoint class, and destination path. Returns native-image exit code."
  [bin-path opts cp main dest]
  (debug "Building native image" dest "with classpath" cp)
  (let [all-args (cond-> []
                   (seq opts) (into opts)
                   dest       (conj (format "-H:Name=%s" dest))
                   cp         (into ["-cp" cp])
                   main       (conj main))]
    (apply eval/sh bin-path all-args)))

(defn native-image
  "Create a native image of your project using GraalVM's native-image."
  [project & _args]
  (compile/compile project :all)
  (let [profile    (or (get-in project [:profiles :native-image])
                       (get-in project [:profiles :uberjar]))
        project    (if profile
                     (project/merge-profiles project [profile])
                     project)
        config     (:native-image project)
        entrypoint (-> (name (:main project))
                       (cs/replace #"\-" "_"))
        dest-path  (absolute-path
                    (:target-path project)
                    (or (:name config)
                        (format "%s-%s" (:name project) (:version project))))
        exit-code  (build-native-image
                    (native-image-path (or (:graal-bin config)
                                           (System/getenv "GRAALVM_HOME")))
                    (:opts config)
                    (->> (classpath/get-classpath project)
                         (filter #(.exists (io/file %)))
                         (cs/join File/pathSeparatorChar))
                    entrypoint
                    dest-path)]
    (if (zero? exit-code)
      (info "Created native image" dest-path)
      (do (warn "Failed to create native image")
          (exit exit-code "native-image failed with exit code" exit-code)))))
