(ns jodadbg.runner
  (:require [doo.runner :refer-macros [doo-all-tests]]
            [jodadbg.core-test]))

(doo-all-tests #"^jodadbg\..*?-test")
