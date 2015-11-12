(ns rhgbot.irc.node-irc
  (:require [cljs.nodejs :as nodejs]
            [com.stuartsierra.component :refer (Lifecycle)]
            [cljs.core.async :as a]
            [rhgbot.irc :as irc]))

(let [irc (nodejs/require "irc")
      Client (.-Client irc)]
  (defn- client
    [nick configuration]
    (Client. (:host configuration) nick
             (clj->js configuration))))

(defrecord IRC [nick configuration]
  irc/IRC
  (-messages [this]
    (let [out (a/chan)]
      (.on (:client this) "message"
          (fn [_ to text _]
            (a/put! out
              (reify irc/Message
                (-text [_] text)
                (-channel [_]
                  (when (= \# (first to))
                    to))))))
      out))
  Lifecycle
  (start [this]
    (if (:client this)
      this
      (assoc this :client (client nick configuration))))
  (stop [this]
    (if-not (:client this)
      this
      (do (.disconnect (:client this) "Going down")
          (dissoc this :client)))))

(def node-irc*
  ->IRC)

(defn node-irc
  "Returns a new component that also implements irc
   Is not started by default"
   [nick configuration]
   (node-irc* nick configuration))
