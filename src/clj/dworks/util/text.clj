(ns dworks.util.text
  (:import [java.util UUID]))

(defn uuid
  []
  (str (UUID/randomUUID)))

(defn ->str
  [x]
  (cond
    (keyword? x) (str (namespace x) "/" (name x))
    :else (str x)))
