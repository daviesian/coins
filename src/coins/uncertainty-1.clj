(ns coins.uncertainty-1
  (:use [datomic.api :only [q db] :as d]
        [clojure.pprint]
        [coins.helpers]))

;; Create a database in memory - this means it will vanish when our program closes.
(def conn
  (create-simple-connection))

;; Define a slightly more complicated schema.
;; Now we can add lots of "uncertainty" labels to a coin, each one pointing to an attribute
;; that we're uncertain about. Each "uncertainty" contains a description of why we're uncertain.

(def schema
  [{:db/id (d/tempid :db.part/db)
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
    :db/ident :coin/uncertainty
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :coin.uncertainty/attribute
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   {:db/id (d/tempid :db.part/db)
    :db/ident :coin.uncertainty/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}])

(d/transact conn schema)

(def some-uncertain-coins
  [{:db/id (d/tempid :db.part/user -1)
    :coin/catalogue-number 2
    :coin/emperor "Justin I"
    :coin/weight 2.03}

   {:db/id (d/tempid :db.part/user -2)
    :coin/_uncertainty (d/tempid :db.part/user -1)
    :coin.uncertainty/attribute :coin/emperor
    :coin.uncertainty/description "Not really sure that it was Justin. Could have been Kyle."}

   {:db/id (d/tempid :db.part/user)
    :coin/catalogue-number 11
    :coin/emperor "Anastasius I"
    :coin/weight 4.56}

   {:db/id (d/tempid :db.part/user)
    :coin/catalogue-number 17
    :coin/emperor "Zeno"
    :coin/weight 4.46}])

(d/transact conn some-uncertain-coins)


(pprint
 (q '[:find ?cat-num ?uncertain-thing ?why-uncertain
      :where
      [?e :coin/catalogue-number ?cat-num]
      [?e :coin/uncertainty ?u]
      [?u :coin.uncertainty/attribute ?uncertain-att]
      [?uncertain-att :db/ident ?uncertain-thing]
      [?u :coin.uncertainty/description ?why-uncertain]]
    (db conn)))
