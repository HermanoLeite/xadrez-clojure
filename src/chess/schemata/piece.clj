(ns chess.schemata.piece
  (:require [schema.core :as s]))

(def piece-type #{:pawn :bishop :knight :queen :king :rook})

(def Type (apply s/enum piece-type))

(def color #{:white :black})

(def Color (apply s/enum color))

(def position-skeleton {:line   {:schema s/Int :required true}
                        :column {:schema s/Str :required true}})

(s/defschema Position position-skeleton)

(def piece-skeleton
  {:position  {:schema Position :required true}
   :color     {:schema Color :required true}
   :piece     {:schema Type :required true}
   :movements {:schema s/Int :required true}})

(s/defschema Piece piece-skeleton)

