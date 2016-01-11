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

(def utc (timezone "UTC"))

(defmacro ^:private with-timezone
  "Returns an instance of the given DateTimeFormatter in the specified DateTimeZone"
  [^DateTimeFormatter fmt ^DateTimeZone dtz]
  `(.withZone ~fmt ~dtz))

(def ^:private iso-printer
  (ISODateTimeFormat/dateTime))

(def ^:private strict-iso-parser
  (ISODateTimeFormat/dateTimeParser))

(defmacro pattern-formatter
  "Returns a DateTimeFormatter for the specified pattern"
  [^String pattern]
  `(DateTimeFormat/forPattern ~pattern))

(def ^:private almost-iso-parsers
  (map #(pattern-formatter %)
       (conj '()
             "yyyy-MM-dd HH:mm:ss.SSSZ" "yyyy-MM-dd HH:mm:ss.SSS"
             "yyyy-MM-dd HH:mm:ss,SSSZ" "yyyy-MM-dd HH:mm:ss,SSS")))

(defmacro ^:private multi-formatter
  "Returns a DateTimeFormatter composed of the provided patterns"
  [formatters]
  `(let [fmt# (with-timezone iso-printer utc)
         bld# (new DateTimeFormatterBuilder)]
     (->> ~formatters
          (map #(.getParser %))
          (into-array DateTimeParser)
          (.append bld# (.getPrinter fmt#)))
     (.toFormatter bld#)))

(def iso-parser
  (multi-formatter (cons strict-iso-parser almost-iso-parsers)))

(defmacro ^:private proxy-formatter
  "Yields a proxy of a DateTimeFormatter using the specified parse-fn as `parseDateTime`"
  [parse-fn]
  `(proxy [DateTimeFormatter]
     [^DateTimePrinter (.getPrinter (with-timezone iso-printer utc))
      ^DateTimeParser  (.getParser   strict-iso-parser)]
     (withZone
       [^DateTimeZone dtz#]
       ~'this)
     (parseDateTime
       [^String timestamp#]
       (~parse-fn timestamp#))))

(def unix-parser
  (proxy-formatter #(-> (Long/parseLong %)
                        (* 1000)
                        (DateTime.)
                        (.toDateTime utc))))

(def unix-ms-parser
  (proxy-formatter #(-> (Long/parseLong %)
                        (DateTime.)
                        (.toDateTime utc))))

(defn string->date-time
  "Converts the given String into a DateTimeObject in UTC, using the given DateTimeFormatter (default: ISODateTimeFormat) and the given DateTimeZone (default: UTC)"
  ([^String timestamp] (string->date-time timestamp iso-parser))
  ([^String timestamp ^DateTimeFormatter formatter]
   (string->date-time timestamp formatter utc))
  ([^String timestamp ^DateTimeFormatter formatter ^DateTimeZone dtz]
   (-> formatter
       (with-timezone dtz)
       (.parseDateTime timestamp)
       (.toDateTime utc))))

(defn date-time->string
  "Converts the given DateTime object into an ISO string for the given DateTimeZone (default: UTC)"
  ([^DateTime dt]
   (date-time->string dt utc))
  ([^DateTime dt ^DateTimeZone dtz]
   (-> iso-printer
       (with-timezone dtz)
       (.print dt))))
