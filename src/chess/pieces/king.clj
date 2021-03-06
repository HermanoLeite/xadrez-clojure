(ns chess.pieces.king
  (:require [chess.board :as board]
            [chess.game :as game]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s]))

(s/defn all-possible-movements :- [s.piece/Position]
  [{{:keys [line column]} :position} :- s.piece/Piece]
  (-> []
      (conj {:line (+ line 1) :column (board/previous-column column)})
      (conj {:line (+ line 1) :column column})
      (conj {:line (+ line 1) :column (board/next-column column)})
      (conj {:line line :column (board/next-column column)})
      (conj {:line (- line 1) :column (board/next-column column)})
      (conj {:line (- line 1) :column column})
      (conj {:line (- line 1) :column (board/previous-column column)})
      (conj {:line line :column (board/previous-column column)})))

(s/defn possible-to-move? :- s/Bool
  [color :- s.piece/Color
   pieces :- [s.piece/Piece]
   movement :- s.piece/Position]
  (and (board/position-inside-board? movement)
       (let [piece (board/find-piece-at-position movement pieces)]
         (boolean (or (nil? piece)
                      (game/enenmy? color piece))))))

(s/defn not-enemy-movement? :- s/Bool
  [king-position :- s.piece/Position
   enemy-movements :- [s.piece/Position]]
  (not (some #(= king-position %) enemy-movements)))

(s/defn possible-movements :- [s.piece/Piece]
  [piece :- s.piece/Piece
   pieces :- [s.piece/Piece]
   all-enemy-movements :- [s.piece/Position]]
  (let [all-possible-movements (all-possible-movements piece)
        possible               (->> all-possible-movements
                                    (filter #(possible-to-move? (:color piece) pieces %))
                                    (filter #(not-enemy-movement? % all-enemy-movements)))]
    possible))
