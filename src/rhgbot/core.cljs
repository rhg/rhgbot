(ns rhgbot.core
  (:require [com.stuartsierra.component :as component
              :refer (start)]
            [com.stuartsierra.dependency :as dep]
            [cljs.core.async :as a]
            [rhgbot.async :as ra]
            [rhgbot.irc :as irc]
            [rhgbot.irc.node-irc :refer (node-irc)]))

(defn handle-command
  "Invokes all functions that match a command
   `data` is passed as the first argument to the function
   `command` is the text of the message
   `commands` is a sequence of regex function pairs"
  [data command commands]
  (doseq [[re f] commands]
    (when-let [m (re-find re command)]
      (f data m))))

(def freenode
  {:host "asimov.freenode.net"
   :port 6697
   :secure true})

(defn -main
  [& _]
  (let [irc (node-irc "rhgbot" (assoc freenode :channels ["#rhg135"]))]
    (start irc)))

(set! *main-cli-fn* -main)
