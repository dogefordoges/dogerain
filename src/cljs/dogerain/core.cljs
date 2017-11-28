(ns dogerain.core
  (:import goog.History)
  (:require [reagent.core :as r]
            [goog.events :as events]
            [clojure.string :as str]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary :refer-macros [defroute]]
            [cljs.core.async :as async :refer [<! >! put! chan]]
            [taoensso.encore :as encore :refer-macros (have have?)]
            [taoensso.timbre :as timbre :refer-macros (tracef debugf infof warnf errorf)]
            [taoensso.sente :as sente :refer [cb-success?]]
            [dogerain.signin :refer [signin-page]]
            [dogerain.app :refer [app]])
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer-macros [go go-loop]]))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" ; Note the same path as before
       {:type :auto})] ; e/o #{:auto :ajax :ws}
       
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state))  ; Watchable, read-only atom

(defmulti -event-msg-handler :id) ; Dispatch on event-id

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler
  :default ; Default/fallback case (no other matching handler)
  [{:as ev-msg :keys [event]}]
  (debugf "Unhandled event: %s" event))

(defmethod -event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (let [[old-state-map new-state-map] (have vector? ?data)]
    (if (:first-open? new-state-map)
      (debugf "Channel socket successfully established!: %s" new-state-map)
      (debugf "Channel socket state change: %s"              new-state-map))))

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (debugf "Push event from server: %s" ?data))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (debugf "Handshake: %s" ?data)))

(def router_ (atom nil))
(defn  stop-router! [] (when-let [stop-f @router_] (stop-f)))
(defn start-router! []
  (stop-router!)
  (reset! router_ (sente/start-chsk-router! ch-chsk event-msg-handler)))

(def app-state (r/atom {}))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! app-state assoc :page :signin))

  (defroute "/app" []
    (swap! app-state assoc :page :app))
  
  (hook-browser-navigation!))

(defn sign-in [state]
  (fn []
    (sente/ajax-lite "/login"
     {:method :post
      :headers {:X-CSRF-Token (:csrf-token @chsk-state)}}
     (fn [res]
       (debugf (str res))
       (swap! state assoc :signed-in? true)
       (secretary/dispatch! "#/app")))))

(defn sign-up []
  (.log js/console "sign up"))

(defmulti current-page #(@app-state :page))
(defmethod current-page :app []
  [app app-state chsk-send!])
(defmethod current-page :signin [] 
  [signin-page app-state sign-in sign-up])
(defmethod current-page :default [] 
  [:div])
          
(defn init []
  (start-router!)
  (app-routes)
  (r/render-component [current-page]
    (.getElementById js/document "content")))

(init)
