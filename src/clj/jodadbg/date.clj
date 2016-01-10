(ns jodadbg.date
  (:import (org.joda.time DateTime
                          DateTimeZone)
           (org.joda.time.format DateTimeParser
                                 DateTimePrinter
                                 DateTimeFormat
                                 DateTimeFormatter
                                 ISODateTimeFormat
                                 DateTimeFormatterBuilder)))

(defmacro timezone
  "Returns the corresponding DateTimeZone for the given ID"
  [^String id]
  `(DateTimeZone/forID ~id))

(def utc
  (timezone "UTC"))

(defmacro with-timezone
  "Returns an instance of the given DateTimeFormatter in the specified DateTimeZone"
  [^DateTimeFormatter fmt ^DateTimeZone dtz]
  `(.withZone ~fmt ~dtz))

(def iso-printer
  (ISODateTimeFormat/dateTime))

(defmacro pattern-formatter
  "Returns a DateTimeFormatter for the specified pattern"
  [^String pattern]
  `(DateTimeFormat/forPattern ~pattern))

(defmacro multi-formatter
  "Returns a DateTimeFormatter composed of the provided patterns"
  [formatters]
  `(let [builder# (new DateTimeFormatterBuilder)]
    (->> ~formatters
         (map #(.getParser %))
         (into-array DateTimeParser)
         (.append builder# (.getPrinter (with-timezone iso-printer utc))))
    (.toFormatter builder#)))

(def ^:private strict-iso-parser
  (ISODateTimeFormat/dateTimeParser))

(def ^:private almost-iso-parsers
  (map #(pattern-formatter %)
       '("yyyy-MM-dd HH:mm:ss.SSSZ" "yyyy-MM-dd HH:mm:ss.SSS"
         "yyyy-MM-dd HH:mm:ss,SSSZ" "yyyy-MM-dd HH:mm:ss,SSS")))

(defmacro proxy-formatter
  "Yields a proxy of a DateTimeFormatterusing the specified parse-fn as `parseMillis`"
  [parse-fn]
  `(proxy [DateTimeFormatter]
          [^DateTimePrinter (.getPrinter (with-timezone iso-printer utc))
           ^DateTimeParser  (.getParser   strict-iso-parser)]
          (parseMillis
            [^String timestamp#]
            (~parse-fn timestamp#))))

(def iso-parser
  (multi-formatter (cons strict-iso-parser almost-iso-parsers)))

(def unix-parser
  (proxy-formatter #(* 1000 (Integer/parseInt %))))

(def unix-ms-parser
  (proxy-formatter #(Integer/parseInt %)))

(defn string->date-time
  "Converts the given String into a DateTimeObject in UTC, using the given DateTimeFormatter (default: ISODateTimeFormat)"
  ([^String timestamp] (string->date-time timestamp strict-iso-parser))
  ([^String timestamp ^DateTimeFormatter formatter]
     (-> (with-timezone formatter utc)
         (.parseDateTime timestamp))))

(defn date-time->string
  "Converts the given DateTime object into an ISO string for the given DateTimeZone (default: UTC)"
  ([^DateTime dt]
     (date-time->string dt utc))
  ([^DateTime dt ^DateTimeZone dtz]
     (.print (with-timezone iso-printer dtz) dt)))
