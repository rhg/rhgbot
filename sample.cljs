(require '[rhgbot.irc :as irc])

(def conf
  {:host "asimov.freenode.net"
   :port 6697
   :secure true})

(def irc
  (irc/node-irc "rhgbot" conf))

(bot/start! irc
            {:command-prefix #"^@rhgbot"
             :plugins '[[core/facts "0.0.1"]]})
