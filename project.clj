(defproject dameon "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :injections [(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [alembic "0.3.2"]
                 [quil "2.4.0"]
                 [org.clojure/core.async "0.2.385"]
                 [opencv/opencv "3.1.0"] ; added line
                 [opencv/opencv-native "3.1.0"]])
