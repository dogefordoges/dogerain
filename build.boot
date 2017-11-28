(set-env!
  :source-paths #{"src/clj" "src/cljc" "src/cljs"}
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/boot-cljs "2.1.4" :scope "test"]
                  [adzerk/boot-reload "0.5.2" :scope "test"]
                  [adzerk/boot-cljs-repl   "0.3.3" :scope "test"]
                  ; used by boot-cljs-repl
                  [com.cemerick/piggieback "0.2.1" :scope "test"]
                  [weasel "0.7.0" :scope "test"]
                  [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                  ; project deps
                  [org.clojure/clojure "1.8.0"]
                  [org.clojure/clojurescript "1.9.946" :scope "test"]
                  [reagent "0.7.0" :scope "test"]
                  [ring "1.5.1"]
                  [nightlight "1.9.2" :scope "test"]
                  [http-kit "2.2.0"]
                  [secretary "1.2.3"]
                  [ring/ring-defaults "0.1.5"]
                  [com.taoensso/sente "1.11.0"]
                  [com.taoensso/timbre "4.7.4"]
                  [compojure "1.6.0"]
                  [org.clojure/core.async "0.3.443"]
                  [org.clojars.frozenlock/reagent-modals "0.2.8"]])

(task-options!
  pom {:project 'dogerain
       :version "1.0.0-SNAPSHOT"
       :description "FIXME: write description"}
  aot {:namespace '#{dogerain.core}}
  jar {:main 'dogerain.core}
  sift {:include #{#"\.jar$"}})

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-reload :refer [reload]]
  '[adzerk.boot-cljs-repl :refer [start-repl cljs-repl]]
  'dogerain.core
  '[nightlight.boot :refer [nightlight]])

(deftask run []
  (comp
    (with-pass-thru _
      (dogerain.core/-main))
    (watch)
    (reload :asset-path "public")
    (cljs-repl :nrepl-opts {:port 9009})
    (cljs :source-map true :optimizations :none :compiler-options {:asset-path "main.out"})
    (target)))

(deftask build []
  (comp
    (cljs :optimizations :advanced)
    (aot)
    (pom)
    (uber)
    (jar)
    (sift)
    (target)))

(deftask night []
  (comp
    (wait)
    (nightlight :port 4000)))

