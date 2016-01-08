(ns jodadbg.runner
  (:require [cljs.test :refer-macros [run-all-tests]]))

(def ^:private success 0)

(enable-console-print!)

(defn ^:export run
  []
  (run-all-tests #"^jodadbg\..*-test$")
  success)
