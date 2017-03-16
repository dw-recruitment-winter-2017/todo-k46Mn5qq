(ns dworks.handler
  (:require [compojure.core :refer [GET POST DELETE defroutes context]]
            [compojure.route :refer [not-found resources]]
            [config.core :refer [env]]
            [dworks.middleware :refer [wrap-middleware]]
            [hiccup.page :refer [include-js include-css html5]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))

(def api-context
  (context "/api" request
     (GET "/todos" [] {:status 200 :body "todos/"})
     (GET "/todo/:todo" [todo] {:status 200 :body (str "get todo:" todo)})
     (DELETE "/todo/:todo" [todo] {:status 200 :body (str "delete! todo:" todo)})
     (POST "/todo/:todo" [todo] {:status 200 :body (str "upsert! todo:" todo)})))

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  api-context
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
