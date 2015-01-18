(ns life.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defonce app-state (atom))

(enable-console-print!)

(defn draw-cells []
  (let [context (.getContext (.getElementById js/document "canvas") "2d")]
    (doseq [n (take 10 (repeatedly 1)) ]
      (.fillRect context (* n 15) (* n 15) 10 10))))

(defn main []
  (draw-cells))
