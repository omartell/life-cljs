(ns life.test.core-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t])
  (:use [clojure.set :only [union intersection difference subset?]]))

(defn neighbors-locations [[x y]]
  (into #{} (for [sumx #{-1 1 0} sumy #{-1 1 0}
                  :when (not (and (= sumx 0) (= sumy 0)))]
              [(+ x sumx) (+ y sumy)])))

(defn tick [world]
  (let [all-neighbors (reduce
                       (fn [map cell]
                         (let [cell-neighbors (neighbors-locations cell)]
                           (assoc map cell {:living (intersection cell-neighbors world)
                                            :dead (difference cell-neighbors world)})))
                       {}
                       world)

        living-cells (keys (filter (fn [[cell cell-neighbors]]
                                     (#{2 3} (count (:living cell-neighbors))))
                                   all-neighbors))

        all-dead-cells (mapcat (fn [[cell cell-neighbors]]
                                 (into [] (:dead cell-neighbors)))
                               all-neighbors)

        new-born-cells (keys (filter (fn [[cell instances]]
                                       (#{3} (count instances)))
                                     (group-by identity all-dead-cells)))]

    (union (into #{} living-cells) (into #{} new-born-cells))))

;; Cell Coordinates
;; [0,0 1,0 2,0 3,0
;;  0,1 1,1 2,1 3,1
;;  0,2 1,2 2,2 3,2
;;  0,3 1,3 2,3 3,3]

(deftest a-tick-produces-the-next-generation
  (testing "A cell with no neighbors dies on the next tick"
    (is (= #{}
           (tick #{[0 1]}))))

  (testing "A cell with one living neighbor dies on the next tick"
    (is (= #{}
           (tick #{[1 1] [1 0]}))))

  (testing "A cell with two or three living neighbors lives on the next tick"
    (testing "one neighbor on top and one under"
      (is (subset? #{[1 1]}
                   (tick #{[1 1] [1 0] [1 2]}))))

    (testing "one neighbor left and one right"
      (is (subset? #{[1 2]}
             (tick #{[1 2] [0 2] [2 2]}))))

    (testing "three neighbors including diagonals"
      (is (subset? #{[1 2] [0 2] [2 2] [1 3]}
                   (tick #{[1 2] [0 2] [2 2] [1 3]})))))

  (testing "A cell with more than three neighbors dies by overpopulation"
    (is (subset? #{[0 2] [2 2] [1 3] [1 1]}
                 (tick #{[1 2] [0 2] [2 2] [1 3] [1 1]}))))

  (testing "A new cell is born when there are three neighbors"
    (is (= #{[1 1] [0 1] [2 1]}
           (tick #{[1 0] [1 1] [1 2]})))))
