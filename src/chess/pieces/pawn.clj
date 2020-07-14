(ns chess.pieces.pawn
  (:require [schema.core :as s]
            [chess.schemata.piece :as s.piece]))

(s/defn ^:private possible-movements-white-pawn
  [{:keys [position movements]} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (let [line   (:line position)
        column (:column position)]
    ; use cond->
    (-> []
        (conj {:line   (+ line 1)
               :column column}
              {:line   (+ line 2)
               :column column}))))

(s/defn ^:private possible-movements-black-pawn
  [{:keys [position movements]} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (let [line   (:line position)
        column (:column position)]
    ; use cond->
    (-> []
        (conj {:line   (- line 1)
               :column column}
              {:line   (- line 2)
               :column column}))))

(s/defn possible-movements :- [s.piece/Position]
  [{:keys [color] :as piece} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (if (= color :white)
    (possible-movements-white-pawn piece pieces)
    (possible-movements-black-pawn piece pieces)))
