(ns chess.console
  (:require [chess.board :as board]
            [chess.schemata.board :as s.board]
            [chess.schemata.piece :as s.piece]
            [clojure.string :as str]
            [schema.core :as s]))

(s/defn ^:private print-element!
  [{:keys [value background color last-column?]} :- s.board/Print]
  (if last-column?
    (println background color value)
    (print background color value)))

(s/defn ^:private print-line!
  [line]
  (run! print-element! line))

(s/defn print! [board]
  (run! print-line! board))

(s/defn clean-console []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H")))

(s/defn read! :- s.piece/Position
  [text :- s/Str]
  (println text)
  (let [input  (str/split (read-line) #"")
        line   (Integer/parseInt (first input))
        column (second input)]
    {:line line :column column}))

(s/defn read-piece-to-move! :- s.piece/Piece
  [text :- s/Str
   pieces :- [s.piece/Piece]
   turn :- s.piece/Color]
  (let [position      (read! text)
        piece-to-move (board/find-piece-at-position position pieces)
        color         (:color piece-to-move)]
    (if (and (some? color)
             (= color turn))
      piece-to-move
      (read-piece-to-move! (str "You have to choose a piece from the " (name turn) " color. Try again!") pieces turn))))

(s/defn read-movement! :- s.piece/Position
  [text :- s/Str
   possible-movements :- [s.piece/Position]]
  (let [movement (read! text)]
    (if (some #(= % movement) possible-movements)
      movement
      (read-movement! "Invalid position! Should be in a blue spot :) try again!" possible-movements))))
