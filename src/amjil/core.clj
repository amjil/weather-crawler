(ns amjil.core
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookie]
            [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log])
  (:gen-class))

(def my-cookie (cookie/cookie-store))
(def forcast15-url "http://tianqi.moji.com/forecast15/china/inner-mongolia/")
(def weather-url "http://tianqi.moji.com/weather/china/inner-mongolia/")
(def hour24-url "http://tianqi.moji.com/index/getHour24")
(def area-url "http://tianqi.moji.com/weather/china/inner-mongolia")

(def condition {"晴" "ᠴᠡᠯᠮᠡᠭ" "多云" "ᠡᠭᠦᠯᠡᠲᠡᠢ" "少云" "ᠡᠭᠦᠯᠡᠷᠬᠡᠭ" "阴" "ᠪᠦᠷᠬᠦᠭ" "雨夹雪" "ᠪᠣᠷᠤᠭ᠎ᠠ ᠴᠠᠰᠤ" "小雨" "ᠪᠠᠭ᠎ᠠ ᠪᠣᠷᠤᠭ᠎ᠠ"})
(def address {"乌审召镇" "ᠦᠦᠰᠢᠨ ᠬᠣᠰᠢᠭᠤ" "乌达区" "ᠦᠬᠠᠶ" "鄂伦春自治旗" "ᠣᠷᠴᠣᠨ ᠬᠣᠰᠢᠭᠤ "
              "喀喇沁旗" "ᠬᠠᠷᠠᠴᠢᠨ ᠬᠣᠰᠢᠭᠤ" "巴彦淖尔市" "ᠪᠠᠶᠠᠨᠨᠠᠭᠤᠷ ᠬᠤᠳᠠ" "太仆寺旗" "ᠲᠠᠶᠢᠪᠤᠰᠧ ᠬᠣᠰᠢᠭᠤ"
              "中国达斡尔民族园" "ᠮᠤᠷᠢᠠᠳᠠᠪᠠᠭ᠎ᠠ" "伊克乌素镇" "ᠶᠡᠬᠡᠤᠰᠤ" "包头市青山区" "ᠪᠤᠭᠤᠲᠤ"
              "锡林郭勒盟" "ᠰᠢᠯᠢ ᠶᠢᠨ ᠭᠣᠸᠯ ᠬᠣᠲᠠ" "河南(内蒙)" "ᠾᠧᠨᠠᠨ" "凉城县" "ᠯᠢᠶᠠᠭᠩᠴᠧᠩ ᠰᠢᠶᠠᠨ"
              "巴彦诺尔贡" "ᠪᠠᠶᠠᠨᠨᠢᠷᠤᠭᠤᠨ" "磴口县" "‍ᠳᠧᠩᠺᠧᠤ ᠰᠢᠶᠠᠨ" "拐子湖" "ᠳᠤᠭᠤᠯᠠᠩ ᠨᠠᠭᠤᠷ"
              "四子王旗" "ᠲᠦᠷᠪᠡᠳ ᠬᠤᠰᠢᠭᠤ" "呼和浩特市新城区" "ᠰᠢᠨ᠎ᠠ ᠬᠤᠳᠡ ᠲᠤᠭᠤᠷᠢᠭ"
              "回民区" "ᠬᠤᠳᠤᠠ᠌ᠩ ᠡᠠᠷᠠᠤᠡ ‍ᠤᠡ ᠲᠤᠭᠤᠷᠢᠭ" "格根塔拉草原旅游中心" "ᠭᠡᠭᠡᠨᠳᠡᠯ᠎ᠡ ᠵᠢᠭᠤᠯᠴᠢᠯᠠᠯ ‍ᠤᠨ ᠤᠷᠤᠨ"
              "兴和县" "ᠰᠢᠩ ᠾᠧ ᠰᠢᠶᠠᠨ" "红山区" "ᠤᠯᠠᠭᠠᠨᠬᠠᠲᠠ" "临河区" "ᠯᠢᠨᠾᠧ"})

(defn area-urls []
  (let [areas (->> (-> (client/get area-url) :body (java.io.StringReader.) (html/html-resource)
                     (html/select [:div.city_hot :ul :li :a]))
                (map #(list (first (:content %)) (-> % :attrs :href)))
                (map #(into [] %))
                (into {}))]))

(defn forcast
  [area]
  (let [forcast15-url (str forcast15-url area)
        weather-url (str weather-url area)
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
