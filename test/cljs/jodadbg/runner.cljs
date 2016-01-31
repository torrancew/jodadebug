(ns jodadbg.runner
  (:require [doo.runner :refer-macros [doo-all-tests]]
            [jodadbg.core-test]
            [jodadbg.date-test]))

(doo-all-tests #"^jodadbg\..*?-test")
