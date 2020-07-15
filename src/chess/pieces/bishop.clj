(ns chess.pieces.bishop
  (:require [chess.schemata.piece :as s.piece]
            [schema.core :as s]
            [chess.board :as board]
            [chess.game :as game]))

(s/defn diogonal-movement :- [s.piece/Position]
  ([position :- s.piece/Position
    line-function
    column-function]
   (diogonal-movement [] position line-function column-function))
  ([possible-movements :- [s.piece/Position]
    {:keys [line column]} :- s.piece/Position
    line-function
    column-function]
   (let [new-position {:line (line-function line 1) :column (column-function column)}]
     (if (board/position-inside-board? new-position)
       (diogonal-movement (conj possible-movements new-position) new-position line-function column-function)
       possible-movements))))

(s/defn possible-movements :- [s.piece/Position]
  [piece :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (println "bishop possible movements")
  (let [all-possible-movements (concat (diogonal-movement (:position piece) + board/next-column)
                                       (diogonal-movement (:position piece) + board/previous-column)
                                       (diogonal-movement (:position piece) - board/next-column)
                                       (diogonal-movement (:position piece) - board/previous-column))]
    all-possible-movements))

