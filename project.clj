(defproject ssh-launcher "1.0.0-SNAPSHOT"
  :description "Easy launching of ssh shells"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :aot [ssh-launcher.core]
  :main ssh-launcher.core)