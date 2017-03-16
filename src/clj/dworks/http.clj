(ns dworks.http
  (:require [clojure.walk :as walk]
            [dworks.status :as status]))

(defn response
  ([body] (response status/ok body))
  ([status body & [headers]]
   (let [r {:status status :body body}]
     (if headers
       (assoc r :headers headers)
       r))))

(defn ok
  [body & [headers]]
  (response status/ok body headers))

(defn bad-request
  [body & [headers]]
  (response status/bad-request body headers))

(defn not-found
  [body & [headers]]
  (response status/not-found body headers))

(defn internal-server-error
  [body & [headers]]
  (response status/internal-server-error body headers))

(defn exception
  ([e] (exception e nil nil))
  ([e msg] (exception e nil msg))
  ([e s msg & [headers]]
   (let [ns (or s status/internal-server-error)]
     (response ns
               {:status ns
                :exception (str msg (.getMessage e))}
               headers))))

(defn body->map
  [r]
  (when-let [b (:body r)]
    (cond
      (map? b) (walk/keywordize-keys b)
      (string? b) (walk/keywordize-keys (read-string b))
      :else (walk/keywordize-keys (read-string (slurp b))))))

(defn ok?
  [r]
  (= status/ok (get r :status)))

(defn body->string
  [r]
  (if-let [b (get r :body)]
    (update r :body str)
    r))
