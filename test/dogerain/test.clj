(ns dogerain.test
  (:require [dogerain.core :as dr]))

(dr/insert-account dr/db {:username "fooboo" :password "dude" :longitude 153.25 :latitude 134.56 :public_key "123456" :private_key "12345678"})

(println (dr/get-accounts dr/db))
