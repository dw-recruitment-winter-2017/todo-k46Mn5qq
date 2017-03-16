(ns dworks.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [dworks.core-test]))

(doo-tests 'dworks.core-test)
