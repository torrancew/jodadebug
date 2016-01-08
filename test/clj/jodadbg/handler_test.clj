(ns jodadbg.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [jodadbg.handler :refer :all]))

(deftest test-app
  (is (= 1 1)))
