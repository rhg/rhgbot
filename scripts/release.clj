(require '[cljs.build.api :as build])

(build/build "src"
             {:output-to "index.js"
              :main 'rhgbot.core
              ; :optimizations :simple
              :target :nodejs})
