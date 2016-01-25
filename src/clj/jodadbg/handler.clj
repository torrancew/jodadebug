(ns jodadbg.handler
  (:require [jodadbg.date    :as date]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core             :refer [env]]
            [compojure.core           :refer [GET defroutes]]
            [ring.util.response       :refer [content-type response resource-response]]
            [ring.middleware.json     :refer [wrap-json-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]))

(defn match-handler
  [timestamp pattern timezone]
  (let [dtz (case timezone
              nil date/utc
              (date/timezone timezone))
        fmt (case pattern
              "ISO8601" date/iso-parser
              "UNIX"    date/unix-parser
              "UNIX_MS" date/unix-ms-parser
              (date/pattern-formatter pattern))
        dt (date/string->date-time timestamp fmt dtz)]
    (response {:isotime (date/date-time->string dt)})))

(defroutes app-routes
  (GET "/" []
       (-> (resource-response "index.html" {:root "public"})
           (content-type "text/html")))
  (GET "/match" [timestamp pattern timezone & params]
       (response (match-handler timestamp pattern timezone)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-defaults api-defaults)))

(defn -main
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))
