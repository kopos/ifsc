(ns ifsc.core
  "Data at
  https://github.com/razorpay/ifsc/releases
  https://github.com/razorpay/ifsc/blob/master/src/elixir/lib/razorpay/ifsc.ex

  Cleanup non-ascii characters from the CSV file using:
  $ perl -pi -e 's/[^[:ascii:]]//g' IFSC.csv
  "
  (:require
   [clojure.set :refer [rename-keys]]
   [clojure.java.jdbc :as jdbc]
   [clojure.string :refer [upper-case]]
   [cheshire.core :as json]
   [com.pekaplay.util :refer [->resource]]
   [com.pekaplay.math :refer [->int]]
   [ifsc.errors :refer [mk-error]]
   [ifsc.handlers.api-handler :refer [defapi]]
   [ifsc.db :as db]))

(defrecord IFSC [bank ifsc micr branch centre district state city address contact
                 imps? rtgs? neft? upi?])

(defn prepare
  [ifsc]
  (rename-keys ifsc {:imps? :has_imps
                     :neft? :has_neft
                     :rtgs? :has_rtgs
                     :upi?  :has_upi}))

(defn insert [ifsc]
  (let [orm (prepare ifsc)]
    (jdbc/insert! orm (db/db-connection) :bnk_ifsc)))

(def transform identity)

(defn find-by-code
  [ifsc]
  (let [rs (jdbc/query (db/db-connection) ["SELECT * FROM bnk_ifsc WHERE ifsc = ?", ifsc])]
    (if (>= (count rs) 1)
      (transform (first rs)))))

(defonce ifscs (atom nil))

(defonce banks (atom nil))

(defonce sublets (atom nil))

(defn file->json [file]
  (-> file (->resource) (slurp) (json/decode)))

(defn init []
  (reset! ifscs   (file->json "fixtures/IFSC.json"))
  (reset! banks   (file->json "fixtures/banknames.json"))
  (reset! sublets (file->json "fixtures/sublet.json")))

(defn find-by-ifsc [ifsc]
  (get @ifscs ifsc))

(defn bank-code [ifsc sublet?]
  (if sublet?
    (or (get @sublets ifsc) (subs ifsc 0 4))
    (subs ifsc 0 4)))

(defn bank-name [ifsc]
  (get @banks (bank-code ifsc true)))

(defn branch-code [ifsc]
  (let [code (subs ifsc 4)]
    (if (re-matches #"^\d+$" code)
      (->int code)
      (upper-case code))))

(defn validate-format [ifsc]
  (and (= 11 (count ifsc)) (= \0 (nth ifsc 4))))

(defn validate-bank [ifsc]
  (contains? @banks (bank-code ifsc false)))

(defn validate-branch [ifsc]
  (let [ifsc-data (find-by-code ifsc)
        code (bank-code ifsc false)
        branches (get @ifscs code)]
    (and branches (.contains branches (branch-code ifsc)))))
