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
  Stolen directly from a stackoverflow answer."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn printc
  [& ms]
  (.log js/console (apply str ms)))

;; -------------------------
;; State

(def id-sequence (atom 10))

(def test-state {:todo {"1" {:id "1" :description "Do something #1" :completed? false}
                        "2" {:id "2" :description "Do something else #2" :completed? true}
                        "3" {:id "3" :description "Do something different #33" :completed? false}}})

(def state (atom test-state))

;; --------------------------
;; Logic

(defn next-id
  []
  (let [id @id-sequence]
    (swap! id-sequence inc)))

(defn initialize-todos!
  []
  ;; todo : sync ajax call to GET /api/todos
  ;; todo : update state
  )

(defn toggle-todo!
  [id]
  (when-let [t (get-in @state [:todo id])]
    (go (let [response (<! (http/post "api/todo" {:edn-params (update t :completed? not)}))
              status (:status response)
              b (:body response)
              r (transit/reader :json)
              body (transit/read r b)]
          (if (= status 200)
            (swap! state assoc-in [:todo (get body :id)] body)
            (do
              (printc "Bad toggle...")
              (printc (str "status: " status "; body: " body))))))))

(defn add-todo!
  [t]
  (go (let [response (<! (http/post "api/todo" {:edn-params t}))
            status (:status response)
            b (:body response)
            r (transit/reader :json)
            body (transit/read r b)]
        (if (= status 200)
          (swap! state assoc-in [:todo (get body :id)] body)
          (do
            (printc "Bad store...")
            (printc (str "status: " status "; body: " body)))))))

(defn delete-todo!
  [id]
  (printc (str "delete-todo!: " id))
  (when-let [t (get-in @state [:todo id])]
    (go (let [response (<! (http/delete (str "api/todo/" id)))
              status (:status response)
              b (:body response)
              r (transit/reader :json)
              body (transit/read r b)]
          (if (= status 200)
            (do
              (printc (str "swapping state.  good store"))
              (swap! state dissoc-in [:todo id])
              (printc (str "state: " @state)))
            (do
              (printc "Bad delete...")
              (printc (str "status: " status "; body: " body))))))))

(defn generate-todo
  [d]
  {:description d})

;; ------------------------
;; Views

(defn todo
  [t]
  [:li
   [:span {:on-click #(toggle-todo! (:id t))
           :class (str (when (:completed? t) "completed"))}
    (:description t)]
   [:span {:on-click #(delete-todo! (:id t))}
    [:img {:src "images/delete.png"}]]])

(defn new-todo []
  (let [val (atom "")]
    (fn []
      [:div
       [:input {:type "text"
                :placeholder "Enter todo item here."
                :value @val
                :on-change #(reset! val (-> % .-target .-value))}]
       [:button {:on-click #(when-let [t (generate-todo @val)]
                              (add-todo! t)
                              (reset! val ""))}
        "Add Todo Item"]])))

(defn todo-list-page []
  [:div
   [:h1 "Todo list"]
   [:ul
    (for [t (sort-by #(str (:completed? %) (:description %)) (vals (:todo @state)))]
      [todo t])]
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

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation! {:nav-handler (fn [path] (secretary/dispatch! path))
                                     :path-exists? (fn [path] (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (initialize-todos!)
  (mount-root))
