(ns ifsc.handlers
(:require
 [ifsc.handlers.api-handler :refer [defapi]]
 [ifsc.errors :refer [mk-error]]
 [ifsc.core :as core]))

 (defapi fetch-ifsc-details
   "Get the branch details based on the IFSC code"
   [req]
   (if-let [info (core/find-by-code (get-in req [:params :ifsc]))]
     [true info]
     (mk-error "IFSC_NOT_FOUND")))

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