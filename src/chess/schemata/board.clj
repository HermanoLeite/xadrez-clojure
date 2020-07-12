(ns chess.schemata.board
  (:require [schema.core :as s]))

(def cell-skeleton {:value     {:schema s/Str :required true}
                    :movement? {:schema s/Bool :required false}})

(s/defschema Cell cell-skeleton)
