(ns users.client
  (:require [clojure.spec.alpha :as s]
            [clojure.data.json :as json]
            [org.httpkit.client :as client]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::name string?)
(s/def ::service-names (s/coll-of keyword? :kind vector?))
(s/def ::request (s/keys :req-un [::serviceNames]))
(s/def ::result (s/coll-of string? :gen-max 3))
(s/def ::error int?)
(s/def ::response (s/or :ok (s/keys :req [::result])
                        :err (s/keys :req [::error])))


(s/conform ::service-names [:national-insurance :self-assessment :mtd-income-tax :customs-services :mtd-vat])

(gen/sample (s/gen ::request))

(s/fdef consume-api
  :args (s/cat :body ::request)
  :ret ::response)

(defn consume-api [body]
  (let [options {
                 :method :post
                 :user-agent "User-Agent Anudis"
                 :headers {"Accept" "application/vnd.hmrc.1.0+json"
                           "Content-Type" "application/json"
                           "Authorization" "Bearer fbe2a920bbf0c00d51fa778b5568ed7e"}
                 :body (json/write-str body) ; use this for content-type json
                 :keepalive 3000
                 :timeout 5000
                 :filter (client/max-body-filter (* 1024 100)) ; reject if body is more than 100k
                 :insecure? false
                 :follow-redirects false
                 }
        {:keys [body error]} @(client/post "https://test-api.service.hmrc.gov.uk/create-test-user/individuals" options)]
    (if error
      (str "Failed, exception is " error)
      (str body))))

(s/fdef create-user
  :args (s/cat :body ::request)
  :ret (s/or :ok ::result :err ::error))

(defn create-user [body]
  (let [{::keys [result error]} (consume-api body)]
    (or result error)))

(def body {:serviceNames [:national-insurance :self-assessment :mtd-income-tax :customs-services :mtd-vat]})

(def a {:serviceNames [:national-insurance :self-assessment :mtd-income-tax :customs-services :mtd-vat]})

(s/valid? ::request body)

(create-user body)

(consume-api body)

(json/write-str body)
