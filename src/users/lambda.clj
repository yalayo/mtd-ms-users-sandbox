(ns users.lambda
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [uswitch.lambada.core :refer [deflambdafn]]
            [users.client :as c]))

(deflambdafn com.anudis.hmrc.sandbox.GenerateUser
  [in out ctx]
  (let [in (json/read (io/reader in) :key-fn keyword)
        response (c/create-user (get in :data))]
    (with-open [w (io/writer out)]
      (json/write response w))))
