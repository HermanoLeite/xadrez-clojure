(ns chess.piece
  (:require [chess.board :as board]
            [chess.pieces.bishop :as bishop]
            [chess.pieces.king :as king]
            [chess.pieces.knight :as knight]
            [chess.pieces.pawn :as pawn]
            [chess.pieces.queen :as queen]
            [chess.pieces.rook :as rook]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s]))

(s/defn possible-movements :- (s/maybe [s.piece/Position])
  [{:keys [piece] :as piece-to-move} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (case piece
    :pawn
    (pawn/possible-movements piece-to-move pieces)

    :knight
    (knight/possible-movements piece-to-move pieces)

    :bishop
    (bishop/possible-movements piece-to-move pieces)

    :rook
    (rook/possible-movements piece-to-move pieces)

    :queen
    (queen/possible-movements piece-to-move pieces)

    :king
    (king/possible-movements piece-to-move pieces)

    []))

(s/defn move :- [s.piece/Piece]
  [pieces :- [s.piece/Piece]
   piece-to-move :- s.piece/Piece
   to-position :- s.piece/Position]
  (let [captured-piece                  (board/find-piece-at-position to-position pieces)
        pieces-without-pieces-to-remove (remove #(or (= % piece-to-move)
                                                     (= % captured-piece)) pieces)
        uptaded-movements               (-> piece-to-move :movements (+ 1))
        piece-at-new-position           (-> piece-to-move
                                            (assoc :position to-position)
                                            (assoc :movements uptaded-movements))]
    (conj pieces-without-pieces-to-remove piece-at-new-position)))
