(ns leiningen.iplant-rpm
  (:use [clojure.string :only (split)]
        [fleet]))

(def test-settings
  {:summary "Distro summary."
   :name "distro"
   :version "1.0.0"
   :release "1"
   :provides "distro"
   :dependencies ["foo >= 1.0.2" "bar >= 2.1.1.2"]
   :jar-version "1.0.0-SNAPSHOT"
   :description "Distro description."
   :config-files ["foo.properties" "bar.properties"]})

(def spec-path "rpm/spec.fleet")

(defn- slurp-resource [resource-path]
  (let [loader (.. (Thread/currentThread) getContextClassLoader)]
    (slurp (.getResourceAsStream loader resource-path))))

(defn- load-template [template-path]
  (fleet [spec] (slurp-resource template-path) {:escaping :bypass}))

(defn- generate-spec [settings]
  ((load-template spec-path) settings))

(defn- project-to-settings [project]
  (let [settings (get project :iplant-rpm {})]
    (assoc settings
           :name (:name project)
           :description (:description project)
           :jar-version (:version project)
           :version (first (split (:version project) #"-"))
           :description (:description project)
           :config-path (:config-path settings))))

(defn iplant-rpm [project]
  (print (str (generate-spec (project-to-settings project))))
  (flush))
