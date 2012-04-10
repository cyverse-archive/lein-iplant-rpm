(ns leiningen.iplant-rpm
  (:use [clojure.java.io :only (file copy)]
        [clojure.string :only (split)]
        [fleet]))

(def ^{:private true} spec-path "rpm/spec.fleet")
(def ^{:private true} init-path "rpm/init.fleet")

(defn- slurp-resource
  "Slurps the contents of a resource that can be found relative to a location
   on the classpath."
  [resource-path]
  (let [loader (.. (Thread/currentThread) getContextClassLoader)]
    (slurp (.getResourceAsStream loader resource-path))))

(defn- load-template
  "Loads a Fleet template from a template file that is located relative to a
   location on the classpath."
  [template-path]
  (fleet [spec] (slurp-resource template-path) {:escaping :bypass}))

(defn- project-to-settings
  "Converts a project map to the settings map that we need to fill in the
   templates."
  [project]
  (let [settings (get project :iplant-rpm {})]
    (assoc settings
           :name (:name project)
           :description (:description project)
           :jar-version (:version project)
           :version (first (split (:version project) #"-"))
           :description (:description project))))

(defn- warn
  "Prints a warning message to standard error output."
  [& ms]
  (binding [*out* *err*]
    (println (apply str ms))
    (flush)))

(defn- gen-file
  "Generates a file with the given name using the given template name."
  [settings file-name template-name]
  (spit file-name (str ((load-template template-name) settings))))

(defn- mkdirs
  "Creates a directory and any parent directories that need to be created.  If
   the directory already exists then this function is a no-op."
  [dir]
  (let [f (file dir)]
    (if (.exists f)
      (when (not (.isDirectory f))
        (throw (Exception. (str dir " exists and is not a directory"))))
      (when (not (.mkdirs f))
        (throw (Exception. (str "unable to create " dir)))))))

(declare rec-copy)

(defn- copy-dir
  "Copies the contents of a directory to another directory."
  [dest f]
  (mkdirs dest)
  (rec-copy dest (seq (.listFiles f))))

(defn- copy-file-or-dir
  "Copies either a file or a directory."
  [dir f]
  (let [dest (file dir (.getName f))]
    (cond (.isFile f) (copy f dest)
          (.isDirectory f) (copy-dir dest f)
          :else (throw (Exception. "unrecognized file type")))))

(defn- rec-copy
  "Performs a recursive copy of one or more files."
  [dir fs]
  (dorun (map #(copy-file-or-dir dir %) fs)))

(defn- make-build-dir
  "Creates the build directory, which will be used to generate the source
   tarball."
  [build-dir settings init-name]
  (let [config-dir (file (:config-path settings))]
    (mkdirs build-dir)
    (rec-copy build-dir (map #(file %) [init-name "project.clj" "src"]))
    (rec-copy (.getParentFile (file build-dir config-dir)) [config-dir])))

(defn- build-rpm
  "Builds the RPM."
  [prj]
  (let [settings (project-to-settings prj)
        init-name (:name settings)
        spec-name (str init-name ".spec")
        build-dir (file (str (:name settings) "-" (:version settings)))]
    (gen-file settings init-name init-path)
    (gen-file settings spec-name spec-path)
    (make-build-dir build-dir settings init-name)))

(defn iplant-rpm
  "Generates the type of RPM that is used by the iPlant Collaborative to
   distribute web services written in Clojure."
  [project]
  (try
    (do (build-rpm project) 0)
    (catch Exception e
      (warn (.getMessage e))
      1)))
