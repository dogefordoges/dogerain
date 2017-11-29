(ns dogerain.test
  (:require [dogerain.core :as dr]))

(dr/create-accounts-table dr/db)

(dr/insert-account dr/db {:username "fooboo" :password "dude" :public_key "123456" :private_key "12345678"})

(dr/update-user-location dr/db {:username "fooboo" :longitude 153.25 :latitude 134.56 })

(println (dr/get-deposit-address dr/db {:username "fooboo"}))

(println (dr/get-private-key dr/db {:username "fooboo" :password "dude"}))

(println (dr/get-users-near dr/db {:radius 100 :current_lat 134.56 :current_long 153.25}))

(dr/drop-accounts-table dr/db)

;(dr/create-storms-table dr/db)

(dr/insert-storm dr/db {:username "fooboo" :duration 10 :p1_lat 0.5 :p1_long 1.4 :p2_lat 0.6 :p2_long 0.8 :p3_lat 0.9 :p3_long 1.6 :p4_lat 4.5 :p4_long 6.7})

(println (dr/get-storms-nearby dr/db {:current_lat 0.5 :current_long 0.6 :username "fooboo"}))

(dr/drop-storms-table dr/db)

(dr/create-bowls-table dr/db)

(dr/insert-bowl dr/db {:username "fooboo" :duration 100 :bowl_code "12345"})

(println (dr/get-bowl dr/db {:bowl_code "12345"}))

(dr/drop-bowls-table dr/db)