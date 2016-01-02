(defproject {{name}} "0.0.1-SNAPSHOT"
  :description "FIXME: write this!"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [quil "2.2.6" :exclusions [org.clojure/clojure]]
                 [ring "1.3.2"]]
  :source-paths ["src/clj"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :cljsbuild { 
    :builds [{:source-paths ["src/cljs"]
              :compiler {:output-to "resources/public/cljs.js"
                         :optimizations :advanced
                         :pretty-print false}
              :jar true}]}
  :aot [{{namespace}}]
  :main {{namespace}}
  :ring {:handler {{namespace}}/app})
