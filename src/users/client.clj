(ns users.client
  (:require [clojure.spec.alpha :as s]
            [clojure.data.json :as json]
            [org.httpkit.client :as client]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::name string?)
(s/def ::serviceNames (s/coll-of string? :kind vector?))
(s/def ::request (s/keys :req-un [::serviceNames]))
(s/def ::result (s/coll-of string? :gen-max 3))
(s/def ::error int?)
(s/def ::response (s/or :ok (s/keys :req [::result])
                        :err (s/keys :req [::error])))

(defn request-token []
  (let [options {
                 :method :post
                 :user-agent "User-Agent Anudis"
                 :headers {"Content-Type" "application/x-www-form-urlencoded"}
                 :form-params {"client_id" "1mSebFWgzHoxglgYrrRYIjB4mEeW" "client_secret" "81911b7e-3c2e-4546-b4da-40f324fcebce" "grant_type" "client_credentials"}
                 :keepalive 3000
                 :timeout 5000
                 :filter (client/max-body-filter (* 1024 100)) ; reject if body is more than 100k
                 :insecure? false
                 :follow-redirects false
                 }
        {:keys [body error]} @(client/post "https://test-api.service.hmrc.gov.uk/oauth/token" options)]
    (if error
      (println "Failed, exception is " error)
      (str body))))

(def b {:serviceNames ['national-insurance 'self-assessment 'mtd-income-tax 'customs-services 'mtd-vat]})

(defn consume-api [body]
  (let [token ((json/read-str (request-token)) "access_token")
        options {
                 :method :post
                 :user-agent "User-Agent Anudis"
                 :headers {"Accept" "application/vnd.hmrc.1.0+json"
                           "Content-Type" "application/json"
                           "Authorization" (str "Bearer " token)}
                 :body (json/write-str body) ; use this for content-type json
                 :keepalive 3000
                 :timeout 5000
                 :filter (client/max-body-filter (* 1024 100)) ; reject if body is more than 100k
                 :insecure? false
                 :follow-redirects false
                 }
        {:keys [status headers body error]} @(client/post "https://test-api.service.hmrc.gov.uk/create-test-user/individuals" options)]
    (if error
      (println "Failed, exception is " error)
      (str body))))


(defn create-user []
  (json/read-str (consume-api b)))


;(json/read-str (create-user))

;(create-user)
