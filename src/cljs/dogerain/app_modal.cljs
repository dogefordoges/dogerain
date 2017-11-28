(ns dogerain.app-modal
  (:require [reagent.core :as r]
            [reagent-modals.modals :as modal]))

(defn send-event-and-close [event action]
  (action)
  (modal/close-modal!))

;;State needs :public-key
(defn deposit-modal [state]
  [:div
   [:h1 {:style {:text-align "center"}} "Deposit"]
   [:hr]
   [:div.container-fluid
    [:h3 "Deposit Address:"]
    [:p {:style {:width "100%" :border "1px solid black"}} "123456789"]
    [:div {:style {:text-align "center"}} [:h1 {:on-click modal/close-modal!} "Close"]]]])

(defn withdraw-modal [state send]
  [:div
   [:h1 {:style {:text-align "center"}} "Withdraw"]
   [:hr]
   [:div.container-fluid
    [:h3 "Amount:"]
    [:input.input-form {:id "withdraw-amount":type "text" :name "amount"}]
    [:h3 "Receiver Address:"]
    [:input.input-form {:id "receiver-address":type "text" :name "receiver-address"}]
    [:row {:style {:text-align "center"}}
     [:div.col-lg-6 [:h1 {:on-click (fn [] 
                                      (send-event-and-close "withdraw" 
                                        (fn [] (send [:action/withdraw {:amount (.-value (.getElementById js/document "withdraw-amount"))
                                                                        :receiver-address (.-value (.getElementById js/document "receiver-address"))}]))))} 
                     "Withdraw"]]
     [:div.col-lg-6 [:h1 {:on-click modal/close-modal!} "Cancel"]]]]])

(defn rain-modal [state send]
  [:div
   [:h1 {:style {:text-align "center"}} "Rain"]
   [:hr]
   [:div.container-fluid
    [:h3 "Amount:"]
    [:input.input-form {:id "rain-amount":type "text" :name "amount"}]
    [:p "Estimated transaction fee is "]
    [:h3 "Distance (KM):"]
    [:input {:type "range" :min "1" :max "100" :value "1" :class "slider" :id "rain-radius-range"}]
    [:row {:style {:text-align "center"}}
     [:div.col-lg-6 [:h1 {:on-click (fn [] (send-event-and-close "rain"
                                             (fn [] (send [:action/rain {:amount (.-value (.getElementById js/document "rain-amount"))
                                                                         :radius (.-value (.getElementById js/document "rain-radius-range"))}]))))}
                     "Make it Rain!"]]
     [:div.col-lg-6 [:h1 {:on-click modal/close-modal!} "Cancel"]]]]])

(defn storm-modal [state send]
  [:div
   [:h1 {:style {:text-align "center"}} "Storm"]
   [:hr]
   [:div.container-fluid
    [:h3 "Amount:"]
    [:input.input-form {:id "storm-amount":type "text" :name "amount"}]
    [:p "Estimated transaction fee is "]
    [:h3 "Distance (KM):"]
    [:input {:type "range" :min "1" :max "100" :value "1" :class "slider" :id "storm-radius-range"}]
    [:h3 "Duration (hrs):"]
    [:input.input-form {:id "storm-duration" :type "text" :name "storm-duration"}]
    [:row {:style {:text-align "center"}}
     [:div.col-lg-6 [:h1 {:on-click (fn [] (send-event-and-close "storm"
                                             (fn [] (send [:action/storm {:amount (.-value (.getElementById js/document "storm-amount"))
                                                                          :radius (.-value (.getElementById js/document "storm-radius-range"))
                                                                          :duration (.-value (.getElementById js/document "storm-duration"))}]))))} 
                     "Start storm!"]]
     [:div.col-lg-6 [:h1 {:on-click modal/close-modal!} "Cancel"]]]]])

(defn bowl-modal [state send]
  [:div
   [:h1 {:style {:text-align "center"}} "Bowl"]
   [:hr]
   [:div.container-fluid
    [:h3 "Amount:"]
    [:input.input-form {:id "bowl-amount":type "text" :name "amount"}]
    [:p "Estimated transaction fee is "]
    [:h3 "Duration (hrs):"]
    [:input.input-form {:id "bowl-duration" :type "text" :name "duration"}]
    [:row {:style {:text-align "center"}}
     [:div.col-lg-6 [:h1 {:on-click (fn [] (send-event-and-close "bowl"
                                             (fn [] (send [:action/bowl {:amount (.-value (.getElementById js/document "bowl-amount"))
                                                                         :duration (.-value (.getElementById js/document "bowl-duration"))}]))))} 
                     "Create bowl!"]]
     [:div.col-lg-6 [:h1 {:on-click modal/close-modal!} "Cancel"]]]]])

(defn slurp-modal [state send]
  [:div
   [:h1 {:style {:text-align "center"}} "Slurp"]
   [:hr]
   [:div.container-fluid
    [:h3 "Bowl Code:"]
    [:input.input-form {:id "bowl-code" :type "text" :name "code"}]
    [:row {:style {:text-align "center"}}
     [:div.col-lg-6 [:h1 {:on-click (fn [] (send-event-and-close "slurp"
                                             (fn [] (send [:action/slurp {:code (.-value (.getElementById js/document "bowl-code"))}]))))} 
                     "Slurp bowl!"]]
     [:div.col-lg-6 [:h1 {:on-click modal/close-modal!} "Cancel"]]]]])    
