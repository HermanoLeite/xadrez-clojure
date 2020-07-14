(ns chess.pieces.pawn
  (:require [chess.board :as board]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s]
            [chess.game :as game]))

(s/defn ^:private possible-movements-white-pawn
  [{:keys [position movements color]} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (let [line   (:line position)
        column (:column position)]
    (cond-> []
      ;top
      (nil? (-> position
                (assoc :line (+ line 1))
                (board/find-piece-at-position pieces)))
      (conj {:line   (+ line 1)
             :column column})
      ;double top
      (and (nil? (-> position
                     (assoc :line (+ line 2))
                     (board/find-piece-at-position pieces)))
           (= movements 0))
      (conj {:line   (+ line 2)
             :column column})
      ;top left
      (game/enenmy? color (-> position
                              (assoc :line (+ line 1))
                              (assoc :column (board/previous-letter column))
                              (board/find-piece-at-position pieces)))
      (conj {:line   (+ line 1)
             :column (board/previous-letter column)})
      ;top right
      (game/enenmy? color (-> position
                              (assoc :line (+ line 1))
                              (assoc :column (board/next-letter column))
                              (board/find-piece-at-position pieces)))
      (conj {:line   (+ line 1)
             :column (board/next-letter column)}))))

(s/defn ^:private possible-movements-black-pawn
  [{:keys [position movements color]} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (let [line   (:line position)
        column (:column position)]
    (cond-> []
      ; bottom
      (nil? (-> position
                (assoc :line (- line 1))
                (board/find-piece-at-position pieces)))
      (conj {:line   (- line 1)
             :column column})
      ;double bottom
      (and (nil? (-> position
                     (assoc :line (- line 2))
                     (board/find-piece-at-position pieces)))
           (= movements 0))
      (conj {:line   (- line 2)
             :column column})
      ;bottom left
      (game/enenmy? color (-> position
                              (assoc :line (- line 1))
                              (assoc :column (board/previous-letter column))
                              (board/find-piece-at-position pieces)))
      (conj {:line   (- line 1)
             :column (board/previous-letter column)})
      ;bottom right
      (game/enenmy? color (-> position
                              (assoc :line (- line 1))
                              (assoc :column (board/next-letter column))
                              (board/find-piece-at-position pieces)))
      (conj {:line   (- line 1)
             :column (board/next-letter column)}))))


(s/defn possible-movements :- [s.piece/Position]
  [{:keys [color] :as piece} :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (if (= color :white)
    (possible-movements-white-pawn piece pieces)
    (possible-movements-black-pawn piece pieces)))
