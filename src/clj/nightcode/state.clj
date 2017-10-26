(ns nightcode.state
  (:require [clojure.edn :as edn]
            [clojure.spec.alpha :as s :refer [fdef]]
            [nightcode.utils :as u]))

; preferences

(declare runtime-state)

(defn write-pref!
  "Writes a key-value pair to the preference file."
  [k v]
  (when-let [prefs (:prefs @runtime-state)]
    (doto prefs
      (.put (name k) (pr-str v))
      .flush)))

(defn remove-pref!
  "Removes a key-value pair from the preference file."
  [k]
  (when-let [prefs (:prefs @runtime-state)]
    (doto prefs
      (.remove (name k))
      .flush)))

(defn read-pref
  "Reads value from the given key in the preference file."
  ([k]
   (read-pref k nil))
  ([k default-val]
   (when-let [prefs (:prefs @runtime-state)]
     (if-let [string (.get prefs (name k) nil)]
       (edn/read-string string)
       default-val))))

; state

(defonce pref-state (atom {}))

(defonce runtime-state (atom {:web-port nil
                              :projects {}
                              :editor-panes {}
                              :bridges {}
                              :processes {}
                              :stage nil
                              :prefs nil}))

(defn init-pref-state! []
  (reset! pref-state
    {:project-set (read-pref :project-set #{})
     :expansion-set (u/filter-paths (read-pref :expansion-set #{}))
     :selection (read-pref :selection)
     :theme (read-pref :theme :dark)
     :text-size (read-pref :text-size 16)
     :auto-save? (read-pref :auto-save? true)})
  (add-watch pref-state :write-prefs
    (fn [_ _ old-state new-state]
      (doseq [key [:project-set :expansion-set :selection :theme :text-size :auto-save?]]
        (let [old-val (get old-state key)
              new-val (get new-state key)]
          (when (not= old-val new-val)
            (write-pref! key new-val)))))))

; specs

(fdef write-pref!
  :args (s/cat :key keyword? :val any?))

(fdef remove-pref!
  :args (s/cat :key keyword?))

(fdef read-pref
  :args (s/alt
          :key-only (s/cat :key keyword?)
          :key-and-val (s/cat :key keyword? :default-val any?)))

