(defproject weather-crawler "0.2.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.logging "0.4.1"]
                 [cprop "0.1.13"]
                 [clj-http "3.9.1"]
                 [clj-time "0.15.1"]
                 [enlive "1.1.6"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [mount "0.1.15"]]
  :plugins [[lein-ancient "0.6.15"]]
  :main ^:skip-aot amjil.core
  :target-path "target/%s"
  :profiles
  {:uberjar {:aot :all}
   :dev           [:project/dev :profiles/dev]
   :project/dev  {:jvm-opts ["-Dconf=config.edn"]}
   :profiles/dev {}})
