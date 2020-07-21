(ns chess.piece-test
  (:require [clojure.test :refer :all]
            [chess.piece :as piece]
            [chess.fixtures.board :as fixture]))

(deftest save-xeque-mate?
  (testing "true if it is not in xeque-mate - empty pieces"
    (is (= true
           (piece/piece-save-xeque-mate? fixture/white-king [])))))

(deftest save-xeque-mate-1?
  (testing "Saves xeque-mate when pawn can get king - king can move"
    (is (= true
           (piece/piece-save-xeque-mate? fixture/white-king fixture/pieces-in-xeque-pawn)))))

(deftest save-xeque-mate-2?
  (testing "Does not saves check-mate - king is in the last line
            and there are two rooks in position to take the king"
    (is (= false
           (piece/piece-save-xeque-mate? fixture/white-king fixture/pieces-in-xeque-2-rooks)))))

(deftest move-save-check-mate?
  (testing ""
    (is (= false
           (piece/move-save-xaque-mate? fixture/pieces-in-xeque-2-rooks fixture/white-king {:line 2 :column "d"})))))

(deftest try-move
  (testing "king can't move as it goes to a xeque position"
    (is (= nil
           (piece/try-move fixture/pieces-in-xeque-2-rooks fixture/white-king {:line 2 :column "d"})))))

(deftest xeque?
  (testing "False if pieces is nil"
    (is (= false
           (piece/xeque? nil :white))))

  (testing "False if pieces is empty"
    (is (= false
           (piece/xeque? [] :white))))

  (testing "false if there is only the king"
    (is (= false
           (piece/xeque? [fixture/white-king] :white))))

  (testing "True if king is in the same line as rook"
    (is (= true
           (piece/xeque? fixture/pieces-in-xeque-2-rooks :white)))))

(deftest possible-movements
  (is (= []
         (piece/possible-movements fixture/white-king fixture/pieces-in-xeque-2-rooks))))
