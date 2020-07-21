(ns chess.board
  (:require [chess.game :as game]
            [chess.schemata.board :as s.board]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s]))

(def abc ["a" "b" "c" "d" "e" "f" "g" "h"])

(s/defn column-letter-from-board :- s/Str
  [column :- s/Int]
  (nth abc (- column 1)))

(s/defn column-letter-from-piece :- (s/maybe s/Str)
  [column :- s/Int]
  (when (and (>= column 0)
             (< column 8))
    (nth abc column)))

(s/defn next-column :- (s/maybe s/Str)
  ([letter :- s/Str]
   (next-column letter 1))
  ([letter :- s/Str
    number-of-columns :- s/Int]
   (-> abc
       (.indexOf letter)
       (+ number-of-columns)
       column-letter-from-piece)))

(s/defn previous-column :- (s/maybe s/Str)
  ([letter :- s/Str]
   (previous-column letter 1))
  ([letter :- s/Str
    number-of-columns :- s/Int]
   (-> abc
       (.indexOf letter)
       (- number-of-columns)
       column-letter-from-piece)))

(s/defn line-number :- s/Str
  [line :- s/Int]
  (- 8 line))

(s/defn y-axis? :- s/Bool
  [line :- s/Int
   column :- s/Int]
  (and (= column 0)
       (< line 8)))

(s/defn x-axis? :- s/Bool
  [line :- s/Int
   column :- s/Int]
  (and (= line 8)
       (> column 0)))

(s/defn position-inside-board? :- s/Bool
  [{:keys [line column]} :- s.piece/Position]
  (and (>= line 1)
       (<= line 8)
       (>= (.indexOf abc column) 0)
       (< (.indexOf abc column) 8)))

(s/defn ^:private inside-board? :- s/Bool
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
       (= column (column-letter-from-board board-column))))

(s/defn piece-at-position? :- s/Bool
  [{:keys [line column]} :- s.piece/Position
   board-line :- s/Int
   board-column :- s/Str]
  (and (= line board-line)
       (= column board-column)))

(s/defn find-piece-at-board-position :- (s/maybe s.piece/Piece)
  [pieces :- (s/maybe [s.piece/Piece])
   line :- s/Int
   column :- s/Int]
  (let [piece (filter #(piece-at-board-position? (:position %) line column) pieces)]
    (first piece)))

(s/defn find-piece-at-position :- (s/maybe s.piece/Piece)
  [{:keys [line column]} :- s.piece/Position
   pieces :- (s/maybe [s.piece/Piece])]
  (let [piece (filter #(piece-at-position? (:position %) line column) pieces)]
    (first piece)))

(s/defn possible-movement? :- s/Bool
  [possible-movements :- [s.piece/Position]
   line :- s/Int
   column :- s/Int]
  (let [piece (filter #(piece-at-board-position? % line column) possible-movements)]
    (first piece)))

(s/defn ->cell :- s.board/Cell
  [piece :- s/Str
   movement? :- s/Bool]
  {:value     piece
   :movement? movement?})

(s/defn value-at-position :- s.board/Cell
  [piece :- (s/maybe s.piece/Piece)
   line :- s/Int
   column :- s/Int
   possible-movements :- (s/maybe [s.piece/Position])]
  (cond
    (y-axis? line column)
    (->cell (line-number line) false)

    (x-axis? line column)
    (->cell (column-letter-from-board column) false)

    (possible-movement? possible-movements line column)
    (->cell (game/piece-name piece) true)

    :else
    (->cell (game/piece-name piece) false)))

(s/defn last-column? :- s/Bool
  [column :- s/Int]
  (= 8 column))
