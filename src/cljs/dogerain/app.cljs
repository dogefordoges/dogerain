(ns dogerain.app
  (:require [reagent.core :as r]
            [secretary.core :as secretary]
            [reagent-modals.modals :as modal]
            [dogerain.app-modal :as app-modal]))

(defn set-border-color! [id color]
  (fn [e] (set! (.-borderColor (.-style (.getElementById js/document id))) color)))

(defn dogerain-button [text action]
  [:div.container-fluid.click-me
   {:id text
    :on-mouse-over (set-border-color! text "black")
    :on-mouse-out (set-border-color! text "#fff5cc")
    :on-mouse-down (set-border-color! text "black")
    :on-mouse-up (set-border-color! text "#fff5cc")
    :on-click (fn [e] (action))}
   [:h3 text]])

(defn instructions [state send]
  [:div.container-fluid.instructions
   [:row 
    [:div.col-md-4
     [dogerain-button "deposit" #(modal/modal! [app-modal/deposit-modal state])]  
     [:p "Send funds to your dogerain wallet from another dogecoin address (could be a hardware wallet or an exchange)."]
     [dogerain-button "withdraw" #(modal/modal! [app-modal/withdraw-modal state send])]
     [:p "Withdraw funds from your dogerain wallet to another address (could be a hardware wallet or an exchange)."]] 
    [:div.col-md-4
     [dogerain-button "rain" #(modal/modal! [app-modal/rain-modal state send])]
     [:p "Rain dogecoin from up above to all the lucky shibes who happen to be in your area."]
     [dogerain-button "storm" #(modal/modal! [app-modal/storm-modal state send])]
     [:p "Start a dogestorm in your area for a period of time or until the doge runs out."]] 
    [:div.col-md-4
     [dogerain-button "bowl" #(modal/modal! [app-modal/bowl-modal state send])]
     [:p "Create a bowl (faucet) for other shibes to drink from for a period of time, and taste the glory that is dogecoin."]
     [dogerain-button "slurp" #(modal/modal! [app-modal/slurp-modal state send])]
     [:p "Redeem doge with a bowl code from another shibe. Very nice!"]]]])   

(defn sketch []
  [:div.col-md-4 {:id "animation-sketch"}
   [:div [:img {:src "img/dogeshine.png"
                :style {:max-width "100%"
                        :width "auto"
                        :height "auto\\9"}}]]])

(defn data-feed [state]
  [:div.col-md-4.data-feed
   ;[:h3 (str "Balance (Ð):" (:balance @state))]
   [:h3 (str "Balance (Ð): " 42.3467)]
   [:p "Please be aware that 1Ð = 1Ð"]
   [:div.updates
    [:hr]
    [:h3 "Updates:"]
    [:div.container
     [:p "niceshibe42 made it rain 300Ð around your location"]
     [:p "dogedoggo started to storm 1000Ð for 10 minutes around your location"]
     [:p "You slurped 20Ð from coolshibes bowl"]
     [:p "You withdrew 100Ð from your account"]]]])

(defn set-map-image! [state]
  (let [width (.-offsetWidth (.getElementById js/document "map-dashboard"))
        height (.-offsetHeight (.getElementById js/document "app-screen"))
        url (str "https://maps.googleapis.com/maps/api/staticmap?center=" 
              (:latitude @state) "," (:longitude @state) 
              "&zoom=13&size=" width "x" height "&key=AIzaSyCIhPvixqJM9uVQIvKj2u_-xI4PxbKlTyM")]
    (swap! state assoc :map url)))

(defn set-position [state]
  (fn [position]
    (swap! state assoc :latitude position.coords.latitude.)
    (swap! state assoc :longitude position.coords.longitude.)
    (set-map-image! state)))
    
(defn get-location! [state]
  (.getCurrentPosition js/navigator.geolocation. (set-position state)))
     
(defn map-dashboard [state]
  (r/create-class 
    {:component-did-mount 
     (fn [this] (get-location! state))                          
     :reagent-render
     (fn [state]
       [:div.col-md-4.map-dashboard
        {:id "map-dashboard"}
        (if (not js/navigator.geolocation.)
          [:p "Location is not supported by your browser"] 
          [:div 
           [:img {:id "map"
                  :src (:map @state)
                  :style {:max-width "100%"
                          :width "auto"
                          :height "auto\\9"}}]
           [:div {:id "coordinates"} 
            [:p (str "Latitude: " (:latitude @state)) " | " (str "Longitude: " (:longitude @state))]]])])})) 

(defn app [state send]
  (if (:signed-in? @state)
    (do 
      [:div 
       [:div#header [:h1 "dogerain, much wow"]]
       [:hr]
       [:div.container-fluid.app-screen {:id "app-screen"}
        [:row
         [map-dashboard state] 
         [data-feed state]
         [sketch]]]
       [:hr]
       [instructions state send]
       [modal/modal-window]])    
    (secretary/dispatch! "#/")))
