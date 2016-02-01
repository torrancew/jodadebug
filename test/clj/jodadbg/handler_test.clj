(ns jodadbg.handler-test
  (:require [jodadbg.handler :refer :all]
            [clojure.test    :refer :all]))

(deftest match-handler-test
  (testing "Successful Date Match"
    (is (= (match-handler "2012-12-25T12:00:00.000Z" "ISO8601" "UTC")
           {:status 200
            :headers {}
            :body {:isotime "2012-12-25T12:00:00.000Z"}})))
  (testing "Failed Date Match"
    (is (= (match-handler "2012-12-25_12:00:00.000Z" "ISO8601" "UTC")
           {:status 400
            :headers {}
            :body {:error "Invalid format: \"2012-12-25_12:00:00.000Z\" is malformed at \"_12:00:00.000Z\""}}))))
