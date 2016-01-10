(ns jodadbg.date-test
  (:require [cljs-time.extend]
            [jodadbg.date :as date]
            [cljs.core.async :refer [<! >! chan close! timeout]])
  (:require-macros [cljs.test :refer [async deftest is testing]]
                   [cljs.core.async.macros :refer [alt! go]]))

(def ^:dynamic *ref-ts* "2015-12-25T00:00:00.000Z")

(def ^:dynamic *ref-dt* (cljs-time.core/date-time 2015 12 25 0 0 0 0))

(deftest now-test-async
  (testing "Getting the current time"
    (let [start (date/now)]
      (async done (go
                    (<! (timeout 5))
                    (is (not= (date/now) start))
                    (done))))))

(deftest string->date-time-test
  (testing "Converting an ISO-formatted string into a DateTime object"
    (is (= (date/string->date-time *ref-ts*)
           *ref-dt*))))

(deftest date-time->string-test
  (testing "Converting a DateTime object into an ISO-formatted string"
    (is (= (date/date-time->string *ref-dt*)
           *ref-ts*))))
