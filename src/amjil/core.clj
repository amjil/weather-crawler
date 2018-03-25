(ns amjil.core
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html])
  (:gen-class))

(def my-cookie (cookie/cookie-store))
(def forcast15-url "http://tianqi.moji.com/forecast15/china/inner-mongolia/xincheng-district")
(def weather-url "http://tianqi.moji.com/weather/china/inner-mongolia/xincheng-district")
(def hour24-url "http://tianqi.moji.com/index/getHour24")
(def area-url "http://tianqi.moji.com/weather/china/inner-mongolia")

(defn area-urls []
  (let [areas (->> (-> (client/get area-url) :body (java.io.StringReader.) (html/html-resource)
                     (html/select [:div.city_hot :ul :li :a]))
                (map #(list (first (:content %)) (-> % :attrs :href)))
                (map #(into [] %))
                (into {}))]))



(defn forcast
  []
  (let [forcast (-> (client/get forcast15-url {:cookie-store my-cookie})
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
                 (map #(into {} (map (fn [[k v]] [(subs k 1) v]) %))))]))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (java.io.StringReader.) (html/html-resource)
  (println "Hello, World!"))
