(ns amjil.core
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookie]
            [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]
            [amjil.config :refer [env]])
  (:gen-class))

(def my-cookie (cookie/cookie-store))

(defn get-url [type]
  (condp = type
    :forcast15 (str (env :base-url) (env :forcast15-url))
    :weather (str (env :base-url) (env :weather-url))
    :hour24 (str (env :base-url) (env :hour24-url))
    :area (str (env :base-url) (env :area-url))))

(defn area-urls []
  (let [area-url (get-url :area)
        areas (->> (-> (client/get area-url) :body (java.io.StringReader.) (html/html-resource)
                     (html/select [:div.city_hot :ul :li :a]))
                (map #(list (first (:content %)) (-> % :attrs :href)))
                (map #(into [] %))
                (into {}))]))

(defn forcast
  [area]
  (let [forcast15-url (str (get-url :forcast15) area)
        weather-url (str (get-url :weather) area)
        hour24-url (get-url :hour24)
        forcast (-> (client/get forcast15-url {:cookie-store my-cookie})
                  :body (java.io.StringReader.) (html/html-resource)
                  (html/select [:div#detail_future :div.detail_future_grid :div.wea_list :li])
                  (html/texts))
        forcast15 (->> forcast
                    (map #(clojure.string/split % #"\n"))
                    (map (fn [x] (map #(clojure.string/trim %) x)))
                    (map #(keep-indexed (fn [idx x] (if (contains? #{2 8 9 15 16} idx) x)) %))
                    (map #(zipmap [:from :high :low :to :date] %)))
        weather (->> (-> (client/get weather-url {:cookie-store my-cookie})
                       :body (java.io.StringReader.) (html/html-resource)
                       (html/select [:div.forecast :ul.days])
                       (html/texts))
                  (map #(clojure.string/split % #"\n"))
                  (map (fn [x] (map #(clojure.string/trim %) x)))
                  (map #(keep-indexed (fn [idx x] (if (contains? #{8 9 11 12 15} idx) x)) %)))

        hour24 (-> (client/get hour24-url {:cookie-store my-cookie}) :body json/read-str)
        sunset (-> hour24 (get "sunset") (select-keys ["date" "sunrise" "sundown"]))
        hour24 (->> (get hour24 "hour24")
                 (map #(into {} (map (fn [[k v]] [(subs k 1) v]) %))))]
    {:area area :forcast15 forcast15 :forcast3 weather :sunset sunset :hour24 hour24}))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
