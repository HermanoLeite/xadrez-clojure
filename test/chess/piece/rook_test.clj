(ns chess.piece.rook-test
  (:require [clojure.test :refer :all]
            [chess.pieces.rook :as rook]
            [chess.fixtures.board :as f.board]))

(def white-rook-on-line-7 (assoc-in f.board/white-rook [:position :line] 7))
(def white-rook-on-line-6 (assoc-in f.board/white-rook [:position :line] 6))
(def white-pawn-on-line-7 (assoc-in f.board/white-pawn [:position :line] 7))
(def black-pawn-on-line-7 (assoc white-pawn-on-line-7 :color :black))

(def position-8a {:line 8 :column "a"})
(def position-7a {:line 7 :column "a"})
(def off-board-position {:line 9 :column "a"})

(def moves-to-top [{:line 2 :column "a"} {:line 3 :column "a"} {:line 4 :column "a"} {:line 5 :column "a"}
                   {:line 6 :column "a"} {:line 7 :column "a"} {:line 8 :column "a"}])

(def moves-to-top-but-last [{:line 2 :column "a"} {:line 3 :column "a"} {:line 4 :column "a"} {:line 5 :column "a"}
                            {:line 6 :column "a"} {:line 7 :column "a"}])

(def moves-to-right [{:line 1 :column "b"} {:line 1 :column "c"} {:line 1 :column "d"} {:line 1 :column "e"}
                     {:line 1 :column "f"} {:line 1 :column "g"} {:line 1 :column "h"}])

(deftest inline-possible-capture-and-move-actions
  (testing "one movement to top when in line 7 and no piece above"
    (is (= [position-8a]
           (rook/inline-possible-move-actions white-rook-on-line-7 [white-rook-on-line-7] rook/vertical-movements +))))

  (testing "two movement to top when in line 6 and an enemy on line 7"
    (is (= [position-7a]
           (rook/inline-possible-move-actions white-rook-on-line-6 [white-rook-on-line-6 black-pawn-on-line-7] rook/vertical-movements +)))))

(deftest rook-can-capture-or-move-to-this-position?
  (testing "movement to a position with no piece - true"
    (is (true?
          (rook/possible-move-action? [white-rook-on-line-7] position-8a))))
  (testing "movement to a position with any piece - false"
    (is (false?
          (rook/possible-move-action? [black-pawn-on-line-7] position-7a))))

  (testing "movement to a position with same side piece - false"
    (is (false?
          (rook/possible-move-action? [white-pawn-on-line-7] position-7a))))

  (testing "movement to a position off board - true (shouldnt happen)"
    (is (true?
          (rook/possible-move-action? [white-pawn-on-line-7] off-board-position)))))

(deftest possible-move-or-capture-actions
  (testing "rook on the bottom left corner can move up and right"
    (is (= (concat moves-to-top moves-to-right)
           (rook/possible-movements f.board/white-rook [f.board/white-rook]))))

  (testing "rook on the bottom left corner can move up and right even with enemy piece in the middle"
    (is (= (set (concat moves-to-top-but-last moves-to-right))
           (set (rook/possible-movements f.board/white-rook [f.board/white-rook black-pawn-on-line-7]))))))
