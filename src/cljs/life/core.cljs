(ns life.core
  (:require [clojure.set :as set]))

(defn neighbors-locations [[x y]]
  (into #{} (for [sumx #{-1 1 0} sumy #{-1 1 0}
                  :when (not (and (= sumx 0) (= sumy 0)))]
              [(+ x sumx) (+ y sumy)])))

(defn neighbors-per-cell [world]
  (reduce
   (fn [m cell]
     (let [cell-neighbors (neighbors-locations cell)]
       (assoc m cell {:living (set/intersection cell-neighbors world)
                      :dead (set/difference cell-neighbors world)})))
   {}
   world))

(defn survivor-cells [neighbors-per-cell]
  (keys (filter (fn [[cell neighbors]]
                  (#{2 3} (count (:living neighbors))))
                neighbors-per-cell)))

(defn new-born-cells [neighbors-per-cell]
  (let [all-dead-cells (mapcat (fn [[cell cell-neighbors]]
                                 (into [] (:dead cell-neighbors)))
                               neighbors-per-cell)]
    (keys (filter (fn [[cell neighbor-instances]]
                    (#{3} (count neighbor-instances)))
                  (group-by identity all-dead-cells)))))

(defn tick [world]
  (let [neighbors-per-cell (neighbors-per-cell world)]
    (set/union (into #{} (survivor-cells neighbors-per-cell))
               (into #{} (new-born-cells neighbors-per-cell)))))

(defn draw-cells [world]
  (let [element (.getElementById js/document "canvas")
        context (.getContext element "2d")]
    (.clearRect context 0 0 500 500)
    (doseq [x (range 100)
            y (range 100)]
      (.strokeRect context (* x 11) (* y 11) 11 11))
    (set! (. context -fillStyle) 'green')
    (doseq [[x y] world]
      (.fillRect context (* x 11) (* y 11) 10 10))))

(defn draw-next-tick [world]
  (.setTimeout js/window
               (fn []
                 (let [next-generation (tick world)]
                   (draw-cells next-generation)
                   (draw-next-tick next-generation)))
               2000))

(defn main []
  (let [world #{[12 0] [13 0] [14 0]
                [12 5] [13 5] [14 5]
                [10 2] [10 3] [10 4]
                [15 2] [15 3] [15 4]

                [17 2] [17 3] [17 4]
                [18 0] [19 0] [20 0]
                [18 5] [19 5] [20 5]
                [22 2] [22 3] [22 4]

                [12 7]  [13 7]  [14 7]
                [12 12] [13 12] [14 12]
                [10 8]  [10 9]  [10 10]
                [15 8]  [15 9]  [15 10]

                [17 8]  [17 9]  [17 10]
                [18 7]  [19 7]  [20 7]
                [18 12] [19 12] [20 12]
                [22 8]  [22 9]  [22 10]}]
    (draw-cells world)
    (draw-next-tick world)))
