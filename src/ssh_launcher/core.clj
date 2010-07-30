(ns ssh-launcher.core
  (:gen-class)
  (:use (clojure.contrib [shell-out :only (sh)])
	(clojure.contrib [duck-streams :only (file-str read-lines)])))

(def & comp)
(def p partial)
(def server-map (atom {}))

(defn help
  "Prints the help info"
  []
  (println "SSH-Launcher Commands:")
  (println "\t<short-name> - Launches a shell to host of <short-name>")
  (println "\tclear - clears the display")
  (println "\texit - exits this program")
  (println "\thelp - prints this message")
  (println "\thosts - prints the list of hosts that can be launched")
  (println "\tlocal - Launches local shell")
  (println "\trestart - reloads the configuration file"))

(defn launch-shell
  "Launches the shell specified by the shortname name"
  [name]
  (println "Launching shell -" name))

(defn make-server-map
  "Make the map with all the server name pairs from a sequence of config lines"
  [f-seq]
  (if-let [lines (remove (p re-find #"^#.*") f-seq)]
    (let [parsed (map #(seq (.split % ",")) lines)]
      (map #(swap! server-map assoc (first %) (next %)) lines))
      (do
	(println "Improper conf file")
	(System/exit -1))))

(defn do-cmd
  "Runs command issued from user or prints error if command is unknown"
  [cmd]
  (cond
   (= cmd "help") (help)
   (= cmd "exit") (System/exit 0)
   (= cmd "clear") (sh "clear")
   (@server-map cmd) (launch-shell cmd)
   :else (println "Could not recognize command:" cmd)))

;(defn -main [& args]
  (let [user (.trim (sh "whoami"))
	conf-file (file-str "~/Desktop/SSH-Launcher/launcher-ssh.conf")
	server-map (make-server-map (read-lines conf-file))
	stdin (java.util.Scanner. System/in)]
;    (sh "clear")
    (print "Starting SSH-Launcher Shell\nssh-launcher:" user "-- ")
    (loop [input (.trim (.nextLine stdin))]
      (do-cmd input)
      (print "ssh-launcher:" user "-- ")
      (recur (.trim (.nextLine stdin)))));)