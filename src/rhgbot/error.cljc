(ns rhgbot.error
  (:refer-clojure :exclude (>=)))

(defrecord Maybe [error success])

(defn error?
  [monadic-value]
  (some? (:error monadic-value)))

(defn error
  "Returns a Maybe that represents an error"
  [error]
  (->Maybe error nil))

(defn success
  "Returns a Maybe that represents a success"
  [value]
  (->Maybe nil value))

(defn >=
  "Monadic bind. Shorts on an error."
  [monadic-value f]
  (if (error? monadic-value)
      monadic-value
      (f (:success monadic-value))))
