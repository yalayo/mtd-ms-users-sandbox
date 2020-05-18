(ns users.client-test
  (:require [users.client :as c]
            [clojure.spec.test.alpha :as st]
            [org.httpkit.client :as client]
            [clojure.spec.alpha :as s]))

(st/instrument `c/consume-api {:stub #{`c/consume-api}})

(st/summarize-results (st/check `c/create-user))


