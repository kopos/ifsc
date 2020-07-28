(ns ifsc.handlers
(:require
 [ifsc.handlers.api-handler :refer [defapi]]
 [ifsc.errors :refer [mk-error]]
 [ifsc.core :as core]))

(defapi fetch-bank-name
  "Fetch bank details"
  [req]
  (if-let [bank-name (core/bank-name (get-in req [:params :code]))]
    [true bank-name]
    (mk-error "INVALID_BANK_CODE")))

(defapi fetch-ifsc-details
  "Get the branch details based on the IFSC code"
  [req]
  (let [ifsc (get-in req [:params :ifsc])]
    (if-let [info (core/find-by-code ifsc)]
      [true (assoc info :bank-code (core/bank-code ifsc true))]
      (mk-error "IFSC_NOT_FOUND"))))

(defapi validate
  "Validate and return the basic details of the IFSC branch"
  [req]
  (let [ifsc (get-in req [:params :ifsc])]
    (cond
      (not (core/validate-format ifsc))
      (mk-error "INVALID_FORMAT")

      (not (core/validate-bank ifsc))
      (mk-error "INVALID_BANK_CODE")

      (not (core/validate-branch ifsc))
      (mk-error "INVALID_BRANCH_CODE")

      :else
      [true {:ifsc ifsc
             :bank (core/bank-name ifsc)
             :bank-code (core/bank-code ifsc true)}])))