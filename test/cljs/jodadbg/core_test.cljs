(ns jodadbg.core-test
  (:require [jodadbg.core :as jodadbg]
            [cljs.test :refer-macros [deftest is testing]]))

(deftest test-header
  (is (= 1 0)))
