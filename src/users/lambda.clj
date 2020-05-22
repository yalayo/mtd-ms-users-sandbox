(ns users.lambda
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [uswitch.lambada.core :refer [deflambdafn]]
            [users.client :as c]))


(deflambdafn com.anudis.hmrc.sandbox.GenerateUser
  [in out ctx]
  (let [response (c/create-user)]
    (println "OUTPUT" response)
    (with-open [w (io/writer out)]
      (json/write response w))))

