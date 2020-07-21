(ns chess.fixtures.board)

(def white-rook {:position  {:line 1 :column "a"}
                 :color     :white
                 :piece     :rook
                 :movements 0})

(def white-pawn {:position  {:line 1 :column "a"}
                 :color     :white
                 :piece     :pawn
                 :movements 0})

(def white-king {:position  {:line 1 :column "a"}
                 :color     :white
                 :piece     :king
                 :movements 0})

(def pieces-in-xeque-pawn [{:position  {:line 2 :column "c"}
                            :color     :black
                            :piece     :pawn
                            :movements 0}
                           white-king
                           {:position  {:line 1 :column "c"}
                            :color     :white
                            :piece     :pawn
                            :movements 0}])

(def pieces-in-xeque-2-rooks [{:position  {:line 2 :column "a"}
                               :color     :black
                               :piece     :rook
                               :movements 0}
                              {:position  {:line 1 :column "a"}
                               :color     :black
                               :piece     :rook
                               :movements 0}
                              white-king])
