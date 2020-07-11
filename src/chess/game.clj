(ns chess.game)

(def pieces
  [{:position {:line 8 :column "a"} :color :white :piece :rook}
   {:position {:line 8 :column "b"} :color :white :piece :knight}
   {:position {:line 8 :column "c"} :color :white :piece :bishop}
   {:position {:line 8 :column "d"} :color :white :piece :queen}
   {:position {:line 8 :column "e"} :color :white :piece :king}
   {:position {:line 8 :column "f"} :color :white :piece :bishop}
   {:position {:line 8 :column "g"} :color :white :piece :knight}
   {:position {:line 8 :column "h"} :color :white :piece :rook}
   {:position {:line 7 :column "a"} :color :white :piece :pawn}
   {:position {:line 7 :column "b"} :color :white :piece :pawn}
   {:position {:line 7 :column "c"} :color :white :piece :pawn}
   {:position {:line 7 :column "d"} :color :white :piece :pawn}
   {:position {:line 7 :column "e"} :color :white :piece :pawn}
   {:position {:line 7 :column "f"} :color :white :piece :pawn}
   {:position {:line 7 :column "g"} :color :white :piece :pawn}
   {:position {:line 7 :column "h"} :color :white :piece :pawn}
   {:position {:line 1 :column "a"} :color :black :piece :rook}
   {:position {:line 1 :column "b"} :color :black :piece :knight}
   {:position {:line 1 :column "c"} :color :black :piece :bishop}
   {:position {:line 1 :column "d"} :color :black :piece :queen}
   {:position {:line 1 :column "e"} :color :black :piece :king}
   {:position {:line 1 :column "f"} :color :black :piece :bishop}
   {:position {:line 1 :column "g"} :color :black :piece :knight}
   {:position {:line 1 :column "h"} :color :black :piece :rook}
   {:position {:line 2 :column "a"} :color :black :piece :pawn}
   {:position {:line 2 :column "b"} :color :black :piece :pawn}
   {:position {:line 2 :column "c"} :color :black :piece :pawn}
   {:position {:line 2 :column "d"} :color :black :piece :pawn}
   {:position {:line 2 :column "e"} :color :black :piece :pawn}
   {:position {:line 2 :column "f"} :color :black :piece :pawn}
   {:position {:line 2 :column "g"} :color :black :piece :pawn}
   {:position {:line 2 :column "h"} :color :black :piece :pawn}])
