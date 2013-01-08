(ns coins.simple-example-1
  (:use [datomic.api :only [q db] :as d]
        [clojure.pprint]
        [coins.helpers]))

;; Create a database in memory - this means it will vanish when our program closes.
(def conn
  (create-simple-connection))

;; Define a really simple schema that is only able to store catalogue number and weight about an entity

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
    :db.install/_attribute :db.part/db}])

;; Install the schema into the database, so that it knows what catalogue-number and weight mean.

(d/transact conn schema)

;; Now here's some data for a few coins

(def some-coins
  [{:db/id (d/tempid :db.part/user)
    :coin/catalogue-number 2
    :coin/weight 2.03}

   {:db/id (d/tempid :db.part/user)
    :coin/catalogue-number 11
    :coin/weight 4.56}

   {:db/id (d/tempid :db.part/user)
    :coin/catalogue-number 17
    :coin/weight 4.46}])

;; Add that data to the database

(d/transact conn some-coins)

;; Now let's look at what is in the database
;; We'll ask for all [catalogue-number weight] pairs

(pprint
 (d/q '[:find ?cat-number ?wt
        :where [?e :coin/catalogue-number ?cat-number]
        [?e :coin/weight ?wt]]
      (db conn)))

;; OUTPUT: #<HashSet [[11 4.56], [2 2.03], [17 4.46]]>

;; Well, we got what we asked for, but it doesn't look very nice.
;; This time, get all the entities that have both catalogue-number and weight attributes.

(def entities
 (map d/touch
      (map #(d/entity (db conn) (first %))
           (d/q '[:find ?entity-id
                  :where [?entity-id :coin/catalogue-number]
                  [?entity-id :coin/weight]]
                (db conn)))))

(pprint entities)

;; OUTPUT: ({:coin/catalogue-number 2, :coin/weight 2.03, :db/id 17592186045418}
;;          {:coin/catalogue-number 17, :coin/weight 4.46, :db/id 17592186045420}
;;          {:coin/catalogue-number 11, :coin/weight 4.56, :db/id 17592186045419})

;; That looks nicer!
;; Now, what's the average weight of a coin?

(let [all-weights (map :coin/weight entities)
      total-weight (reduce + all-weights)
      average-weight (/ total-weight (count entities))]

  (println "Average weight of" (count entities) "coins:" average-weight))

;; OUTPUT: Average weight of 3 coins: 3.6833333333333336


;; Excellent. That'll do for now.
