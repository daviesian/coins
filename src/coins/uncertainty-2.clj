(ns coins.uncertainty-2
  (:use [datomic.api :only [q db] :as d]
        [clojure.pprint]
        [coins.helpers]))

;; Create a database in memory - this means it will vanish when our program closes.
(def conn
  (create-simple-connection))

;; Define a similar schema to the first uncertainty example, but this time every coin
;; has a set of facts. Each fact has a single value attribute (something like :coin/weight),
;; but can also have an uncertainty attached to it.

(def schema
  [{:db/id (d/tempid :db.part/db)
    :db/ident :fact
    :db/isComponent true
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :coin/catalogue-number
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :coin/weight
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :coin/emperor
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :fact/uncertainty
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}])

;; Install the schema

(d/transact conn schema)

;; Add the same coins, with the same uncertain emperor. Notice that this time it takes much more code.

(def some-uncertain-coins
  [
   ;; First coin

   {:db/id (d/tempid :db.part/user -1)}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -1)
    :coin/catalogue-number 2}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -1)
    :coin/weight 2.03}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -1)
    :coin/emperor "Justin I"
    :fact/uncertainty "Actually I think it was Chaz, not Justin."}


   ;; Second coin

   {:db/id (d/tempid :db.part/user -2)}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -2)
    :coin/catalogue-number 11}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -2)
    :coin/weight 4.56}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -2)
    :coin/emperor "Anastasius I"}


   ;; Third coin

   {:db/id (d/tempid :db.part/user -3)}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -3)
    :coin/catalogue-number 17}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -3)
    :coin/weight 4.46}

   {:db/id (d/tempid :db.part/user)
    :_fact (d/tempid :db.part/user -3)
    :coin/emperor "Zeno"}])

(d/transact conn some-uncertain-coins)

;; Find all the coins with uncertain emperors.
(pprint
 (q '[:find ?cat-num ?emperor ?uncertainty
      :where
      [?c :fact ?f1]
      [?f1 :coin/catalogue-number ?cat-num]
      [?c :fact ?f2]
      [?f2 :coin/emperor ?emperor]
      [?f2 :fact/uncertainty ?uncertainty]
     ]
    (db conn)))
