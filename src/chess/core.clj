(ns chess.core
  (:require [chess.board :as board]
            [chess.console :as console]
            [chess.pieces.bishop :as bishop]
            [chess.pieces.knight :as knight]
            [chess.pieces.king :as king]
            [chess.pieces.queen :as queen]
            [chess.pieces.pawn :as pawn]
            [chess.pieces.rook :as rook]
            [chess.game :as game]
            [chess.schemata.piece :as s.piece]
            [chess.schemata.board :as s.board]
            [schema.core :as s])
  (:gen-class))

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

(s/defn possible-movement? :- s/Bool
  [possible-movements :- [s.piece/Position]
   line :- s/Int
   column :- s/Int]
  (let [piece (filter #(board/piece-at-board-position? % line column) possible-movements)]
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
    (board/y-axis? line column)
    (->cell (board/line-number line) false)

    (board/x-axis? line column)
    (->cell (board/column-letter-from-board column) false)

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
  (let [piece (board/find-piece-at-board-position pieces line column)
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
  [{:keys [piece] :as piece-to-move} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (case piece
    :pawn
    (pawn/possible-movements piece-to-move pieces)

    :knight
    (knight/possible-movements piece-to-move pieces)

    :bishop
    (bishop/possible-movements piece-to-move pieces)

    :rook
    (rook/possible-movements piece-to-move pieces)

    :queen
    (queen/possible-movements piece-to-move pieces)

    :king
    (king/possible-movements piece-to-move pieces)

    []))

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
   position :- s.piece/Position]
  (let [captured-piece                  (board/find-piece-at-position position pieces)
        pieces-without-pieces-to-remove (remove #(or (= % piece-to-move)
                                                     (= % captured-piece)) pieces)
        uptaded-movements               (-> piece-to-move :movements (+ 1))
        piece-at-new-position           (-> piece-to-move
                                            (assoc :position position)
                                            (assoc :movements uptaded-movements))]
    (conj pieces-without-pieces-to-remove piece-at-new-position)))

(s/defn pass-turn :- s.piece/Color
  [turn :- s.piece/Color]
  (if (= turn :white)
    :black
    :white))

(s/defn game
  [warn :- s/Str
   turn :- s.piece/Color
   pieces :- [s.piece/Piece]]
  (console/clean-console)
  (println "TURN:" (name turn))
  (println warn)
  (print-board! pieces)
  (let [piece-to-move      (console/read-piece-to-move! "Which piece will u move?" pieces turn)
        possible-movements (possible-movements piece-to-move pieces)
        next-turn          (pass-turn turn)]
    (if (empty? possible-movements)
      (game "No movements for that piece, sorry." turn pieces)
      (print-board! pieces possible-movements))
    (->> (console/read-movement! "To where?" possible-movements)
         (move pieces piece-to-move)
         (game "" next-turn))))

(defn -main
  "chess, mate!"
  [& args]
  (let [pieces game/pieces]
    (game "Start!" :white pieces)))
