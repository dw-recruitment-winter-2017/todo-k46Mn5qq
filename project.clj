(defproject dworks "0.1.0-SNAPSHOT"
  :description "Democracy Works : todo exercise"
  :url "http://example.com/anonymous"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.473" :scope "provided"]
                 [compojure "1.5.1"]
                 [lein-doo "0.1.7"]
                 [hiccup "1.0.5"]
                 [reagent "0.6.0"]
                 [reagent-utils "0.2.0"]
                 [ring "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring-server "0.4.0"]
                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.7" :exclusions [org.clojure/tools.reader]]
                 [yogthos/config "0.8"]]

  :plugins [[lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.1"]
            [lein-doo "0.1.7"]
            [lein-asset-minifier "0.2.7" :exclusions [org.clojure/clojure]]]

  :ring {:handler dworks.handler/app
         :uberwar-name "dworks.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "dworks.jar"

  :main dworks.server

  :hooks [leiningen.cljsbuild]

  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir]
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets {:assets {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:min {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
                             :compiler {:output-to "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/uberjar"
                                        :optimizations :advanced
                                        :pretty-print  false}}
                       :app {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                             :compiler {:main "dworks.dev"
                                        :asset-path "/js/out"
                                        :output-to "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :source-map true
                                        :optimizations :none
                                        :pretty-print  true}}
                       :test {:source-paths ["src/cljs" "src/cljc" "test/cljs"]
                              :compiler {:main dworks.doo-runner
                                         :asset-path "/js/out"
                                         :output-to "target/test.js"
                                         :output-dir "target/cljstest/public/js/out"
                                         :optimizations :whitespace
                                         :pretty-print true}}}}

  :doo {:build "test"
        :alias {:default [:phantom]}}

  :figwheel {:http-server-root "public"
             :server-port 3449
             :nrepl-port 7002
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
             :css-dirs ["resources/public/css"]
             :ring-handler dworks.handler/app}

  :profiles {:dev {:repl-options {:init-ns dworks.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :dependencies [[ring/ring-mock "0.3.0"]
                                  [ring/ring-devel "1.5.0"]
                                  [prone "1.1.4"]
                                  [figwheel-sidecar "0.5.8"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                                  [pjstadig/humane-test-output "0.8.1"]]
                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.8"]
                             [lein-doo "0.1.6"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]
                   :env {:dev true}}
             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
