(defproject ifsc "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main ifsc.main
  :dependencies
  [[org.clojure/clojure "1.10.0"]

   ; logging
   [org.clojure/tools.logging "0.4.1"]
   [ch.qos.logback/logback-classic "1.1.2"]
   [ch.qos.logback/logback-core "1.1.2"]
   [org.slf4j/slf4j-api "1.7.9"]
   [org.codehaus.janino/janino "2.6.1"]
   [org.clojure/data.csv "1.0.0"]

   ; webserver
   [ring-oauth2 "0.1.4"]
   [ring/ring-spec "0.0.4"]
   [ring/ring-core "1.7.0-RC1" :exclusions [ring/ring-codec]]
   [ring/ring-defaults "0.3.2"]
   [ring/ring-json "0.4.0"]
   [ring-logger "1.0.1"]
   [compojure "1.6.1"]

   ; db
   [org.clojure/java.jdbc "0.7.8"]
   [clj-time "0.14.4"]
   [com.mchange/c3p0 "0.9.5.2"]
   [migratus "1.2.3"]
   [migratus-lein "0.7.2"]
   [org.xerial/sqlite-jdbc "3.7.15-M1"]
   [com.h2database/h2 "1.4.194"]

   [pekaplay-base "0.3.9"]
   [http-kit "2.3.0"]]

  :plugins
  [[lein-environ "1.1.0"]
   [migratus-lein "0.7.2"]]

  :repl-options {:init-ns ifsc.core}

  :migratus
  {:store :database
   :migration-dir ~(or (System/getenv "MIGRATIONS_TABLE") "migrations")
   :db {:classname   "org.h2.Driver"
        :subprotocol "h2"
        :subname     "./resources/ifsc.db"}})
