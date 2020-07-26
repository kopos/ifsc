(ns ifsc.errors)

(def ^:const ERRORS
  {"INVALID_BRANCH_CODE" {:code "invalid-branch-code"
                          :message "Invalid Branch Code"}
   "INVALID_BANK_CODE" {:code "invalid-bank-code"
                        :message "Invalid Bank Code"}
   "INVALID_FORMAT" {:code "invalid-format"
                     :message "Invalid Format"}
   "IFSC_NOT_FOUND" {:code "ifsc-not-found"
                     :message "IFSC not found"}
   "DEFAULT" {:code "err-unknown"
              :message "Unknown error"}})

(defn raise-error
  [code-str]
  (let [{:keys [code message]} (get ERRORS code-str)]
    (throw (ex-info message {:code code :code-str code-str}))))

(defn mk-error
  ([code-str]
    (let [{:keys [code message]} (get ERRORS code-str)]
      [false {:error-code code
              :error-string code-str
              :message message}]))
  ([code-str message]
    (let [{:keys [code _message]} (get ERRORS code-str)]
      [false {:error-code code
              :error-string code-str
              :message message}])))