(ns dogerain.core
  (:require [ring.middleware.defaults]
            [clojure.java.io :as io]
            [org.httpkit.server :refer [run-server]]
            [org.httpkit.client :as http]
            [hiccup.core :as hiccup]
            [clojure.core.async :as async  :refer (<! <!! >! >!! put! chan go go-loop)]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route])  
  (:gen-class))

(def dogecoin-api-key "d817-ca1e-ed72-71c7")

(defn new-address []
  (http/get (str "https://block.io/api/v2/get_new_address/?api_key=" dogecoin-api-key)))

(let [;; Serializtion format, must use same val for client + server:
      packer :edn ; Default packer, a good choice in most cases

      chsk-server
      (sente/make-channel-socket-server!
       (get-sch-adapter) {:packer packer})

      {:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      chsk-server]

  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids)) ; Watchable, read-only atom
  
  
;; We can watch this atom for changes if we like
(add-watch connected-uids :connected-uids
  (fn [_ _ old new]
    (when (not= old new)
      (infof "Connected uids change: %s" new))))

(defn index-page-handler [ring-req]
    (hiccup/html
        [:link {:rel "stylesheet" :type "text/css" :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"}]
        [:link {:rel "stylesheet" :type "text/css" :href "./stylesheet.css"}]
        [:script {:src "https://code.jquery.com/jquery-2.1.1.min.js"}]
        [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"}]
        [:div {:id "content"}]
        [:script {:src "main.js"}]))

(defn login-handler
  "Here's where you'll add your server-side login/auth procedure (Friend, etc.).
  In our simplified example we'll just always successfully authenticate the user
  with whatever user-id they provided in the auth request."
  [ring-req]
  (let [{:keys [session params]} ring-req
        {:keys [user-id]} params]
    (debugf "Login request: %s" params)
    {:status 200 :session (assoc session :uid user-id)}))

(defroutes ring-routes
  (GET  "/"      ring-req (index-page-handler            ring-req))
  (GET  "/chsk"  ring-req (ring-ajax-get-or-ws-handshake ring-req))
  (POST "/chsk"  ring-req (ring-ajax-post                ring-req))
  (POST "/login" ring-req (login-handler                 ring-req))
  (route/resources "/") ; Static files, notably public/main.js (our cljs target)
  (route/not-found "<h1>Page not found</h1>"))

(def main-ring-handler
  "**NB**: Sente requires the Ring `wrap-params` + `wrap-keyword-params`
  middleware to work. These are included with
  `ring.middleware.defaults/wrap-defaults` - but you'll need to ensure
  that they're included yourself if you're not using `wrap-defaults`."
  (ring.middleware.defaults/wrap-defaults
    ring-routes ring.middleware.defaults/site-defaults))

(defn test-fast-server>user-pushes
  "Quickly pushes 100 events to all connected users. Note that this'll be
  fast+reliable even over Ajax!"
  []
  (doseq [uid (:any @connected-uids)]
    (doseq [i (range 100)]
      (chsk-send! uid [:fast-push/is-fast (str "hello " i "!!")]))))

(defonce broadcast-enabled?_ (atom true))

(defn start-example-broadcaster!
  "As an example of server>user async pushes, setup a loop to broadcast an
  event to all connected users every 10 seconds"
  []
  (let [broadcast!
        (fn [i]
          (let [uids (:any @connected-uids)]
            (debugf "Broadcasting server>user: %s uids" (count uids))
            (doseq [uid uids]
              (chsk-send! uid
                [:some/broadcast
                 {:what-is-this "An async broadcast pushed from server"
                  :how-often "Every 10 seconds"
                  :to-whom uid
                  :i i}]))))]

    (go-loop [i 0]
      (<! (async/timeout 10000))
      (when @broadcast-enabled?_ (broadcast! i))
      (recur (inc i)))))

(defmulti -event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id) ; Dispatch on event-id
  

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (-event-msg-handler ev-msg)) ; Handle event-msgs on a single thread
  ;; (future (-event-msg-handler ev-msg)) ; Handle event-msgs on a thread pool
  

(defmethod -event-msg-handler
  :default ; Default/fallback case (no other matching handler)
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]
    (debugf "Unhandled event: %s" event)
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))

(defmethod -event-msg-handler :action/withdraw
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (println (str "WITHDRAW" ?data)))

(defmethod -event-msg-handler :action/rain
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}] 
  (println (str "RAIN" ?data)))

(defmethod -event-msg-handler :action/storm
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}] 
  (println (str "STORM" ?data)))

(defmethod -event-msg-handler :action/bowl
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (println (str "BOWL" ?data)))

(defmethod -event-msg-handler :action/slurp
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (println (str "SLURP" ?data)))

(defmethod -event-msg-handler :example/toggle-broadcast
  [{:as ev-msg :keys [?reply-fn]}]
  (let [loop-enabled? (swap! broadcast-enabled?_ not)]
    (?reply-fn loop-enabled?)))

(defonce router_ (atom nil))
(defn  stop-router! [] (when-let [stop-fn @router_] (stop-fn)))
(defn start-router! []
  (stop-router!)
  (reset! router_
    (sente/start-server-chsk-router!
      ch-chsk event-msg-handler)))

(defn -main [& args]
    (start-router!)
    (run-server (var main-ring-handler) {:port 3000}))
