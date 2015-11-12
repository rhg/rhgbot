(ns rhgbot.loader
  (:require
    [com.stuartsierra.dependency :as dep]
    [clojure.set :as set]
    [rhgbot.error :refer (error success)]))

(defrecord Loader [loaded graph commands])

(defn loader
  "Returns a new immutable loader"
  []
  (->Loader #{} (dep/graph) []))

(defn engine
  "Returns a new engine plugin"
  [version]
  {:id ['core/engine version]
   :dependencies #{}
   :commands [[#"state" (fn [{:keys [app db]} _]
                          (println app)
                          db)]]})
(defn- depend-plugin
  "Makes `id` depend on every item in `dependencies`"
  [graph id dependencies]
  (reduce (fn [graph dep]
            (dep/depend graph id dep))
          graph
          dependencies))

(defn load-plugin*
  "Just loads a plugin. Pure"
  [loader plugin]
  (let [{:keys [id dependencies]} plugin
        {:keys [loaded]}          loader]
      (-> loader
        (update :graph depend-plugin id dependencies)
        (update :loaded conj id))))

(defn- undepend-ids
  "removes all dependencies on anything in `ids`"
  [graph ids]
  (reduce dep/remove-all graph ids))

(defn unload-plugin*
  "Just unloads a set of ids"
  [loader ids]
  (-> loader
      (update :loaded set/difference ids)
      (update :graph undepend-ids ids)))

(defn fail-on-missing-deps
  "Return a nice error if any deps are not satisfied"
  [f]
  (fn [loader plugin]
    (doseq [dep (:dependencies plugin)]
      (when-not (contains? (:loaded loader) dep)
        (error (ex-info "dependency missing"
                        {:dep dep :loaded (:loaded loader)}))))
    (success (f loader plugin))))

(defn load-plugin
  "Loads a plugin subject to a stack of middleware"
  ([loader plugin] (load-plugin loader plugin fail-on-missing-deps))
  ([loader plugin middleware]
    ((middleware load-plugin*) loader plugin)))

(defn unload-deps-too
  "Make sure we also unload all plugins that depend on it"
  [f]
  (fn [loader id]
    (success (conj (dep/transitive-dependents (:graph loader) id) id))))

(defn unload-plugin
  "Unloads a plugin given the id"
  ([loader plugin] (unload-plugin loader plugin unload-deps-too))
  ([loader id middleware]
   ((middleware unload-plugin*) loader id)))
