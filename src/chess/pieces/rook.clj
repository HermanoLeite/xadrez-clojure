(ns chess.pieces.rook
  (:require [chess.schemata.piece :as s.piece]
            [schema.core :as s]
            [chess.board :as board]
            [chess.game :as game]))

(s/defn possible-move-action? :- s/Bool
  [pieces :- [s.piece/Piece]
   position :- [s.piece/Position]]
  (let [piece (board/find-piece-at-position position pieces)]
    (nil? piece)))

(s/defn vertical-movements :- [s.piece/Position]
  ([position :- s.piece/Position
    line-function]
   (vertical-movements [] position line-function))

  ([possible-movements :- [s.piece/Position]
    {:keys [line column]} :- s.piece/Position
    line-function]
   (let [new-position {:line (line-function line 1) :column column}]
     (if (board/position-inside-board? new-position)
       (vertical-movements (conj possible-movements new-position) new-position line-function)
       possible-movements))))

(s/defn horizontal-movements :- [s.piece/Position]
  ([position :- s.piece/Position
    column-function]
   (horizontal-movements [] position column-function))

  ([possible-movements :- [s.piece/Position]
    {:keys [line column]} :- s.piece/Position
    column-function]
   (let [new-position {:line line :column (column-function column)}]
     (if (board/position-inside-board? new-position)
       (horizontal-movements (conj possible-movements new-position) new-position column-function)
       possible-movements))))

(s/defn inline-possible-move-actions :- [s.piece/Position]
  [{:keys [color position]} :- s.piece/Color
   pieces :- [s.piece/Piece]
   possible-movements-function
   function-to-move]
  (let [movements                        (split-with (partial possible-move-action? pieces) (possible-movements-function position function-to-move))
        movements-until-not-nil-position (first movements)
        first-not-nil-position           (first (second movements))
        first-not-nil-piece              (board/find-piece-at-position first-not-nil-position pieces)]
    (if (game/enenmy? color first-not-nil-piece)
      (conj movements-until-not-nil-position first-not-nil-position)
      movements-until-not-nil-position)))

(s/defn possible-move-actions :- [s.piece/Position]
  [piece :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (let [partial-possible-movements (partial inline-possible-move-actions piece pieces)
        possible-movements         (concat (partial-possible-movements vertical-movements +)
                                           (partial-possible-movements vertical-movements -)
                                           (partial-possible-movements horizontal-movements board/next-column)
                                           (partial-possible-movements horizontal-movements board/previous-column))]
    possible-movements))
