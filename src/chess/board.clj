(ns chess.board
  (:require [schema.core :as s]
            [chess.schemata.piece :as s.piece]))

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

(s/defn inside-board? :- s/Bool
        [board-line :- s/Int
         board-column :- s/Int]
        (and (>= board-line 0)
             (< board-line 8)
             (> board-column 0)
             (< board-column 9)))

(s/defn piece-at-board-position? :- s/Bool
        [{:keys [line column]} :- s.piece/Position
         board-line :- s/Int
         board-column :- s/Int]
        (and (inside-board? board-line board-column)
             (= (- 8 line) board-line)
             (= column (column-letter board-column))))

(s/defn piece-at-position? :- s/Bool
        [{:keys [line column]} :- s.piece/Position
         board-line :- s/Int
         board-column :- s/Str]
        (and (= line board-line)
             (= column board-column)))

(s/defn find-piece-at-board-position :- (s/maybe s.piece/Piece)
        [pieces :- (s/maybe s.piece/Piece)
         line :- s/Int
         column :- s/Int]
        (let [piece (filter #(piece-at-board-position? (:position %) line column) pieces)]
          (first piece)))

(s/defn find-piece-at-position :- (s/maybe s.piece/Piece)
        [pieces :- (s/maybe s.piece/Piece)
         {:keys [line column]} :- s.piece/Position]
        (let [piece (filter #(piece-at-position? (:position %) line column) pieces)]
          (first piece)))
