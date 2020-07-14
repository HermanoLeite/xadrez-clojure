(ns chess.pieces.knight
  (:require [chess.schemata.piece :as s.piece]
            [schema.core :as s]
            [chess.board :as board]
            [chess.game :as game]))

(s/defn all-possible-movements :- [s.piece/Position]
  [{{:keys [line column]} :position} :- s.piece/Piece]
  (-> []
      (conj {:line (+ line 2) :column (board/next-column column)})
      (conj {:line (+ line 2) :column (board/previous-column column)})
      (conj {:line (- line 2) :column (board/next-column column)})
      (conj {:line (- line 2) :column (board/previous-column column)})
      (conj {:line (+ line 1) :column (board/next-column column 2)})
      (conj {:line (+ line 1) :column (board/previous-column column 2)})
      (conj {:line (- line 1) :column (board/next-column column 2)})
      (conj {:line (- line 1) :column (board/previous-column column 2)})))

(s/defn possible-to-move? :- s/Bool
  [color :- s.piece/Color
   pieces :- [s.piece/Piece]
   movement :- s.piece/Position]
  (and (board/piece-inside-board? movement)
       (let [piece (board/find-piece-at-position movement pieces)]
         (boolean (or (nil? piece)
                      (game/enenmy? color piece))))))

(s/defn possible-movements :- [s.piece/Piece]
  [piece :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (let [all-possible-movements (all-possible-movements piece)]
    (filter #(possible-to-move? (:color piece) pieces %) all-possible-movements)))
