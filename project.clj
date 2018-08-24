(defproject mastodonusers "0.1.2-SNAPSHOT"
  :description "A Clojure app to report and log the current number of users in the fediverse."
  :url "https://github.com/simon-brooke/mastodonusers"
  :license {:name "GNU General Public License,version 2.0"
            :url "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.cli "0.3.7"]
                 [clj-http "3.9.1"]
                 [clj-time "0.14.4"]]
  :aot [mastodonusers.core]
  :main mastodonusers.core)
