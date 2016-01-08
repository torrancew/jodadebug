(ns jodadbg.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [jodadbg.date :as date]
            [om.core :as om]
            [om.dom  :as dom]
            [om-bootstrap.panel :as p]
            [om-bootstrap.input :as i]
            [clojure.browser.repl :as repl]))

;; TODO: Gate this around development mode
(def ^:dynamic *repl*
  "http://localhost:9000/repl")
(repl/connect *repl*)

(def now
  (date/now))

(defonce app-state
  (atom
    {:timestamp nil
     :format nil
     :timezone "UTC"
     :isotime now}))

(defn header
  [data owner]
  (om/component
    (dom/header #js {:role "banner" :className "navbar navbar-inverse"}
                (dom/div #js {:className "container"}
                         (dom/a #js {:href "#" :className "navbar-brand"}
                                "jodadbg")))))

(defn inputs
  [data owner]
  (let [timestamp (:timestamp @data)
        formatstr (:format    @data)]
    (om/component
      (dom/form nil
                (i/input
                  {:feedback? true
                   :type "text"
                   :value (:timestamp @data)
                   :label "Timestamp String"
                   :placeholder "2015-12-25T00:00:00.000Z"
                   :help "A sample timestamp to parse"
                   :on-change #()})
                (i/input
                  {:feedback? true
                   :type "text"
                   :value (:format @data)
                   :label "Date Filter Format String"
                   :placeholder "ISO8601"
                   :help "A single format string to test"
                   :on-change #()})))))

(defn matches
  [data owner]
  (let [isotime (:isotime @data)]
    (om/component
      (dom/div nil
               (p/panel {:header "Parsed Timestamps"}
                 (p/panel {:header "UTC Time"
                           :bs-style "success"}
                          (date/date-time->string isotime))
                 (p/panel {:header "(Browser) Local Time"
                           :bs-style "info"}
                          (-> isotime
                              date/utc-to-local
                              date/date-time->string)))))))

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
