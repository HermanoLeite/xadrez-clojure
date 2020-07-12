(ns chess.game)

(def pieces
  [{:position {:line 8 :column "a"} :color :white :piece :rook :movements 0}
   {:position {:line 8 :column "b"} :color :white :piece :knight :movements 0}
   {:position {:line 8 :column "c"} :color :white :piece :bishop :movements 0}
   {:position {:line 8 :column "d"} :color :white :piece :queen :movements 0}
   {:position {:line 8 :column "e"} :color :white :piece :king :movements 0}
   {:position {:line 8 :column "f"} :color :white :piece :bishop :movements 0}
   {:position {:line 8 :column "g"} :color :white :piece :knight :movements 0}
   {:position {:line 8 :column "h"} :color :white :piece :rook :movements 0}
   {:position {:line 7 :column "a"} :color :white :piece :pawn :movements 0}
   {:position {:line 7 :column "b"} :color :white :piece :pawn :movements 0}
   {:position {:line 7 :column "c"} :color :white :piece :pawn :movements 0}
   {:position {:line 7 :column "d"} :color :white :piece :pawn :movements 0}
   {:position {:line 7 :column "e"} :color :white :piece :pawn :movements 0}
   {:position {:line 7 :column "f"} :color :white :piece :pawn :movements 0}
   {:position {:line 7 :column "g"} :color :white :piece :pawn :movements 0}
   {:position {:line 7 :column "h"} :color :white :piece :pawn :movements 0}
   {:position {:line 1 :column "a"} :color :black :piece :rook :movements 0}
   {:position {:line 1 :column "b"} :color :black :piece :knight :movements 0}
   {:position {:line 1 :column "c"} :color :black :piece :bishop :movements 0}
   {:position {:line 1 :column "d"} :color :black :piece :queen :movements 0}
   {:position {:line 1 :column "e"} :color :black :piece :king :movements 0}
   {:position {:line 1 :column "f"} :color :black :piece :bishop :movements 0}
   {:position {:line 1 :column "g"} :color :black :piece :knight :movements 0}
   {:position {:line 1 :column "h"} :color :black :piece :rook :movements 0}
   {:position {:line 2 :column "a"} :color :black :piece :pawn :movements 0}
   {:position {:line 2 :column "b"} :color :black :piece :pawn :movements 0}
   {:position {:line 2 :column "c"} :color :black :piece :pawn :movements 0}
   {:position {:line 2 :column "d"} :color :black :piece :pawn :movements 0}
   {:position {:line 2 :column "e"} :color :black :piece :pawn :movements 0}
   {:position {:line 2 :column "f"} :color :black :piece :pawn :movements 0}
   {:position {:line 2 :column "g"} :color :black :piece :pawn :movements 0}
   {:position {:line 2 :column "h"} :color :black :piece :pawn :movements 0}])
