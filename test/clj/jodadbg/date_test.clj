(ns jodadbg.date-test
  (:import [org.joda.time DateTime])
  (:require [jodadbg.date :refer :all]
            [clojure.test :refer :all]))

(def ^:dynamic *ref-ts*
  "2015-12-25T00:00:00.000Z")

(def ^:dynamic *ref-dt* (DateTime. 2015 12 25 0 0 0 utc))

(deftest string->date-time-test
  (testing "Converting an ISO string for UTC into a DateTime object"
    (is (= (string->date-time *ref-ts*)
           *ref-dt*))))

(deftest date-time->string-test
  (testing "Converting a DateTime object into ISO string representation for UTC"
    (is (= (date-time->string *ref-dt*)
           *ref-ts*))))
