(ns ifsc.routes
  (:require
    [clojure.tools.logging :as log]
    [compojure.core :refer [defroutes context GET]]
    [ifsc.handlers :as h]))

(defroutes routes
  (context "/api/v1" []
           (GET "/banks/:code" [] h/fetch-bank-name)
           (context "/ifsc:ifsc" []
                    (GET "/" [] h/fetch-ifsc-details)
                    (GET "/validate" [] h/validate))))