(defproject weather-crawler "0.2.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/tools.logging "1.0.0"]
                 [clj-http "3.10.0"]
                 [clj-time "0.15.2"]
                 [enlive "1.1.6"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [mount "0.1.16"]]
  :plugins [[lein-ancient "0.6.15"]]
  :main ^:skip-aot amjil.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
