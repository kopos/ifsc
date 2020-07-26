(ns ifsc.migrations.seed-ifsc-data
  (:require
    [clojure.java.io :as io]
    [clojure.java.jdbc :as sql]
    [clojure.data.csv :as csv]
    [com.pekaplay.util :refer [->resource]]))

(def cols ["BANK" "IFSC" "BRANCH" "CENTRE" "DISTRICT" "STATE" "ADDRESS"
                "CONTACT" "IMPS" "RTGS" "CITY" "NEFT" "MICR" "UPI"])

(defn csv-file->rows []
  (->> (->resource "fixtures/IFSC.csv")
       (io/reader)
       (csv/read-csv)))

(defn row->IFSC [row]
  (let [[bank ifsc branch centre district state address contact
         imps rtgs city neft micr upi] row
        imps? (if (= imps "true") 1 0)
        rtgs? (if (= rtgs "true") 1 0)
        neft? (if (= neft "true") 1 0)
        upi?  (if (= upi "true") 1 0)]
    {:bank bank
     :ifsc ifsc
     :branch branch
     :centre centre
     :district district
     :state state
     :address address
     ;:contact contact
     :has_imps imps?
     :has_rtgs rtgs?
     :city city
     :has_neft neft?
     :micr micr
     :has_upi upi?}))

(defn migrate-up [config]
  (let [rows (csv-file->rows)]
    (assert (= cols (first rows)))
    (doseq [rows (partition 100 (rest rows))]
      (try
        (sql/insert-multi! (:db config) :bnk_ifsc (map row->IFSC rows))
        (catch Throwable ex
          (prn ex rows))))))

(defn migrate-down [config] true)