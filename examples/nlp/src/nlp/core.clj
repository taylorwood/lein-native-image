(ns nlp.core
  (:require [clojure.string :as cs])
  (:import (edu.stanford.nlp.ling CoreAnnotations$SentencesAnnotation)
           (edu.stanford.nlp.neural.rnn RNNCoreAnnotations)
           (edu.stanford.nlp.pipeline StanfordCoreNLP)
           (edu.stanford.nlp.sentiment SentimentCoreAnnotations$SentimentAnnotatedTree
                                       SentimentCoreAnnotations$SentimentClass)
           (edu.stanford.nlp.trees Tree)
           (edu.stanford.nlp.util TypesafeMap)
           (java.util Properties))
  (:gen-class))

(set! *warn-on-reflection* true)

(defn build-pipeline [annotators]
  (let [props (doto (Properties.)
                (.put "annotators" (cs/join "," (map name annotators))))]
    (StanfordCoreNLP. props true)))

(def default-annotators [:tokenize :ssplit :pos :parse :sentiment])

(defn get-type [^TypesafeMap anns klass] (.get anns klass))

(defn sentence->sentiment-class [s]
  (let [^Tree tree (get-type s SentimentCoreAnnotations$SentimentAnnotatedTree)]
    (RNNCoreAnnotations/getPredictedClass tree)))

(defn sentence->sentiment-label [s]
  (get-type s SentimentCoreAnnotations$SentimentClass))

(defn text->sentences [text ^StanfordCoreNLP nlp]
  (let [annotation (.process nlp text)]
    (get-type annotation CoreAnnotations$SentencesAnnotation)))

(def default-pipeline (build-pipeline default-annotators))

(comment
  (->> (text->sentences "This is so really great? I'm not sure." default-pipeline)
       (map sentence->sentiment-class))
  (->> (text->sentences "This is so really great? I'm not sure." default-pipeline)
       (map sentence->sentiment-label)))

(defn sentiment-index [text]
  (let [sentences (text->sentences text default-pipeline)
        sentiment-classes (map sentence->sentiment-class sentences)]
    (when (seq sentiment-classes)
      (float (/ (apply + sentiment-classes)
                (count sentiment-classes))))))

(defn -main [& args]
  (let [text (if (seq args)
               (apply str args)
               (slurp *in*))]
    (println (sentiment-index text))))
