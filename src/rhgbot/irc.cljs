(ns rhgbot.irc
  (:require [rhgbot.async :as ra]))

(defprotocol Message
  "A message from any source"
  (-text [this] "Returns a string containing the text")
  (-channel [this] "Returns the channel that this came on if any"))

(defprotocol IRC
  "All these methods must not return the same object on repeat calls"
  (-messages [this] "Returns a channel of messages"))

(defn commands
  "Returns a channel of all prefixed commands"
  [irc options]
  (->> (-messages irc)
       (ra/transform (filter (comp (partial re-find (:command-prefix options))
                                   -text)))))
