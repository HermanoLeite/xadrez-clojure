(ns chess.core
  (:require [chess.game :as game]
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

(s/defn column-letter->number :- s/Int
  [column-letter :- s/Str]
  (+ 1 (.indexOf abc column-letter)))

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
   line :- s/Int
   column :- s/Str]
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

(s/defn color :- s/Str
  [{:keys [color]} :- s.piece/Piece]
  (if (= color :black)
    "\u001B[31m"
    "\u001B[37m"))

(s/defn background :- s/Str
  [{:keys [movement?]} :- s.board/Cell]
  (if movement?
    "\033[44m"
    "\033[40m"))

(s/defn print-piece-at-position :- s/Str
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])
   line :- s/Int
   column :- s/Int]
  (let [piece          (find-piece-at-board-position pieces line column)
        value          (-> piece
                           (piece-at-position line column possible-movements))
        value-to-print (-> value
                           :value
                           (str " "))]
    (if (last-column? column)
      (println (color piece) (background value) value-to-print)
      (print (color piece) (background value) value-to-print))))

(s/defn line
  [pieces :- [s.piece/Piece]
   possible-movements :- (s/maybe [s.piece/Position])
   line :- s/Int]
  (doall (map #(print-piece-at-position pieces possible-movements line %) (range 0 9))))

(s/defn print-board
  [pieces :- [s.piece/Piece]
   possible-movements :- [s.piece/Position]]
  (doall (map #(line pieces possible-movements %) (range 0 9))))

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
  ([pieces :- [s.piece/Piece]]
   (print-board pieces nil))
  ([pieces :- [s.piece/Piece]
    piece-to-move :- s.piece/Piece]
   (let [possible-movements (possible-movements piece-to-move)]
     (print-board pieces possible-movements))))

(s/defn clean-console []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H")))

(s/defn origin :- (s/maybe s.piece/Piece)
  [pieces :- [s.piece/Piece]]
  (println "Which piece will u move?")
  (let [input  (str/split (read-line) #"")
        line   (Integer/parseInt (first input))
        column (second input)]
    (find-piece-at-position pieces line column)))

(s/defn desitiny :- s.piece/Position
  []
  (println "To where?")
  (let [input  (str/split (read-line) #"")
        line   (Integer/parseInt (first input))
        column (second input)]
    {:line line :column column}))

(s/defn pieces-after-movement :- [s.piece/Piece]
  [pieces :- [s.piece/Piece]
   piece-to-move :- s.piece/Piece
   {:keys [line column]} :- s.piece/Position]
  (println "To where?")
  (let [pieces-without-piece-to-move (remove #(= % piece-to-move) pieces)
        piece-at-new-position        (-> piece-to-move
                                         (assoc-in [:position :line] line)
                                         (assoc-in [:position :column] column))]

    (conj pieces-without-piece-to-move piece-at-new-position)))

(s/defn game
  [pieces :- [s.piece/Piece]]
  (clean-console)
  (board pieces)
  (let [piece-to-move (origin pieces)]
    (board pieces piece-to-move)
    (->> (desitiny)
         (pieces-after-movement pieces piece-to-move)
         game)))

(defn -main
  "chess, mate!"
  [& args]
  (let [pieces game/pieces]
    (game pieces)))
