(ns dworks.handler
  (:require [cognitect.transit :as transit]
            [compojure.core :refer [GET POST DELETE defroutes context]]
            [compojure.route :refer [not-found resources]]
            [config.core :refer [env]]
            [dworks.middleware :refer [wrap-middleware]]
            [dworks.persist.store :as store]
            [dworks.util.http :refer [body->string body->map body->transit]]
            [hiccup.page :refer [include-js include-css html5]])
  (import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(def todo-defaults {:completed? false})

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head
  []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page
  []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))

(def api-context
  (context "/api" request
     (GET "/todos" [] (body->transit (store/retrieve-all)))
     (GET "/todo/:id" [id] (body->transit (store/retrieve id)))
     (POST "/todo" [] (body->transit (store/upsert! (body->map request) nil todo-defaults)))
     (POST "/todo/:id" [id] (body->transit (store/upsert! (body->map request) id todo-defaults)))
     (DELETE "/todo/:id" [id] (body->transit (store/delete! id)))))

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  api-context
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
