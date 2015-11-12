(require 'cljs.repl 'cljs.repl.node)

(cljs.repl/repl (cljs.repl.node/repl-env)
                :watch "src")
