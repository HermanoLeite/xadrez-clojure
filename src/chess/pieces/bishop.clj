(ns chess.pieces.bishop
  (:require [chess.schemata.piece :as s.piece]
            [schema.core :as s]
            [chess.board :as board]
            [chess.game :as game]))

(s/defn possible-movement? :- s/Bool
  [pieces :- [s.piece/Piece]
   position :- [s.piece/Position]]
  (let [piece (board/find-piece-at-position position pieces)]
    (nil? piece)))

(s/defn diogonal-movements :- [s.piece/Position]
  ([position :- s.piece/Position
    line-function
    column-function]
   (diogonal-movements [] position line-function column-function))
  ([possible-movements :- [s.piece/Position]
    {:keys [line column]} :- s.piece/Position
    line-function
    column-function]
   (let [new-position {:line (line-function line 1) :column (column-function column)}]
     (if (board/position-inside-board? new-position)
       (diogonal-movements (conj possible-movements new-position) new-position line-function column-function)
       possible-movements))))

(s/defn possible-diogonal-movements
  [color :- s.piece/Color
   pieces :- [s.piece/Piece]
   position :- s.piece/Position
   line-function
   column-function]
  (let [movements              (split-with (partial possible-movement? pieces) (diogonal-movements position line-function column-function))
        first-part             (first movements)
        first-not-nil-position (first (second movements))
        first-not-nill-piece   (board/find-piece-at-position first-not-nil-position pieces)]
    (if (game/enenmy? color first-not-nill-piece)
      (conj first-part first-not-nil-position)
      first-part)))

(s/defn possible-movements :- [s.piece/Position]
  [{:keys [color] :as piece} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (println "bishop possible movements")
  (let [partial-possible-diogonal-movements (partial possible-diogonal-movements color pieces (:position piece))
        possible-movements                  (concat (partial-possible-diogonal-movements + board/next-column)
                                                    (partial-possible-diogonal-movements + board/previous-column)
                                                    (partial-possible-diogonal-movements - board/next-column)
                                                    (partial-possible-diogonal-movements - board/previous-column))]
    possible-movements))

