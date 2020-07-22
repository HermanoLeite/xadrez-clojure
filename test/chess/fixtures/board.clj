(ns chess.fixtures.board)

(def white-rook {:position  {:line 1 :column "a"}
                 :color     :white
                 :piece     :rook
                 :movements 0})

(def white-rook-5c {:position  {:line 5 :column "c"}
                    :color     :white
                    :piece     :rook
                    :movements 0})

(def black-rook-1a (assoc white-rook :color :black))

(def white-pawn {:position  {:line 1 :column "a"}
                 :color     :white
                 :piece     :pawn
                 :movements 0})

(def white-king-1d {:position  {:line 1 :column "d"}
                    :color     :white
                    :piece     :king
                    :movements 0})

(def pieces-in-xeque-pawn [{:position  {:line 2 :column "c"}
                            :color     :black
                            :piece     :pawn
                            :movements 0}
                           white-king-1d
                           {:position  {:line 1 :column "c"}
                            :color     :white
                            :piece     :pawn
                            :movements 0}])

(def pieces-in-xeque-)

(def white-pieces-in-xeque-2-rooks [(assoc-in black-rook-1a [:position :line] 2)
                                    black-rook-1a
                                    white-king-1d])

(def white-pieces-in-xeque-2-rooks-can-be-saved [(assoc-in black-rook-1a [:position :line] 2)
                                                 black-rook-1a
                                                 white-king-1d
                                                 white-rook-5c])

(def white-pieces-in-xeque-2-rooks-cant-be-saved [{:position {:line 1, :column "a"}, :color :white, :piece :rook, :movements 0}
                                                  white-king-1d
                                                  {:position {:line 1, :column "h"}, :color :black, :piece :rook, :movements 0}
                                                  {:position {:line 2, :column "h"}, :color :black, :piece :rook, :movements 0}])
