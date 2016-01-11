(defproject jodadbg "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [;; Backend
                 [org.clojure/clojure "1.7.0"]
                 [clj-time "0.11.0"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 ;; Frontend
                 [org.clojure/clojurescript "1.7.189"]
                 [org.clojure/core.async "0.2.374"]
                 [cljs-http "0.1.39"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [org.omcljs/om "1.0.0-alpha28"]
                 [racehub/om-bootstrap "0.5.3"]
                 [doo "0.1.6"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-cljsbuild "1.1.2"]
            [lein-pdo "0.1.1"]
            [lein-doo "0.1.6"]]
  :hooks [leiningen.cljsbuild]
  :aliases {"up"     ["pdo" "cljsbuild" "auto" "dev," "ring" "server-headless"]
            "brepl"  ["trampoline" "cljsbuild" "repl-listen"]
            "jsrepl" ["trampoline" "cljsbuild" "repl-rhino"]}
  :ring {:handler jodadbg.handler/app}
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :cljsbuild {:builds {:dev {:source-paths ["src/cljs"]
                             :compiler {:output-to "env/dev/resources/public/js/app.js"
                                        :output-dir "env/dev/resources/public/js/out"
                                        :optimizations :none
                                        :source-map true
                                        :pretty-print true}}
                       :test {:source-paths ["src/cljs" "test/cljs"]
                              :compiler {:output-to "env/test/resources/public/js/test.js"
                                         :output-dir "env/test/resources/public/js/out"
                                         :main jodadbg.runner
                                         :optimizations :whitespace
                                         :pretty-print true}}
                       :release {:source-paths ["src/cljs"]
                                 :compiler {:output-to "resources/public/js/app.js"
                                            :optimizations :advanced
                                            :pretty-print false}}}
              :test-commands {"unit" ["lein" "doo"
                                      "slimer" "test" "once"]}}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]
         :resource-paths ["env/dev/resources"]}
   :test {:resource-paths ["env/test/resources"]}}
  :target-path "target/%s"
  :clean-targets ^{:protect false} ["target"
                                    "resources/public/js"
                                    "env/dev/resources/public/js"
                                    "env/test/resources/public/js" ])
