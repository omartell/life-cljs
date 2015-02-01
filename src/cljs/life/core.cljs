(ns life.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.set :as set]))

(defonce app-state (atom))

(enable-console-print!)

(defn main []
  (draw-cells))

(defn draw-cells []
  (let [context (.getContext (.getElementById js/document "canvas") "2d")]
    (doseq [n (take 10 (repeatedly 1)) ]
      (.fillRect context (* n 15) (* n 15) 10 10))))

(defn neighbors-locations [[x y]]
  (into #{} (for [sumx #{-1 1 0} sumy #{-1 1 0}
                  :when (not (and (= sumx 0) (= sumy 0)))]
              [(+ x sumx) (+ y sumy)])))

(defn neighbors-per-cell [world]
  (reduce
   (fn [map cell]
     (let [cell-neighbors (neighbors-locations cell)]
       (assoc map cell {:living (set/intersection cell-neighbors world)
                        :dead (set/difference cell-neighbors world)})))
   {}
   world))

(defn living-cells [neighbors-per-cell]
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
    (set/union (into #{} (living-cells neighbors-per-cell))
               (into #{} (new-born-cells neighbors-per-cell)))))
