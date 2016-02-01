(ns jodadbg.handler
  (:require [jodadbg.date    :as date]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core             :refer [env]]
            [compojure.core           :refer [GET defroutes]]
            [ring.util.response       :refer [content-type status response resource-response]]
            [ring.middleware.json     :refer [wrap-json-response]]
            [ring.middleware.logger   :refer [wrap-with-logger]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]])
  (:gen-class))

(defn match-handler
  [timestamp pattern timezone]
  (try (let [dtz (case timezone
                   nil date/utc
                   (date/timezone timezone))
             fmt (case pattern
                   "ISO8601" date/iso-parser
                   "UNIX"    date/unix-parser
                   "UNIX_MS" date/unix-ms-parser
                   (date/pattern-formatter pattern))
             dt (date/string->date-time timestamp fmt dtz)]
         (response {:isotime (date/date-time->string dt)}))
       (catch java.lang.IllegalArgumentException e
         (status (response {:error (.getMessage e)})
                 400))))

(defroutes app-routes
  (GET "/" []
       (-> (resource-response "index.html" {:root "public"})
           (content-type "text/html")))
  (GET "/match" [timestamp pattern timezone & params]
       (match-handler timestamp pattern timezone))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-defaults api-defaults)
      (wrap-with-logger)))

(defn -main
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))
