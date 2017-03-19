(ns dworks.core
  (:require [accountant.core :as accountant]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]
            [cognitect.transit :as transit]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; NOTE: Code is mostly modified from the reagent lein template generated code
;; and the migrated Reagent tutorial code.

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure.
  Stolen directly from a stackoverflow answer to save some time."
  [m [k & ks :as keys]]
  (if ks
    (if-let [next-map (get m k)]
      (let [new-map (dissoc-in next-map ks)]
        (if (seq new-map)
          (assoc m k new-map)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn printc
  [& ms]
  (.log js/console (apply str ms)))

;; -------------------------
;; State

; (def id-sequence (atom 10))

;(def test-state {:todo {"1" {:id "1" :description "Do something #1" :completed? false}
;                        "2" {:id "2" :description "Do something else #2" :completed? true}
;                        "3" {:id "3" :description "Do something different #33" :completed? false}}})

(def state (atom {}))

;; --------------------------
;; Logic

;(defn next-id
;  []
;  (let [id @id-sequence]
;    (swap! id-sequence inc)))

(defn initialize-todos!
  "Updates todo items state from server."
  []
  (go (let [response (<! (http/get "api/todos"))
            status (:status response)
            b (:body response)
            r (transit/reader :json)
            body (transit/read r b)]
        (if (= status 200)
          (swap! state assoc :todo body)
          (do
            (printc "Bad initialize todos...")
            (printc (str "status: " status "; body: " body)))))))

(defn toggle-todo!
  "Toggles the completed? of the todo item."
  [id]
  (when-let [t (get-in @state [:todo (keyword id)])]
    (go (let [response (<! (http/post "api/todo" {:edn-params (update t :completed? not)}))
              status (:status response)
              b (:body response)
              r (transit/reader :json)
              body (transit/read r b)]
          (if (= status 200)
            (swap! state assoc-in [:todo (keyword (get body :id))] body)
            (do
              (printc "Bad toggle...")
              (printc (str "status: " status "; body: " body))))))))

(defn add-todo!
  "Adds a todo item to list."
  [t]
  (go (let [response (<! (http/post "api/todo" {:edn-params t}))
            status (:status response)
            b (:body response)
            r (transit/reader :json)
            body (transit/read r b)]
        (if (= status 200)
          (swap! state assoc-in [:todo (keyword (get body :id))] body)
          (do
            (printc "Bad store...")
            (printc (str "status: " status "; body: " body)))))))

(defn delete-todo!
  "Deletes a todo item from list."
  [id]
  (when-let [t (get-in @state [:todo id])]
    (go (let [response (<! (http/delete (str "api/todo/" id)))
              status (:status response)
              b (:body response)
              r (transit/reader :json)
              body (transit/read r b)]
          (if (= status 200)
            (swap! state dissoc-in [:todo (keyword id)])
            (do
              (printc "Bad delete...")
              (printc (str "status: " status "; body: " body))))))))

(defn generate-todo
  "Generates a todo item map given a description."
  [d]
  {:description d})

;; ------------------------
;; Views

(defn todo
  "Generates HTML list item for given todo item t."
  [t]
  [:li
   [:span {:on-click #(toggle-todo! (:id t))
           :class (str (when (:completed? t) "completed"))}
    (:description t)]
   [:span {:on-click #(delete-todo! (:id t))}
    " "
    [:img {:src "images/delete.png"}]]])

(defn new-todo
  "Generates HTML text input and add button for new todo items."
  []
  (let [val (atom "")]
    (fn []
      [:div
       [:input {:name "todod"
                :type "text"
                :placeholder "Enter todo item here."
                :value @val
                :size 80
                :on-change #(reset! val (-> % .-target .-value))}]
       [:br]
       [:button {:on-click #(when-let [t (generate-todo @val)]
                              (add-todo! t)
                              (reset! val ""))}
        "Add Todo Item"]])))

(defn todo-list-page
  "Generates the todo list page."
  []
  [:div
   [:h1 "Todo list"]
   [:ul
    (for [t (sort-by #(str (:completed? %) (:description %)) (vals (:todo @state)))]
      ^{:key (get t :id)} [todo t])]
   [new-todo]])

;; template views

(defn home-page []
  [:div [:h2 "Welcome to dworks"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About dworks"]
   [:div [:a {:href "/old-home"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'todo-list-page))

#_(secretary/defroute "/old-home" []
  (session/put! :current-page #'home-page))

#_(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root
  "Mounts and renders the current page in the session."
  []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init!
  "Initializes the todo list page."
  []
  (accountant/configure-navigation! {:nav-handler (fn [path] (secretary/dispatch! path))
                                     :path-exists? (fn [path] (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (initialize-todos!)
  (mount-root))
