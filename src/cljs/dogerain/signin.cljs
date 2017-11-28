(ns dogerain.signin
  (:require [reagent.core :as r]
            [secretary.core :as secretary]
            [taoensso.sente :as sente]))

;;change this to about page, with login as left sidebar

(defn info []
 [:div
  [:h2 "Learn More:"]
  [:p "To learn more about dogecoin and dogecoin related projects check out these links:"]
  [:p [:a {:href "http://dogecoin.com/"} "Official Dogecoin Website"]]
  [:p [:a {:href "https://www.reddit.com/r/dogecoin/wiki/index"} "Dogecoin wiki"]]
  [:p [:a {:href "https://www.reddit.com/r/dogecoin/"} "r/dogecoin"]]])

(defn signin-page [state sign-in sign-up]
  [:div.login-page
   [:div.forms-section.container {:style {:height "100vh"}}
    [:div.top-portion.container
     {:style {:text-align "center" :font-size "5em"}}
     [:p [:big "Welcome to dogerain!"]]]
    [:div.login-form.container
     [:row
      [:div.col-md-2]
      [:div.col-md-8
       [:div.spacing]
       [:div
        "Username:"
        [:br]
        [:input.input-form {:type "text" :name "username"}] 
        [:br]
        "Password:"
        [:br]
        [:input.input-form {:type "password" :name "password"}]
        [:br]
        [:input {:type "submit" :value "Sign In!" :on-click (sign-in state)}]
        [:hr]]]]]
    [:div.sign-up-form.container
     [:row
      [:div.col-md-2]
      [:div.col-md-8
       [:div.spacing]
       [:div
        "Username:"
        [:br]
        [:input.input-form {:type "text" :name "username"}] 
        [:br]
        "Password:"
        [:br]
        [:input.input-form {:type "password" :name "password"}]
        "Password Again:"
        [:br]
        [:input.input-form {:type "password" :name "password-again"}]
        [:br]
        [:input {:type "submit" :value "Sign Up!" :on-click sign-up}]
        [:br]]]]]]])
