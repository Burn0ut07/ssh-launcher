(defproject ssh-launcher "1.0.0-SNAPSHOT"
  :description "Easy launching of ssh shells"
  :dependencies [[clojure "1.2.0-master-SNAPSHOT"]
                 [clojure-contrib "1.2.0-SNAPSHOT"]
		 [clojure-protobuf "0.2.4"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [clojure-protobuf "0.2.4"]]
  :tasks [protobuf.tasks]
  :aot [ssh-launcher.core]
  :main ssh-launcher.core)