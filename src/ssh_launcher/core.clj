(ns ssh-launcher.core
  (:gen-class)
  (:use (clojure.contrib [shell-out :only (sh)])
	(clojure.contrib [io :only (file-str read-lines)])))

(def hosts (atom {})); data structure to hold ssh logins

(defn shlex
  "Auxilary for sh which takes command string and splits it"
  [cmd-str]
  (apply sh (-> cmd-str (.split " ") seq)))

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
  (let [host (@hosts name), login (first host)]
    (println "Logging into" login "...")
    (if-let [opts (fnext host)]
      (let [xterm (str "xterm " opts)]
	(shlex (str xterm " -e ssh " login)))
      (let [cmd (str "xterm -e ssh " login)]
	(shlex cmd)))))
	   
(defn printhosts
  "Prints all the hosts that were loaded from config file"
  []
  (let [user (.trim (sh "whoami")), host (.trim (sh "hostname"))]
    (println
     (format "You may launch shells for following systems:\n\tlocal: %s@%s"
	     user host))
    (doseq [x (for [ks (keys @hosts)]
		(format "\t%s: %s" ks (first (@hosts ks))))] (println x))))

(defn make-server-map
  "Make the map with all the server name pairs from a sequence of config lines"
  [f-seq]
  (if-let [lines (remove (partial re-find #"^#.*") f-seq)]
    (let [parsed (map #(seq (.split % ",")) lines)]
      (last (map #(swap! hosts assoc (first %) (next %)) parsed)))
    (do
      (println "Improper conf file")
      (System/exit -1))))

(defn bind-smap
  "Binds server-map to atom"
  []
  (let [conf-file (file-str "~/Desktop/SSH-Launcher/launcher-ssh.conf")
	smap (make-server-map (read-lines conf-file))]
    (swap! hosts into smap)))

(defn do-cmd
  "Runs command issued from user or prints error if command is unknown"
  [cmd]
  (cond
   (= cmd "help") (help)
   (= cmd "exit") (do (println "Exiting SSH-Launcher Shell..") (System/exit 0))
   (= cmd "hosts") (printhosts)
   (= cmd "clear") (sh "clear")
   (= cmd "restart") (bind-smap)
   (= cmd "local") (shlex "xterm &")
   (contains? @hosts cmd) (launch-shell cmd)
   :else (println "Could not recognize command:" cmd)))

(defn -main [& args]
  (let [user (.trim (sh "whoami")), stdin (java.util.Scanner. System/in)]
    (bind-smap)
    (print (str "Starting SSH-Launcher Shell\n[ssh-launcher: " user "]-- "))
    (.flush *out*)
    (loop [input (.trim (.nextLine stdin))]
      (do-cmd input)
      (print (str "[ssh-launcher: " user "]-- "))
      (.flush *out*)
      (recur (.trim (.nextLine stdin))))))

(-main)