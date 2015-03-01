(ns life.test.core-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t]
            [life.core :as life]
            [clojure.set :as set]))

;; Cell Coordinates
;; [0,0 1,0 2,0 3,0 4,0 5,0 6,0 7,0 8,0
;;  0,1 1,1 2,1 3,1 4,1 5,1 6,1 7,1 8,1
;;  0,2 1,2 2,2 3,2 4,2 5,2 6,2 7,2 8,2
;;  0,3 1,3 2,3 3,3 4,3 5,3 6,3 7,3 8,3
;;  0,4 1,4 2,4 3,4 4,4 5,4 6,4 7,4 8,4
;;  0,5 1,5 2,5 3,5 4,5 5,5 6,5 7,5 8,5
;;  0,6 1,6 2,6 3,6 4,6 5,6 6,6 7,6 8,6
;;  0,7 1,7 2,7 3,7 4,7 5,7 6,7 7,7 8,7
;;  0,8 1,8 2,8 3,8 4,8 5,8 6,8 7,8 8,8]

(deftest a-tick-produces-the-next-generation
  (testing "A cell with no neighbors dies on the next tick"
    (is (= #{}
           (life/tick #{[0 1]}))))

  (testing "A cell with one living neighbor dies on the next tick"
    (is (= #{}
           (life/tick #{[1 1] [1 0]}))))

  (testing "A cell with two or three living neighbors lives on the next tick"
    (testing "one neighbor on top and one under"
      (is (set/subset? #{[1 1]}
                       (life/tick #{[1 1] [1 0] [1 2]}))))

    (testing "one neighbor left and one right"
      (is (set/subset? #{[1 2]}
                       (life/tick #{[1 2] [0 2] [2 2]}))))

    (testing "three neighbors including diagonals"
      (is (set/subset? #{[1 2] [0 2] [2 2] [1 3]}
                       (life/tick #{[1 2] [0 2] [2 2] [1 3]})))))

  (testing "A cell with more than three neighbors dies by overpopulation"
    (is (set/subset? #{[0 2] [2 2] [1 3] [1 1]}
                     (life/tick #{[1 2] [0 2] [2 2] [1 3] [1 1]}))))

  (testing "A new cell is born when there are three neighbors"
    (is (= #{[1 1] [0 1] [2 1]}
           (life/tick #{[1 0] [1 1] [1 2]})))))

(deftest known-patterns
  (testing "A known pattern"
    (is (= #{[13 -1] [13 0] [13 1] [14 1]
             [9 3] [10 3] [11 3] [11 4]
             [14 3] [15 3] [16 3] [15 4]
             [13 4] [13 5] [14 5] [13 6]}

           (life/tick #{[12,0] [13,0] [14,0]
                        [12,5] [13,5] [14,5]
                        [10,2] [10,3] [10,4]
                        [15,2] [15,3] [15,4]})))))
