(ns chess.piece
  (:require [chess.board :as board]
            [chess.pieces.bishop :as bishop]
            [chess.pieces.king :as king]
            [chess.pieces.knight :as knight]
            [chess.pieces.pawn :as pawn]
            [chess.pieces.queen :as queen]
            [chess.pieces.rook :as rook]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s]
            [chess.game :as game]))

(s/defn possible-movements :- (s/maybe [s.piece/Position])
  [{:keys [piece color] :as piece-to-move} :- s.piece/Piece
   pieces :- [s.piece/Piece]
   only-capture-movements? :- s/Bool]
  (case piece
    :pawn
    (pawn/possible-movements piece-to-move pieces only-capture-movements?)

    :knight
    (knight/possible-movements piece-to-move pieces)

    :bishop
    (bishop/possible-movements piece-to-move pieces)

    :rook
    (rook/possible-movements piece-to-move pieces)

    :queen
    (queen/possible-movements piece-to-move pieces)

    :king
    (if only-capture-movements?
      (king/possible-movements piece-to-move pieces [])
      (->> pieces
           (filter #(game/enenmy? color %))
           (map #(possible-movements % pieces true))
           (reduce concat)
           (king/possible-movements piece-to-move pieces)))

    []))

(s/defn move :- [s.piece/Piece]
  [pieces :- [s.piece/Piece]
   piece-to-move :- s.piece/Piece
   to-position :- s.piece/Position]
  (let [captured-piece             (board/find-piece-at-position to-position pieces)
        pieces-with-removed-pieces (remove #(or (= % piece-to-move)
                                                (= % captured-piece)) pieces)
        uptaded-movements          (-> piece-to-move :movements (+ 1))
        piece-at-new-position      (-> piece-to-move
                                       (assoc :position to-position)
                                       (assoc :movements uptaded-movements))]
    (conj pieces-with-removed-pieces piece-at-new-position)))

(s/defn xeque? :- s/Bool
  [pieces :- [s.piece/Piece]
   color :- s.piece/Color]
  (let [king            (->> pieces
                             (filter #(and (= :king (:piece %))
                                           (= color (:color %))))
                             first)
        enemy-movements (->> pieces
                             (filter #(game/enenmy? color %))
                             (map #(possible-movements % pieces true))
                             (reduce concat))]
       (some #(= (:position king) %) enemy-movements)))
