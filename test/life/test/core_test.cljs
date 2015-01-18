(ns life.test.core-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t])
  (:use [clojure.set :only [union intersection difference subset?]]))

(defn find-neighbors [[ax ay :as cell] world]
  (filter (fn [[bx by :as neighbor]]
            (and  (not (= cell neighbor))
                  (#{0 1 -1} (- ax bx))
                  (#{0 1 -1} (- ay by))))
          world))

(defn survivors [world]
  (filter (fn [cell]
            (let [neighbors (find-neighbors cell world)]
              (#{2 3} (count neighbors))))
          world))

(defn tick [world]
  (into #{} (survivors world)))

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
