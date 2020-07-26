(ns ifsc.db
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource))
  (:require
   [clj-time.core :as clj-time]
   [clj-time.coerce :refer [to-sql-time]]
   [clojure.string :as string]
   [environ.core :refer [env]]))

(def spec {:classname   "org.h2.Driver"
           :subprotocol "h2"
           :subname     "./ifsc.db"})

(defn pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
                (.setDriverClass (:classname spec))
                (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
                (.setMaxIdleTimeExcessConnections (* 30 60))
                (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pooled-db (delay (pool spec)))

(defn db-connection [] @pooled-db)

(defmulti generated-key
  (fn [rowset] (:sql-store rowset)))

(defmethod generated-key :sqlite3
  [rowset]
  (get rowset (keyword "last_insert_rowid()")))

(defmethod generated-key :default
  [rowset]
  (:generated_key rowset))

(defn now []
  (to-sql-time (clj-time/now)))

(defn with-timestamps [entity-values]
  (-> entity-values
      (assoc :created_at (now))
      (assoc :updated_at (now))))

(defn with-updated_at [entity-values]
  (-> entity-values
      (assoc :updated_at (now))))

(defn add-dashes [s]
  (let [f (fn [[s xs] len] [(subs s len) (conj xs (subs s 0 len))])
        [_ chunks] (reduce f [s []] [8 4 4 4 12])]
    (string/join "-" chunks)))

(defn remove-dashes [s] (string/replace s "-" ""))
