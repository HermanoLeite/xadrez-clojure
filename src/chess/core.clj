(ns chess.core
  (:require [chess.board :as board]
            [chess.console :as console]
            [chess.game :as game]
            [chess.piece :as piece]
            [chess.schemata.board :as s.board]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s])
  (:gen-class))

(s/defn print-piece-at-position :- s.board/Print
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])
   line :- s/Int
   column :- s/Int]
  (let [piece (board/find-piece-at-board-position pieces line column)
        value (board/value-at-position piece line column possible-movements)]
    (console/->print (:color piece) value (board/last-column? column))))

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
     (console/print-board! board)))

  ([pieces :- [s.piece/Piece]
    possible-movements :- [s.piece/Position]]
   (let [board (board pieces possible-movements)]
     (console/print-board! board))))

(s/defn print-end-of-game!
  [turn :- s.piece/Color
   pieces :- [s.piece/Piece]]
  (do (println "xeque mate!")
      (println (str (game/pass-turn turn) " WINS!"))
      (print-board! pieces)))

(s/defn game
  ([turn :- s.piece/Color
    pieces :- [s.piece/Piece]]
   (game "" turn pieces))
  ([warn :- s/Str
    turn :- s.piece/Color
    pieces :- [s.piece/Piece]]
   (if (piece/xeque-mate? turn pieces)
     (print-end-of-game! turn pieces)
     (do (console/print-intro! turn warn (piece/xeque? pieces turn))
         (print-board! pieces)
         (try
           (let [selected-piece     (console/read-piece-to-move! "Which piece will u move?" pieces turn)
                 possible-movements (piece/possible-movements! selected-piece pieces)]
             (print-board! pieces possible-movements)
             (->> (console/read-movement! "To where?" possible-movements)
                  (piece/move! pieces selected-piece)
                  (game (game/pass-turn turn))))
           (catch NumberFormatException e
             (game "Invalid input - input ex: 2a" turn pieces))
           (catch Exception error
             (game (.getMessage error) turn pieces)))))))

(defn -main
  "chess, mate!"
  [& args]
  (let [pieces game/pieces]
    (game "Start!" :white pieces)))
