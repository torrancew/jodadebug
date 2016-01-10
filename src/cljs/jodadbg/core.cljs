(ns jodadbg.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [cljs-http.client :as http]
            [jodadbg.date :as date]
            [om.core :as om]
            [om.dom  :as dom]
            [om-bootstrap.panel :as p]
            [om-bootstrap.input :as i]))

(def now
  (date/now))

(defonce app-state
  (atom
    {:timestamp nil
     :pattern "ISO8601"
     :timezone "UTC"
     :isotime now}))

(defn header
  [data owner]
  (om/component
    (dom/header #js {:role "banner" :className "navbar navbar-inverse"}
                (dom/div #js {:className "container"}
                         (dom/a #js {:href "#" :className "navbar-brand"}
                                "jodadbg")))))

(defn handle-change
  [k e owner]
  (swap! app-state
         assoc k (.. e -target -value))
  (go (let [timestamp (:timestamp @app-state)
            pattern (:pattern @app-state)
            timezone (:timezone @app-state)
            response (<! (http/get "/match" {:query-params {"timestamp" timestamp
                                                            "pattern" pattern
                                                            "timezone" timezone}}))]
        (when (= 200 (:status response))
          (swap! app-state
                 assoc :isotime (-> response
                                    :body
                                    :body
                                    :isotime
                                    date/string->date-time))))))

(defn inputs
  [data owner]
  (om/component
    (dom/form nil
              (i/input
                {:feedback? true
                 :type "text"
                 :value (:timestamp @data)
                 :label "Timestamp String"
                 :placeholder (date/date-time->string now)
                 :help "A sample timestamp to parse"
                 :on-change #(handle-change :timestamp % owner)})
              (i/input
                {:feedback? true
                 :type "text"
                 :value (:pattern @data)
                 :label "Date Filter Format String"
                 :placeholder "ISO8601"
                 :help "A single pattern string to test"
                 :on-change #(handle-change :pattern % owner)}))))

(defn matches
  [data owner]
  (om/component
    (dom/div nil
             (p/panel {:header "Parsed Timestamps"}
                      (p/panel {:header "UTC Time"
                                :bs-style "success"}
                               (date/date-time->string (:isotime @data)))
                      (p/panel {:header "(Browser) Local Time"
                                :bs-style "info"}
                               (-> (:isotime @data)
                                   date/utc-to-local
                                   date/date-time->string))))))

(defn ^:export main
  [& args]
  (om/root
    header
    app-state
    {:target (. js/document
                 (getElementById "header"))})

  (om/root
    inputs
    app-state
    {:target (. js/document
                 (getElementById "inputs"))})

  (om/root
    matches
    app-state
    {:target (. js/document
                 (getElementById "matches"))}))
