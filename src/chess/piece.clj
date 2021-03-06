(ns chess.piece
  (:require [chess.board :as board]
            [chess.pieces.bishop :as bishop]
            [chess.pieces.king :as king]
            [chess.pieces.knight :as knight]
            [chess.pieces.pawn :as pawn]
            [chess.pieces.queen :as queen]
            [chess.pieces.rook :as rook]
            [chess.schemata.piece :as s.piece]
            [schema.core :as s]
            [chess.game :as game]))

(s/defn possible-movements :- (s/maybe [s.piece/Position])
  ([piece-to-move :- s.piece/Piece
    pieces :- [s.piece/Piece]]
   (possible-movements piece-to-move pieces false))
  ([{:keys [piece color] :as piece-to-move} :- s.piece/Piece
    pieces :- [s.piece/Piece]
    only-capture-movements? :- s/Bool]
   (case piece
     :pawn
     (pawn/possible-movements piece-to-move pieces only-capture-movements?)

     :knight
     (knight/possible-movements piece-to-move pieces)

     :bishop
     (bishop/possible-movements piece-to-move pieces)

     :rook
     (rook/possible-movements piece-to-move pieces)

     :queen
     (queen/possible-movements piece-to-move pieces)

     :king
     (if only-capture-movements?
       (king/possible-movements piece-to-move pieces [])
       (->> pieces
            (filter #(game/enenmy? color %))
            (map #(possible-movements % pieces true))
            (reduce concat)
            (king/possible-movements piece-to-move pieces)))

     [])))

(s/defn possible-movements! :- [s.piece/Position]
  [piece-to-move :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (if-let [possible-movements (seq (possible-movements piece-to-move pieces))]
    possible-movements
    (throw (Exception. "No movements for that piece, sorry."))))

(s/defn xeque? :- s/Bool
  [pieces :- (s/maybe [s.piece/Piece])
   color :- s.piece/Color]
  (let [king            (->> pieces
                             (filter #(and (= :king (:piece %))
                                           (= color (:color %))))
                             first)
        enemy-movements (->> pieces
                             (filter #(game/enenmy? color %))
                             (map #(possible-movements % pieces true))
                             (reduce concat))]
    (boolean (some #(= (:position king) %) enemy-movements))))

(s/defn move! :- [s.piece/Piece]
  [pieces :- [s.piece/Piece]
   piece-to-move :- s.piece/Piece
   to-position :- s.piece/Position]
  (let [captured-piece             (board/find-piece-at-position to-position pieces)
        pieces-with-removed-pieces (remove #(or (= % piece-to-move)
                                                (= % captured-piece)) pieces)
        uptaded-movements          (-> piece-to-move :movements (+ 1))
        piece-at-new-position      (-> piece-to-move
                                       (assoc :position to-position)
                                       (assoc :movements uptaded-movements))
        updated-pieces             (conj pieces-with-removed-pieces piece-at-new-position)]
    (if (xeque? updated-pieces (:color piece-to-move))
      (throw (Exception. "You can't put yourself in a xeque position!"))
      updated-pieces)))

(s/defn try-move :- (s/maybe [s.piece/Piece])
  [pieces :- [s.piece/Piece]
   piece-to-move :- s.piece/Piece
   to-position :- s.piece/Position]
  (try (move! pieces piece-to-move to-position)
       (catch Exception e
         nil)))

(s/defn move-save-xaque-mate? :- s/Bool
  [pieces :- [s.piece/Piece]
   {:keys [color] :as piece} :- s.piece/Piece
   to-position :- s.piece/Position]
  (boolean (some-> pieces
                   (try-move piece to-position)
                   (xeque? color)
                   not)))

(s/defn piece-save-xeque-mate? :- s/Bool
  [piece :- s.piece/Piece
   pieces :- [s.piece/Piece]]
  (->> pieces
       (possible-movements piece)
       (map #(move-save-xaque-mate? pieces piece %))
       (some true?)
       (boolean)))

(s/defn only-pieces-of-color :- [s.piece/Piece]
  [color :- s.piece/Color
   pieces :- [s.piece/Piece]]
  (filter #(= color (:color %)) pieces))

(s/defn any-piece-save-xeque-mate? :- s/Bool
  [all-pieces :- [s.piece/Piece]
   same-color-pieces :- [s.piece/Piece]]
  (->> same-color-pieces
       (some #(piece-save-xeque-mate? % all-pieces))
       boolean))

(s/defn xeque-mate? :- s/Bool
  [color :- s.piece/Color
   pieces :- [s.piece/Piece]]
  (and (xeque? pieces color)
       (let [result (->> pieces
                         (only-pieces-of-color color)
                         (any-piece-save-xeque-mate? pieces))]
         (not result))))
