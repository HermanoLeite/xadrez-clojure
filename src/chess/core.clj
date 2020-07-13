(ns chess.core
  (:require [chess.console :as console]
            [chess.game :as game]
            [chess.schemata.piece :as s.piece]
            [chess.schemata.board :as s.board]
            [clojure.string :as str]
            [schema.core :as s])
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

(s/defn piece-at-position :- s.board/Cell
  [piece :- (s/maybe s.piece/Piece)
   line :- s/Int
   column :- s/Int
   possible-movements :- (s/maybe [s.piece/Position])]
  (cond
    (y-axis? line column)
    (->cell (line-number line) false)

    (x-axis? line column)
    (->cell (column-letter column) false)

    (possible-movement? possible-movements line column)
    (->cell (piece-name piece) true)

    :else
    (->cell (piece-name piece) false)))

(s/defn last-column? :- s/Bool
  [column :- s/Int]
  (= 8 column))

(def background-black "\033[40m")
(def background-blue "\033[44m")
(def letter-white "\u001B[37m")
(def letter-red "\u001B[31m")

(s/defn color :- s/Str
  [{:keys [color]} :- s.piece/Piece]
  (if (= color :black)
    letter-red
    letter-white))

(s/defn background :- s/Str
  [{:keys [movement?]} :- s.board/Cell]
  (if movement?
    background-blue
    background-black))

(s/defn ->print :- s.board/Print
  [piece :- s.piece/Piece
   value :- s.board/Cell
   last-column? :- s/Bool]
  {:color        (color piece)
   :background   (background value)
   :value        (-> value :value (str " "))
   :last-column? last-column?})

(s/defn print-piece-at-position :- s.board/Print
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])
   line :- s/Int
   column :- s/Int]
  (let [piece (find-piece-at-board-position pieces line column)
        value (piece-at-position piece line column possible-movements)]
    (->print piece value (last-column? column))))

(s/defn columns
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])
   line :- s/Int]
  (map #(print-piece-at-position pieces possible-movements line %) (range 0 9)))

(s/defn lines
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])]
  (map #(columns pieces possible-movements %) (range 0 9)))

(s/defn possible-movements :- (s/maybe [s.piece/Position])
  [{:keys [piece position]} :- s.piece/Piece]
  (let [line   (:line position)
        column (:column position)]
    (case piece
      :pawn
      (-> []
          (conj {:line   (+ line 1)
                 :column column}
                {:line   (+ line 2)
                 :column column}))
      [])))

(s/defn board
  [pieces :- [s.piece/Piece]
   possible-movements :- [s.piece/Position]]
  (lines pieces possible-movements))

(s/defn print-board!
  ([pieces :- [s.piece/Piece]]
   (let [board (board pieces nil)]
     (console/print! board)))

  ([pieces :- [s.piece/Piece]
    possible-movements :- [s.piece/Position]]
   (let [board (board pieces possible-movements)]
     (console/print! board))))

(s/defn move :- [s.piece/Piece]
  [pieces :- [s.piece/Piece]
   piece-to-move :- s.piece/Piece
   {:keys [line column]} :- s.piece/Position]
  (let [pieces-without-piece-to-move (remove #(= % piece-to-move) pieces)
        piece-at-new-position        (-> piece-to-move
                                         (assoc-in [:position :line] line)
                                         (assoc-in [:position :column] column))]
    (conj pieces-without-piece-to-move piece-at-new-position)))

(s/defn pass-turn :- s.piece/Color
  [turn :- s.piece/Color]
  (if (= turn :white)
    :black
    :white))

(s/defn read-piece-to-move! :- s.piece/Piece
  [text :- s/Str
   pieces :- [s.piece/Piece]
   turn :- s.piece/Color]
  (let [position      (console/read! text)
        piece-to-move (find-piece-at-position pieces position)
        color         (:color piece-to-move)]
    (if (and (some? color)
             (= color turn))
      piece-to-move
      (read-piece-to-move! (str "You have to choose a piece from the " (name turn) " color. Try again!") pieces turn))))

(s/defn game
  [turn :- s.piece/Color
   pieces :- [s.piece/Piece]]
  (console/clean-console)
  (println "TURN:" (name turn))
  (print-board! pieces)
  (let [piece-to-move      (read-piece-to-move! "Which piece will u move?" pieces turn)
        possible-movements (possible-movements piece-to-move)
        next-turn          (pass-turn turn)]
    (print-board! pieces possible-movements)
    (->> (console/read-movement! "To where?" possible-movements)
         (move pieces piece-to-move)
         (game next-turn))))

(defn -main
  "chess, mate!"
  [& args]
  (let [pieces game/pieces]
    (game :white pieces)))
