(ns mastodonusers.core
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer :all]
            [clojure.walk :refer [keywordize-keys]]
            [clj-http.client :as h]
            [clj-time.core :as t]
            [clj-time.format :as f])
  (:gen-class))


;; (def application-id "431276798")
;; (def application-name "mastodonusers")


(defn fetch-data
  "Fetch current data from instances.social with our key"
  []
  (h/get
   "https://instances.social/api/1.0/instances/list"
   {:headers {:authorization (str
                              "Bearer "
                              (slurp "resources/secret.txt"))}
    :query-params {:count 0}
    :accept :json}))


(defn count-users
  "Count the total number of users listed for instances in this `data`"
  [data]
  (reduce
   +
   (keep
    :users
    (:instances
     (keywordize-keys
      (json/read-str
       (:body data)
       :key-fn keyword
       :value-fn (fn [k v]
                   (if
                     (and
                      (string? v)
                      (re-matches #"^[0-9]+$" v))
                     (read-string v)
                     v))))))))


(defn update-file
  "Append to this EDN formatted `file`, expected to contain a map,
  a pair comprising the current time-stamp and the value of `c`."
  [file c]
  (spit
   file
   (with-out-str
     (pprint
      (assoc
        (try
          (read-string (slurp file))
          (catch Exception _ {}))
        (f/unparse (f/formatters :basic-date-time) (t/now))
        c)))))


(defn -main [& args]
  "Print a count of the current number of user accounts in the fediverse.
  If an argument is present, it is presumed to be the pathname of
  an EDN formatted file containing a map mapping times to counts, which will
  be updated with the current count."
  (let [c (count-users (fetch-data))]
    (if-not (empty? args) (update-file (first args) c))
    (println "Total #fediverse user accounts: " c)
    c))


