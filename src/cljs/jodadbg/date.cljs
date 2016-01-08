(ns jodadbg.date
  (:require [cljs-time.core   :as time]
            [cljs-time.coerce :as time.coerce]
            [cljs-time.format :as time.format]))

(def iso-formatter
  (:date-time time.format/formatters))

(defn string->date-time
  [s]
  (time.coerce/from-string s))

(defn utc-to-local
  [dt]
  (time/to-default-time-zone dt))

(defn date-time->string
  [dt]
  (time.format/unparse iso-formatter dt))

(defn now
  []
  (time/now))
