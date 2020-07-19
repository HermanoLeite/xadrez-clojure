(ns chess.pieces.pawn
  (:require [chess.board :as board]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s]
            [chess.game :as game]))

(s/defn ^:private possible-movements-white-pawn
  [{:keys [position movements color]} :- s.piece/Piece
   pieces :- [s.piece/Piece]
   capture-movement? :- s/Bool]
  (let [line   (:line position)
        column (:column position)]
    (cond-> []
      ;top
      (and (nil? (-> position
                     (assoc :line (+ line 1))
                     (board/find-piece-at-position pieces)))
           (not capture-movement?))
      (conj {:line   (+ line 1)
             :column column})
      ;double top
      (and (nil? (-> position
                     (assoc :line (+ line 2))
                     (board/find-piece-at-position pieces)))
           (= movements 0)
           (not capture-movement?))
      (conj {:line   (+ line 2)
             :column column})
      ;top left
      (or capture-movement?
          (game/enenmy? color (-> position
                                  (assoc :line (+ line 1))
                                  (assoc :column (board/previous-column column))
                                  (board/find-piece-at-position pieces))))
      (conj {:line   (+ line 1)
             :column (board/previous-column column)})
      ;top right
      (or capture-movement?
          (game/enenmy? color (-> position
                                  (assoc :line (+ line 1))
                                  (assoc :column (board/next-column column))
                                  (board/find-piece-at-position pieces))))
      (conj {:line   (+ line 1)
             :column (board/next-column column)}))))

(s/defn ^:private possible-movements-black-pawn
  [{:keys [position movements color]} :- s.piece/Piece
   pieces :- [s.piece/Piece]
   capture-movement? :- s/Bool]
  (let [line   (:line position)
        column (:column position)]
    (cond-> []
      ;bottom
      (and (nil? (-> position
                     (assoc :line (- line 1))
                     (board/find-piece-at-position pieces)))
           (not capture-movement?))
      (conj {:line   (- line 1)
             :column column})
      ;double bottom
      (and (nil? (-> position
                     (assoc :line (- line 2))
                     (board/find-piece-at-position pieces)))
           (= movements 0)
           (not capture-movement?))
      (conj {:line   (- line 2)
             :column column})
      ;bottom left
      (or capture-movement?
          (game/enenmy? color (-> position
                                  (assoc :line (- line 1))
                                  (assoc :column (board/previous-column column))
                                  (board/find-piece-at-position pieces))))
      (conj {:line   (- line 1)
             :column (board/previous-column column)})
      ;bottom right
      (or capture-movement?
          (game/enenmy? color (-> position
                                  (assoc :line (- line 1))
                                  (assoc :column (board/next-column column))
                                  (board/find-piece-at-position pieces))))
      (conj {:line   (- line 1)
             :column (board/next-column column)}))))

(s/defn possible-movements :- [s.piece/Position]
  [{:keys [color] :as piece} :- s.piece/Piece
   pieces :- [s.piece/Piece]
   capture-movement? :- s/Bool]
  (if (= color :white)
    (possible-movements-white-pawn piece pieces capture-movement?)
    (possible-movements-black-pawn piece pieces capture-movement?)))
