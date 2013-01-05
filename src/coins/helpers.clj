(ns coins.helpers
  (:use [datomic.api :only [q db] :as d]
        [clojure.repl]))

(set! *print-length* 20)

(defn create-simple-connection []
  (let [url (str "datomic:mem://" (d/squuid))]
    (d/delete-database url)
    (d/create-database url)
    (d/connect url)))
