(ns chess.board-test
  (:require [clojure.test :refer :all]
            [chess.board :as board]
            [chess.fixtures.board :as f.board]))

(deftest find-piece-at-position
  (testing "nil if no piece in position"
    (is (nil?
          (board/find-piece-at-position {:line 2 :column "a"} [f.board/white-pawn]))))

  (testing "piece if piece in position"
    (is (= f.board/white-pawn
           (board/find-piece-at-position {:line 1 :column "a"} [f.board/white-pawn])))))
