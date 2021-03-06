(defsystem "holon"
  :description "a conceptual context"
  :version "1.0"
  :author "Collin Bell <collinalexbelL@gmail.com>"
  :license "Public Domain"
  :depends-on ("parachute"
               "postmodern"
               "cl-cffi-gtk")
  :components ((:file "holon")
               (:file "love")
               (:file "focus")
               (:file "goals")
               (:file "restrictions")
               (:file "task/init")
               (:file "human")
               (:file "i")
               (:file "youtube")
               (:file "mastery")
               (:file "pomodoro")
               (:file "gui")
               (:file "purpose")
               (:file "behavioural-exceptions")))
