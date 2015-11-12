(ns rhgbot.async
  (:refer-clojure :exclude (run!))
  (:require [cljs.core.async :as a]))

(defn transform
  "Returns a new channel with every element from `ch` going through `xform`"
  [xform ch]
  (let [out (a/chan)]
    (a/pipeline 1 out xform ch)
    out))

(defn run!
  "Runs a function on every item that arrives on the channel.
   Via `cljs.core.async/reduce`.
   Returns a channel that closes when the items are processed."
  [f ch]
  (a/reduce #(f %2)
            nil
            ch))

(defn tap
  "Returns an unbuffered channel with items from a mult"
  [mult]
  (let [ch (a/chan)]
    (a/tap mult ch)
    ch))
