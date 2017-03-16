(ns dworks.store
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [dworks.http :as http])
  (:import [java.util UUID]))

(defn uuid
  []
  (str (UUID/randomUUID)))

(def default-store-folder "store/todo/")

(def store-folder (atom default-store-folder))

(defn store-folder!
  [sf]
  (when (and sf (string? sf))
    (reset! store-folder sf)))

(defn filename
  [id]
  (str @store-folder id))

(defn retrieve
  [id]
  (try
    (if id
      (let [fn (filename id)
            f (io/file fn)]
        (if (.exists f)
          (http/ok (read-string (slurp f)))
          (http/not-found fn)))
      (http/bad-request "No id provided."))
    (catch Exception e
      (http/exception e "retrieve: "))))

(defn upsert!
  [d & [i defaults]]
  (try
    (if (and d (map? d))
      (let [id (or (get d :id) i (uuid))
            fn (filename id)
            nd (merge {:id id} defaults d)]
        (io/make-parents (io/file fn))
        (pprint nd (io/writer fn))
        (http/ok nd))
      (http/bad-request "upsert! failed"))
    (catch Exception e
      (http/exception e "upsert!: "))))

(defn delete!
  [id]
  (try
    (if id
      (let [fn (filename id)
            f (io/file fn)]
        (if (.exists f)
          (do
            (io/delete-file f)
            (http/ok (slurp f)))
          (http/not-found fn)))
      (http/bad-request "No id provided."))
    (catch Exception e
      (http/exception e "delete!: "))))

(defn retrieve-all
  []
  (if-let [sf (io/file @store-folder)]
    (http/ok (reduce (fn [a f]
                       (let [id (.getName f)
                             rr (retrieve id)
                             b (when (http/ok? rr) (get rr :body))]
                         (if b
                           (assoc a (keyword id) b)
                           a)))
                     nil
                     (.listFiles sf)))
    (http/not-found "No todos stored.")))
