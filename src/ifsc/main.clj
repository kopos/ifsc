(ns ifsc.main
  (:require
    [clojure.tools.logging :as log]
    [ring.logger :as logger]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
    [org.httpkit.server :as http]
    [environ.core :refer [env]]
    [com.pekaplay.math :refer [->int]]
    [ifsc.core :as ifsc]
    [ifsc.routes :refer [routes]])
  (:gen-class))

(defn wrap-exception
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (log/error handler request (with-out-str (.printStackTrace e)))
        {:status 500 :body (.getMessage e)}))))

(def app
  (-> routes
      (wrap-defaults site-defaults)
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-exception)))

(defonce server (atom nil))

(defn mk-server [port]
  (http/run-server (logger/wrap-with-logger #'app) {:port port}))

(defn start-server [port]
  (ifsc/init)
  (when (nil? @server)
    (reset! server (mk-server port))))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 1000)
    (reset! server nil)))

(defn -main [& _args]
  (if-let [port (->int (:port env))]
    (do
      (log/info "Starting server on port" port)
      (start-server port))
    (println "NOPORT. Exiting...")))