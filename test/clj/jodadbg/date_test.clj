(ns jodadbg.date-test
  (:import [org.joda.time DateTime])
  (:require [jodadbg.date :refer :all]
            [clojure.test :refer :all]))

(def ref-ts "2015-12-25T00:00:00.000Z")
(def ref-dt (DateTime. 2015 12 25 0 0 0 utc))

(def ref-epoch    1451001600)
(def ref-epoch-ms (* ref-epoch 1000))

(def ref-dtz (timezone "America/Los_Angeles"))
(def ref-fmt iso-parser)

(deftest string->date-time-test
  (testing "Converting an ISO string for UTC into a DateTime object"
    (is (= (string->date-time ref-ts)
           ref-dt))))

(deftest date-time->string-test
  (testing "Converting a DateTime object into ISO string representation for UTC"
    (is (= [(date-time->string ref-dt) (date-time->string ref-dt ref-dtz)]
           [ref-ts "2015-12-24T16:00:00.000-08:00"]))))

(deftest timezone-test
  (testing "DateTimeZone object creation"
    (let [sn (.getShortName ref-dtz (.getMillis ref-dt))]
      (is (= sn
             "PST")))))

(deftest utc-test
  (testing "UTC DateTimeZone"
    (let [sn (.getShortName utc (.getMillis ref-dt))]
      (is (= sn
             "UTC")))))

(deftest iso-parser-test
  (testing "Logstash ISO8601 format parsing"
    (let [timestamps (conj '() ref-ts
                           "2015-12-25 00:00:00.000Z" "2015-12-25 00:00:00.000"
                           "2015-12-25 00:00:00,000Z" "2015-12-25 00:00:00,000")
          date-times (map string->date-time
                          timestamps)]
      (is (= date-times
             (take 5 (repeat ref-dt)))))))

(deftest unix-parser-test
  (testing "Logstash UNIX format parsing"
    (is (= (string->date-time (str ref-epoch)
                              unix-parser)
           ref-dt))))

(deftest unix-ms-parser-test
  (testing "Logstash UNIX_MS format parsing"
    (is (= (string->date-time (str ref-epoch-ms)
                              unix-ms-parser)
           ref-dt))))

(deftest pattern-formatter-test
  (testing "Custom pattern-based format parsing"
    (let [formatstr "EEE MMM dd HH:mm:ss Z yyyy"
          timestamp "Thu Dec 24 16:00:00 -0800 2015"
          formatter (pattern-formatter formatstr)]
      (is (= (string->date-time timestamp formatter)
             ref-dt)))))
