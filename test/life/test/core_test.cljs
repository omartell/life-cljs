(ns life.test.core-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t]
            [life.core :as life]
            [clojure.set :as set]))

;; Cell Coordinates
;; [0,0 1,0 2,0 3,0
;;  0,1 1,1 2,1 3,1
;;  0,2 1,2 2,2 3,2
;;  0,3 1,3 2,3 3,3]
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
