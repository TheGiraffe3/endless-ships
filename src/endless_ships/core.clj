(ns endless-ships.core
  (:require [camel-snake-kebab
             [core :refer [->camelCaseKeyword]]
             [extras :refer [transform-keys]]]
            [cheshire.core :refer [generate-string]]
            [endless-ships
             [parser :refer [data]]
             [ships :refer [ships]]]))

(def file->race
  {"kestrel.txt" :human
   "hai ships.txt" :hai
   "pug.txt" :pug
   "wanderer ships.txt" :wanderer
   "quarg ships.txt" :quarg
   "remnant ships.txt" :remnant
   "korath ships.txt" :korath
   "marauders.txt" :pirate
   "coalition ships.txt" :coalition
   "drak.txt" :drak
   "ships.txt" :human})

(def ships-data
  (->> ships
       (filter #(some? (file->race (:file %))))
       (map #(-> %
                 (select-keys [:name :licenses :file
                               :cost :category :hull :shields :mass
                               :engine-capacity :weapon-capacity :fuel-capacity
                               :outfit-space :cargo-space
                               :required-crew :bunks])
                 (assoc :race (get file->race (:file %) :other))
                 (dissoc :file)))
       (map #(transform-keys ->camelCaseKeyword %))))

(defn generate-json [& {:keys [pretty] :or {pretty true}}]
  (let [json (generate-string ships-data {:pretty pretty})]
    (spit "build/data.json" (str json "\n"))))

(comment
  ;; generate data for frontend development
  (let [json (generate-string ships-data {:pretty true})]
    (spit "frontend/public/data.json" (str json "\n")))
  ;; get a list of all possible attribute names
  (->> ships-data
       (map keys)
       (apply concat)
       (into #{}))
  ;; get ship counts by race
  (->> ships-data
       (map :race)
       (reduce (fn [counts object]
                 (update counts object #(inc (or % 0))))
               {})
       (sort-by last >)))
