(ns ifsc.handlers.api-handler
  (:require
   [ring.util.response :refer [status]]
   [ifsc.errors :refer [mk-error]]
   [com.pekaplay.util :refer [json-response]]))

(defmacro defapi
  "Simply require the api handler to return a tuple [boolean CljStruct].
  Converts the struct into json and sets the HTTP status response based
  on the boolean value.

  Wraps the response in exceptions"
  [name doc-string ring-req & body]
  `(def ~name ~doc-string
     (fn ~ring-req
       (try
         (if-let [[ok?# res#] (do ~@body)]
           (if ok?#
             (status (json-response res#) 200)
             (status (json-response res#) 401)))
         (catch Exception ex#
           (.printStackTrace ex#)
           (let [data# (ex-data ex#)]
             (status (json-response {:message (.getMessage ex#)
                                     :error-code (:code data#)
                                     :error-string (:code-str data#)}) 500)))))))

(defmacro with-auth
  "Check for both authorization and authentication headers. If either of them
  is not present bail out"
  [ring-req auth-token-name api-key-name & body]
  `(let [ring-req# ~ring-req]
     (if-let [~auth-token-name (get-in ring-req# [:headers "authorization"])]
       (if-let [~api-key-name (get-in ring-req# [:headers "x-application"])]
         ~@body
         (mk-error "PAM_MISSING_API_KEY"))
       (mk-error "PAM_MISSING_AUTH_TOKEN"))))

(defmacro with-authz
  "Check for just the authorization header. Bail out if not present"
  [ring-req api-key-name & body]
  `(let [ring-req# ~ring-req]
     (if-let [~api-key-name (get-in ring-req# [:headers "x-application"])]
       ~@body
       (mk-error "PAM_MISSING_API_KEY"))))