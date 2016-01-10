(ns jodadbg.date
  (:require [cljs-time.core   :as time]
            [cljs-time.coerce :as time.coerce]
            [cljs-time.format :as time.format]))

(def ^:private iso-formatter
  (:date-time time.format/formatters))

(defn now
  "Returns a DateTime object representing the current time in UTC"
  []
  (time/now))

(defn string->date-time
  "Attempts to parse the given string into a DateTime object"
  [s]
  (time.coerce/from-string s))

(defn utc-to-local
  "Converts the given DateTime object to the default timezone"
  [dt]
  (time/to-default-time-zone dt))

(defn date-time->string
  "Returns an ISO8601 string representation of the given DateTime object"
  [dt]
  (time.format/unparse iso-formatter dt))
