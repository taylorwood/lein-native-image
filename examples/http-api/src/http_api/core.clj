(ns http-api.core
  (:require
   [http-api.handler :as handler]
   [org.httpkit.server :as srv])
  (:import (sun.util.logging PlatformLogger PlatformLogger$Level))
  (:gen-class))

(def ^:private logger
  "This throwaway static/compile-time invocation is a workaround for runtime
   reflection of sun.util.logging.{LoggingSupport|PlatformLogger}."
  (.isLoggable (PlatformLogger/getLogger "dummy") PlatformLogger$Level/ALL))

(defn -main [& args]
  (println "Hello, Web!")
  (srv/run-server #'handler/app {:port 3000}))
