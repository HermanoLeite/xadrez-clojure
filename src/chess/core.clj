(ns chess.core
  (:require [schema.core :as s])
  (:gen-class))

(def abc ["a" "b" "c" "d" "e" "f" "g" "h"])

(s/defn line-number :- s/Str
  [column :- s/Int]
  (str (- 8 column) " "))

(s/defn column-letter :- s/Str
  [line :- s/Int]
  (str (nth abc (- line 1)) " "))

(s/defn y-axis? :- s/Bool
  [column :- s/Int
   line :- s/Int]
  (and (= line 0) (< column 8)))

(s/defn x-axis? :- s/Bool
  [column :- s/Int
   line :- s/Int]
  (and (= column 8) (> line 0)))

(s/defn piece-at-position :- s/Str
  [line :- s/Int
   column :- s/Int]
  (cond
    (y-axis? line column)
    (line-number line)

    (x-axis? line column)
    (column-letter column)

    :else
    "- "))

(s/defn line
  [line :- s/Int]
  (map #(piece-at-position line %) (range 0 9)))

(def print-line (comp println line))

(def lines (map print-line (range 0 9)))

(def print-board (doall lines))

(defn -main
  "chess, mate!"
  [& args]
  print-board)
