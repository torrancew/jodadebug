(ns jodadbg.core-test
  (:require [om.core      :as om]
            [jodadbg.core :as jodadbg]
            [jodadbg.date :as date]
            [dommy.core :as dommy :include-macros true]
            [cljs.core.async :refer [<! >! chan close! timeout]])
  (:require-macros [cljs.test :refer [async deftest is testing]]
                   [cljs.core.async.macros :refer [alt! go]]))

(defn ^:private div
  []
  (dommy/create-element :div))

(deftest header-test
  (testing "Header"
    (let [data {}
          div (dommy/append! (dommy/sel1 :body)
                             (div))]
      (om/root jodadbg/header data {:target div})
      (let [lnk (dommy/sel1 div :a.navbar-brand)
            ref (dommy/attr lnk :href)
            txt (dommy/value lnk)]
        (is (= (seq "#" "jodadbg")
               (seq ref txt)))))))

(deftest handle-match-test
  (testing "Successful Match"
    (let [resp {:status 200
                :body {:isotime "2012-12-25T00:00:00.000Z"}}]
      (jodadbg/handle-match resp)
      (is (= (date/string->date-time "2012-12-25T00:00:00.000Z")
             (:isotime @jodadbg/app-state)))))
  (testing "Failed Match"
    (let [resp {:status 400
                :body {:error "Invalid format: \"2016-01-31_01:50:10.296Z\" is malformed at \"_01:50:10.296Z\""}}]
      (jodadbg/handle-match resp)
      (is (= "Invalid format: \"2016-01-31_01:50:10.296Z\" is malformed at \"_01:50:10.296Z\""
             (:error @jodadbg/app-state))))))

(deftest inputs-test
  (testing "Inputs"
    (let [data {}
          div (dommy/append! (dommy/sel1 :body)
                             (div))]
      (om/root jodadbg/inputs data {:target div})
      (is (= 2
             (count (dommy/sel div :input)))))))

(deftest matches-test
  (testing "Successful Matches"
    (let [data {:isotime (date/now)}
          div (dommy/append! (dommy/sel1 :body)
                             (div))]
      (om/root jodadbg/matches data {:target div})
      (is (= 2
             (count (dommy/sel div "div.panel div.panel"))))))
  (testing "Failed Matches"
    (let [data {:isotime nil}
          div (dommy/append! (dommy/sel1 :body)
                             (div))]
      (om/root jodadbg/matches data {:target div})
      (is (= 0
             (count (dommy/sel div "div.panel div.panel")))))))

(deftest errors-test
  (testing "Failed Matches"
    (let [data {:error "Invalid format: \"2016-01-31_01:50:10.296Z\" is malformed at \"_01:50:10.296Z\""}
          div (dommy/append! (dommy/sel1 :body)
                             (div))]
      (om/root jodadbg/errors data {:target div})
      (is (= 1
             (count (dommy/sel div :div.panel-danger))))))
  (testing "Successful Matches"
    (let [data {:error nil}
          div (dommy/append! (dommy/sel1 :body)
                             (div))]
      (om/root jodadbg/errors data {:target div})
      (is (= 0
             (count (dommy/sel div :div.panel-danger)))))))
