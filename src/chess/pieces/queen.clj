(ns chess.pieces.queen
  (:require [chess.pieces.bishop :as bishop]
            [chess.pieces.rook :as rook]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s]))

(s/defn possible-movements :- [s.piece/Position]
  [piece :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (let [inline-movements (rook/possible-move-actions piece pieces)
        diogonal-movements (bishop/possible-movements piece pieces)]
    (concat inline-movements diogonal-movements)))
