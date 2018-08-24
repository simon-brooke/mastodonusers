(ns mastodonusers.core
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer :all]
            [clojure.string :as s]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.walk :refer [keywordize-keys]]
            [clj-http.client :as h]
            [clj-time.core :as t]
            [clj-time.format :as f])
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; mastodonusers.core
;;;;
;;;; This program is free software; you can redistribute it and/or
;;;; modify it under the terms of the GNU General Public License
;;;; as published by the Free Software Foundation; either version 2
;;;; of the License, or (at your option) any later version.
;;;;
;;;; This program is distributed in the hope that it will be useful,
;;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;;; GNU General Public License for more details.
;;;;
;;;; You should have received a copy of the GNU General Public License
;;;; along with this program; if not, write to the Free Software
;;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
;;;; USA.
;;;;
;;;; Copyright (C) 2018 Simon Brooke
;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def cli-options
  [["-h" "--help" "Show this message"
    :default false]
   ["-o" "--csv" "output data as comma-separated values"]
   ["-s" "--bearer-secret [SECRET]" "The secret which identifies us to instances.social"]
   ["-v" "--verbosity [LEVEL]" nil "Verbosity level - integer value required"
    :parse-fn #(Integer/parseInt %)
    :default 0]
   ])


(defn usage
  "Show a usage message. `options` should be options as
  parsed by [clojure.tools.cli](https://github.com/clojure/tools.cli)"
  [options]
  (println (:summary options)))


(defn fetch-data
  "Fetch current data from instances.social with this `secret` key"
  [secret]
  (h/get
   "https://instances.social/api/1.0/instances/list"
   {:headers {:authorization (str
                              "Bearer "
                              (or
                               secret
                               (slurp "resources/secret.txt")))}
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


(defn ->csv
  "Return the contents of this EDN `file`, assumed to contain a map with
  date/time keys and count values, in CSV format."
  [file]
  (let [data (read-string (slurp file))]
    (s/join
     "\n"
     (map
      #(str % ", " (data %))
      (sort
       #(compare
         (f/parse
          ;; (f/formatters :basic-date-time)
          %1)
         (f/parse
          ;; (f/formatters :basic-date-time)
          %2))
       (keys data))))))


(defn process
  "Process these parsed `options`."
  [options]
  (let [c (count-users
           (fetch-data
            (-> options :options :bearer-secret)))
        args (:arguments options)]
    (if-not
      (empty? args)
      (update-file (first args) c))
    (if-not
      (zero? (-> options :options :verbosity))
      (println "Total #fediverse user accounts: " c))
    (if
      (and
       (not (empty? args))
       (-> options :options :csv))
      (println (->csv (first args))))
    c))


(defn -main
  "Print a count of the current number of user accounts in the fediverse.
  If an argument is present, it is presumed to be the pathname of
  an EDN formatted file containing a map mapping times to counts, which will
  be updated with the current count."
  [& args]
  (let [options (parse-opts args cli-options)]
    (cond
     (empty? args)
     (usage options)
     (seq (:errors options))
     (do
       (doall
        (map
         println
         (:errors options)))
       (usage options))
     (-> options :options :help)
     (usage options)
     true
     (process options))))



