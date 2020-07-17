(ns chess.core
  (:require [chess.board :as board]
            [chess.console :as console]
            [chess.game :as game]
            [chess.piece :as piece]
            [chess.schemata.piece :as s.piece]
            [chess.schemata.board :as s.board]
            [schema.core :as s])
  (:gen-class))

(s/defn print-piece-at-position :- s.board/Print
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])
   line :- s/Int
   column :- s/Int]
  (let [piece (board/find-piece-at-board-position pieces line column)
        value (board/piece-at-position piece line column possible-movements)]
    (console/->print piece value (board/last-column? column))))

(s/defn columns
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])
   line :- s/Int]
  (map #(print-piece-at-position pieces possible-movements line %) (range 0 9)))

(s/defn lines
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])]
  (map #(columns pieces possible-movements %) (range 0 9)))

(s/defn board
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])]
  (lines pieces possible-movements))

(s/defn print-board!
  ([pieces :- [s.piece/Piece]]
   (let [board (board pieces nil)]
     (console/print! board)))

  ([pieces :- [s.piece/Piece]
    possible-movements :- [s.piece/Position]]
   (let [board (board pieces possible-movements)]
     (console/print! board))))

(s/defn game
  [warn :- s/Str
   turn :- s.piece/Color
   pieces :- [s.piece/Piece]]
  (console/print-intro! turn warn)
  (print-board! pieces)
  (let [piece-to-move      (console/read-piece-to-move! "Which piece will u move?" pieces turn)
        possible-movements (piece/possible-movements piece-to-move pieces)
        next-turn          (game/pass-turn turn)]
    (if (empty? possible-movements)
      (game "No movements for that piece, sorry." turn pieces)
      (print-board! pieces possible-movements))
    (->> (console/read-movement! "To where?" possible-movements)
         (piece/move pieces piece-to-move)
         (game "" next-turn))))

(defn -main
  "chess, mate!"
  [& args]
  (let [pieces game/pieces]
    (game "Start!" :white pieces)))
