(ns chess.piece-test
  (:require [clojure.test :refer :all]
            [chess.piece :as piece]
            [chess.fixtures.board :as fixture]))

(def white-king-4d {:position  {:line 4 :column "d"}
                    :color     :white
                    :piece     :king
                    :movements 0})

(def white-pieces-in-xeque-bishop [{:position  {:line 6 :column "b"}
                                    :color     :black
                                    :piece     :bishop
                                    :movements 0}
                                   {:position  {:line 3 :column "d"}
                                    :color     :white
                                    :piece     :pawn
                                    :movements 0}
                                   {:position  {:line 5 :column "d"}
                                    :color     :white
                                    :piece     :pawn
                                    :movements 0}
                                   {:position  {:line 4 :column "e"}
                                    :color     :white
                                    :piece     :pawn
                                    :movements 0}
                                   {:position  {:line 4 :column "c"}
                                    :color     :white
                                    :piece     :pawn
                                    :movements 0}
                                   {:position  {:line 3 :column "c"}
                                    :color     :white
                                    :piece     :pawn
                                    :movements 0}
                                   {:position  {:line 5 :column "e"}
                                    :color     :white
                                    :piece     :pawn
                                    :movements 0}
                                   white-king-4d])

(deftest piece-save-xeque-mate?
  (testing "true if it is not in xeque-mate - empty pieces"
    (is (true?
          (piece/piece-save-xeque-mate? fixture/white-king-1d []))))

  (testing "Saves xeque-mate when pawn can get king - king can move"
    (is (true?
          (piece/piece-save-xeque-mate? fixture/white-king-1d fixture/pieces-in-xeque-pawn))))

  (testing "Does not saves check-mate - king is in the last line
            and there are two rooks in position to take the king"
    (is (false?
          (piece/piece-save-xeque-mate? fixture/white-king-1d fixture/white-pieces-in-xeque-2-rooks))))

  (testing "Does not saves check-mate - king is in the last line
            and there are two rooks in position to take the king"
    (is (false?
          (piece/piece-save-xeque-mate? fixture/white-king-1d fixture/white-pieces-in-xeque-2-rooks))))

  (testing "Rook can move and get in front of the atack to king"
    (is (true?
          (piece/piece-save-xeque-mate? fixture/white-rook-5c fixture/white-pieces-in-xeque-2-rooks-can-be-saved)))))

(deftest move-save-check-mate?
  (testing "no possible move to king to save from xeque-mate"
    (is (false?
          (piece/move-save-xaque-mate? fixture/white-pieces-in-xeque-2-rooks fixture/white-king-1d {:line 2 :column "d"}))))
  (testing "king cant save from bishop"
    (is (false?
          (piece/move-save-xaque-mate? white-pieces-in-xeque-bishop white-king-4d {:line 3 :column "e"}))))
  (testing "rook can save xeque by getting in front of the attack"
    (is (true?
          (piece/move-save-xaque-mate? fixture/white-pieces-in-xeque-2-rooks-can-be-saved fixture/white-rook-5c {:line 1 :column "c"})))))

(deftest try-move
  (testing "king can't move as it goes to a xeque position"
    (is (nil?
          (piece/try-move fixture/white-pieces-in-xeque-2-rooks fixture/white-king-1d {:line 2 :column "d"}))))

  (testing "king can't move to the left as it goes to a xeque position"
    (is (nil?
          (piece/try-move fixture/white-pieces-in-xeque-2-rooks fixture/white-king-1d {:line 1 :column "e"}))))

  (testing "king can't move to the left as it goes to a xeque position"
    (is (nil?
          (piece/try-move white-pieces-in-xeque-bishop white-king-4d {:line 3 :column "e"}))))

  (testing "rook can move to save king from xeque"
    (is (= [{:color :white :movements 1 :piece :rook :position {:column "c" :line 1}}
            {:color :black :movements 0 :piece :rook :position {:column "a" :line 2}}
            {:color :black :movements 0 :piece :rook :position {:column "a" :line 1}}
            {:color :white :movements 0 :piece :king :position {:column "d" :line 1}}]
           (piece/try-move fixture/white-pieces-in-xeque-2-rooks-can-be-saved fixture/white-rook-5c {:line 1 :column "c"})))))

(deftest xeque?
  (testing "False if pieces is nil"
    (is (false?
          (piece/xeque? nil :white))))

  (testing "False if pieces is empty"
    (is (false?
          (piece/xeque? [] :white))))

  (testing "false if there is only the king"
    (is (false?
          (piece/xeque? [fixture/white-king-1d] :white))))

  (testing "True if king is in the same line as rook"
    (is (true?
          (piece/xeque? fixture/white-pieces-in-xeque-2-rooks :white))))

  (testing "True if king is in diagonal of bishop"
    (is (true?
          (piece/xeque? white-pieces-in-xeque-bishop :white)))))

(deftest possible-movements
  (testing "bug - possible movements for king to run from rook on the same line"
    (is (= [{:line 1 :column "e"}]
           (piece/possible-movements fixture/white-king-1d fixture/white-pieces-in-xeque-2-rooks))))

  (testing ""
    (is (= [{:line 3 :column "e"}]
           (piece/possible-movements white-king-4d white-pieces-in-xeque-bishop)))))

(deftest only-pieces-of-color
  (testing "empty if no pieces"
    (is (= []
           (piece/only-pieces-of-color :white []))))
  (testing "only white pieces"
    (is (= [fixture/white-king-1d {:position  {:line 1 :column "c"}
                                   :color     :white
                                   :piece     :pawn
                                   :movements 0}]
           (piece/only-pieces-of-color :white fixture/pieces-in-xeque-pawn))))
  (testing "only white pieces"
    (is (= [fixture/white-king-1d]
           (piece/only-pieces-of-color :white fixture/white-pieces-in-xeque-2-rooks)))))

(deftest any-piece-save-xeque-mate?
  (testing ""
    (is (false?
          (piece/any-piece-save-xeque-mate? fixture/white-pieces-in-xeque-2-rooks [fixture/white-king-1d])))))

(deftest xeque-mate?
  (testing "cant save from xeque-mate two rooks"
    (is (false?
          (piece/xeque-mate? :white fixture/white-pieces-in-xeque-2-rooks))))

  (testing "can save from xeque-mate just a pawn"
    (is (true?
          (piece/xeque-mate? :white fixture/pieces-in-xeque-pawn))))

  (testing "can save from xeque-mate white rook can move"
    (is (true?
          (piece/xeque-mate? :white fixture/white-pieces-in-xeque-2-rooks-can-be-saved)))))
