(defproject org.iplantc/lein-iplant-rpm "1.4.3-SNAPSHOT"
  :eval-in-leiningen true
  :description "Leiningen Plugin for generating RPMs for Clojure projects."
  :url "http://www.iplantcollaborative.org"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :scm {:connection "scm:git:git@github.com:iPlantCollaborativeOpenSource/lein-iplant-rpm.git"
        :developerConnection "scm:git:git@github.com:iPlantCollaborativeOpenSource/lein-iplant-rpm.git"
        :url "git@github.com:iPlantCollaborativeOpenSource/lein-iplant-rpm.git"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [fleet "0.9.5"]]
  :repositories {"iplantCollaborative"
                 "http://projects.iplantcollaborative.org/archiva/repository/internal/"}
  :repositories [["sonatype-nexus-snapshots"
                  {:url "https://oss.sonatype.org/content/repositories/snapshots"}]]
  :deploy-repositories [["sonatype-nexus-staging"
                         {:url "https://oss.sonatype.org/service/local/staging/deploy/maven2/"}]])
