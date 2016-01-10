(ns jodadbg.date-test
  (:import [org.joda.time DateTime])
  (:require [jodadbg.date :refer :all]
            [clojure.test :refer :all]))

(def ^:dynamic *ref-ts*
  "2015-12-25T00:00:00.000Z")

(def ^:dynamic *ref-dt*
  (DateTime. 2015 12 25
             0 0 0 utc))

(deftest parse-timestamp-test
  (let [date-time (-> *ref-ts*
                      parse-timestamp
                      (DateTime. utc))]
    (is (= date-time
           *ref-dt*))))

(deftest millis->string-test
  (let [timestamp (-> *ref-dt*
                      .getMillis
                      millis->string)]
    (is (= timestamp
           *ref-ts*))))
