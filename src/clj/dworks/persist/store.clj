(ns dworks.persist.store
  (:require [dworks.util.http :as http]
            [dworks.persist.file :as fs]))

(def default-handler {:delete! fs/delete!
                      :location fs/store-folder
                      :location! fs/store-folder!
                      :retrieve fs/retrieve
                      :retrieve-all fs/retrieve-all
                      :upsert! fs/upsert!})

(def handler (atom default-handler))

(defn handler-for
  [x]
  (get @handler x))

(defn location
  []
  (when-let [rl (handler-for :location)]
    (deref rl)))

(defn handle-action
  [action & ps]
  (try
    (if-let [h (handler-for action)]
      (apply h ps)
      (http/internal-server-error (str "No handler found for " action ".")))
    (catch Exception e
      (http/exception e (str action ": ")))))

(defn location!
  [l]
  (handle-action :location! l))

(defn retrieve
  [id]
  (handle-action :retrieve id))

(defn upsert!
  [d & [i defaults]]
  (handle-action :upsert! d i defaults))

(defn delete!
  [id]
  (handle-action :delete! id))

(defn retrieve-all
  []
  (handle-action :retrieve-all))
