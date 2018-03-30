(ns amjil.core
  (:require [clj-http.client :as client]
            [clj-http.cookies :as cookie]
            [net.cgrand.enlive-html :as html]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log])
  (:gen-class))

(def hahha [{:id 1 :categories 1 :name "文艺照" :multi true
             :data [{:id 1 :name "单人" :amount 299.0 :data [{:id 1 :name "单人四宫格" :amount 200.0} {:id 2 :name "单人六宫格"}]}
                    {:id 2 :name "情侣" :amount 358.0 :data [{:id 1 :name "情侣四宫格" :amount 200.0} {:id 2 :name "情侣九宫格"}]}
                    {:id 3 :name "闺蜜" :amount 358.0 :data {:id 1 :name "闺蜜拍摄人数" :amount 2}}
                    {:id 4 :name "亲子" :amount 358.0 :data {:id 1 :name "亲子拍摄人数" :amount 2}}]}
            {:id 1 :name "形象套餐"  :categories 1
             :data [{:id 1 :name "形象套餐" :amount 399.0}]}
            {:id 2 :name "仙女照-单人"  :categories 1
             :data [{:id 1 :name "仙女照-单人" :amount 299.0
                     :data [{:id 1 :name "单人四宫格" :amount 200}
                            {:id 2 :name "单人六宫格" :amount 360}
                            {:id 3 :name "单人九宫格" :amount 600}]}]}
            {:id 3 :name "仙女照-双人"  :categories 1
             :data [{:id 1 :name "仙女照-单人" :amount 398.0
                     :data [{:id 1 :name "双人四宫格" :amount 261}
                            {:id 2 :name "双人六宫格" :amount 461}
                            {:id 3 :name "双人九宫格" :amount 701}]}]}
            {:id 4 :name "精致证件照"  :categories 2
             :data [{:id 1 :name "精致证件照" :multi true :data [{:id 1 :name "白色" :amount 159} {:id 2 :name "蓝色" :amount 159}]}]}
            {:id 5 :name "精致签证照"  :categories 2
             :data [{:id 1 :name "精致签证照" :amount 159.0}]}
            {:id 6 :name "结婚照"  :categories 2
             :data [{:id 1 :name "结婚登记照" :amount 299.0
                     :data [{:id 1 :name "纪念套餐A" :amount 179} {:id 2 :name "纪念套餐B" :amount 300.0}]}
                    {:id 2 :name "结婚纪念照" :amount 358
                     :data [{:id 1 :name "四宫格" :amount 241} {:id 2 :name "六宫格" :amount 441} {:id 3 :name "九宫格" :amount 641}]}]}
            {:id 7 :name "职业形象照"  :categories 2
             :data [{:id 1 :name "职业形象照" :amount 299.0
                     :data [{:id 1 :name "四宫格" :amount 200}
                            {:id 2 :name "六宫格" :amount 360}
                            {:id 3 :name "九宫格" :amount 600}]}]}
            {:id 8 :categories 3 :name "真颜照" :multi true
             :data [{:id 1 :name "单人" :amount 299.0
                     :data [{:id 1 :name "单人四宫格" :amount 200.0} {:id 2 :name "单人六宫格" :amount 360} {:id 3 :name "单人九宫格" :amount 600}]}
                    {:id 2 :name "情侣" :amount 398.0
                     :data [{:id 1 :name "情侣四宫格" :amount 261.0} {:id 2 :name "情侣六宫格" :amount 461} {:id 3 :name "情侣九宫格" :amount 701}]}
                    {:id 3 :name "闺蜜" :amount 398.0
                     :data [{:id 1 :name "闺蜜四宫格" :amount 261.0} {:id 2 :name "闺蜜六宫格" :amount 461} {:id 3 :name "闺蜜九宫格" :amount 701}]}
                    {:id 4 :name "亲子" :amount 398.0
                     :data [{:id 1 :name "亲子四宫格" :amount 261.0} {:id 2 :name "亲子六宫格" :amount 461} {:id 3 :name "亲子九宫格" :amount 701}]}]}
            ;
            {:id 9 :categories 3 :name "文艺照" :multi true
             :data [{:id 1 :name "单人" :amount 299.0 :data [{:id 1 :name "单人四宫格" :amount 200.0} {:id 2 :name "单人六宫格"}]}
                    {:id 2 :name "情侣" :amount 358.0 :data [{:id 1 :name "情侣四宫格" :amount 200.0} {:id 2 :name "情侣九宫格"}]}
                    {:id 3 :name "闺蜜" :amount 358.0 :data {:id 1 :name "闺蜜拍摄人数" :amount 2}}
                    {:id 4 :name "亲子" :amount 358.0 :data {:id 1 :name "亲子拍摄人数" :amount 2}}]}
            {:id 10 :categories 3 :name "全家福"
             :data [{:id 1 :name "全家福" :amount 499 :data {:id 1 :name "闺蜜拍摄人数" :amount 2}}]}
            {:id 1 :data [{:id 1 :data 2}
                          {:id 2 :data 1}
                          {:id 3 :data 5}]}

            {:id 4 :data [{:id :data [1 2]}]}])


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
              "兴和县" "ᠰᠢᠩ ᠾᠧ ᠰᠢᠶᠠᠨ" "红山区" "ᠤᠯᠠᠭᠠᠨᠬᠠᠲᠠ" "临河区" "ᠯᠢᠨᠾᠧ"
              "岗子乡" "ᠭᠠᠩᠽᠢ ᠰᠢᠶᠠᠩ" "松山区" "ᠰᠦᠩᠱᠠᠨ ᠲᠣᠭᠣᠷᠢᠭ" "喀喇沁亲王府" "ᠬᠠᠷᠠᠴᠢᠨ ᠴᠢᠨ ᠸᠠᠩ ᠤᠨ ᠣᠷᠳᠣᠨ"
              "南海湿地景区" "ᠡᠮᠦᠨᠡᠲᠦ ᠲᠠᠩᠭᠢᠰ ᠤᠨ ᠦᠵᠡᠮᠵᠢ  ᠶᠢᠨ ᠣᠷᠣᠨ" "高力板镇" "ᠭᠣᠣᠯ ᠤᠨ ᠪᠠᠶᠢᠰᠢᠩ ᠪᠠᠯᠭᠠᠰᠤ "
              "科尔沁左翼中旗" "ᠬᠣᠷᠴᠢᠨ ᠵᠡᠭᠦᠨ ᠭᠠᠷᠤᠨ ᠳᠤᠮᠳᠠᠳᠤ ᠬᠣᠰᠢᠭᠤ "
              "满洲里市" "ᠮᠠᠨᠵᠤᠤᠷ ᠬᠤᠳᠠ" "昭君博物院" "ᠵᠤᠤ ᠵᠢᠶᠦ᠋ᠨ ᠲᠦᠮᠡᠨ ᠪᠣᠳᠠᠰ ᠤᠨ ᠬᠦᠷᠢᠶᠡᠯᠡᠩ"
              "奈曼旗" "ᠨᠠᠶᠮᠠᠨ ᠬᠣᠰᠢᠭᠤ" "集宁区" "ᠵᠢᠨᠢᠩ ᠲᠣᠭᠣᠷᠢᠭ" "巴林右旗" "ᠪᠠᠭᠠᠷᠢᠨ ᠪᠠᠷᠠᠭᠤᠨ ᠬᠣᠰᠢᠭᠤ"
              "鄂托克前旗" "ᠤᠳᠤᠭ ᠤᠨ ᠡᠮᠦᠨᠡᠲᠦ ᠬᠣᠰᠢᠭᠤ" "中国·克什克腾世界地质公园" "ᠬᠡᠰᠢᠭᠲᠡᠨ"
              "鄂温克族自治旗" "ᠡᠸᠡᠩᠬᠢ ᠬᠣᠰᠢᠭᠤ" "呼和诺尔旅游景区" "ᠬᠦᠬᠡ ᠨᠠᠭᠤᠷ ᠵᠢᠭᠤᠯᠴᠢᠯᠠᠯ ᠤᠨ ᠣᠷᠣᠨ"
              "浩尔吐乡" "ᠬᠣᠭᠣᠯᠠᠶᠢᠲᠤ ᠰᠢᠶᠠᠩ"})

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
