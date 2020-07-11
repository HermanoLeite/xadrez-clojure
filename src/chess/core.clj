(ns chess.core
  (:require [schema.core :as s]
            [chess.schemata.piece :as s.piece]
            [chess.game :as game])

  (:gen-class))

(def abc ["a" "b" "c" "d" "e" "f" "g" "h"])

(s/defn line-number :- s/Str
  [line :- s/Int]
  (- 8 line))

(s/defn column-letter :- s/Str
  [column :- s/Int]
  (nth abc (- column 1)))

(s/defn y-axis? :- s/Bool
  [column :- s/Int
   line :- s/Int]
  (and (= line 0) (< column 8)))

(s/defn x-axis? :- s/Bool
  [column :- s/Int
   line :- s/Int]
  (and (= column 8) (> line 0)))

(s/defn piece-name :- s/Str
  [{:keys [piece]} :- (s/maybe s.piece/Piece)]
  (case piece
    :pawn "P"
    :bishop "B"
    :knight "N"
    :queen "Q"
    :king "K"
    :rook "R"
    "-"))

(s/defn inside-board? :- s/Bool
  [board-line :- s/Int
   board-column :- s/Int]
  (and (>= board-line 0)
       (< board-line 8)
       (> board-column 0)
       (< board-column 9)))

(s/defn piece-at-position? :- s/Bool
  [{{:keys [line column]} :position} :- s.piece/Piece
   board-line :- s/Int
   board-column :- s/Int]
  (and (inside-board? board-line board-column)
       (= (- 8 line) board-line)
       (= column (column-letter board-column))))

(s/defn find-piece-at-position :- (s/maybe s.piece/Piece)
  [pieces :- (s/maybe s.piece/Piece)
   line :- s/Int
   column :- s/Int]
  (let [piece (filter #(piece-at-position? % line column) pieces)]
    (first piece)))

(s/defn piece-at-position :- s/Str
  [piece :- (s/maybe s.piece/Piece)
   line :- s/Int
   column :- s/Int]
  (cond
    (y-axis? line column)
    (line-number line)

    (x-axis? line column)
    (column-letter column)

    :else
    (piece-name piece)))

(s/defn last-column? :- s/Bool
  [column :- s/Int]
  (= 8 column))

(s/defn color :- s/Str
  [{:keys [color]} :- s.piece/Piece]
  (if (= color :black)
    "\u001B[31m"
    "\u001B[37m"))

(s/defn print-piece-at-position :- s/Str
  [pieces :- [s.piece/Piece]
   line :- s/Int
   column :- s/Int]
  (let [piece (find-piece-at-position pieces line column)
        value (-> piece
                  (piece-at-position line column)
                  (str " "))]
    (if (last-column? column)
      (println (color piece) value)
      (print (color piece) value))))

(s/defn line
  [pieces :- [s.piece/Piece]
   line :- s/Int]
  (doall (map #(print-piece-at-position pieces line %) (range 0 9))))

(s/defn print-board
  [pieces :- [s.piece/Piece]]
  (doall (map #(line pieces %) (range 0 9))))

(s/defn board
  [pieces :- [s.piece/Piece]]
  (print-board pieces))

(defn -main
  "chess, mate!"
  [& args]
  (let [pieces game/pieces]
    (board pieces)))
