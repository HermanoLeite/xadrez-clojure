(ns chess.schemata.board
  (:require [schema.core :as s]))

(def cell-skeleton {:value     {:schema s/Str :required true}
                    :movement? {:schema s/Bool :required false}})

(s/defschema Cell cell-skeleton)

(def print-skeleton {:value      {:schema s/Str :required true}
                     :color      {:schema s/Str :required true}
                     :background {:schema s/Str :required true}
                     :last-column {:schema s/Bool :required false}})

(s/defschema Print print-skeleton)
