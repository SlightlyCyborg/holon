(defclass todo ()
  ((description :accessor todo-description
                :initarg :description)
   (priority :accessor todo-priority
             :initarg :priority
             :initform 0)
   (groups :accessor todo-groups
           :initarg :groups
           :initform 0)
   (selected-duration :accessor todo-selected-duration
                      :initarg :selected-duration
                      :initform 0)
   (last-selected-time :accessor last-selected-time)
   (parent :accessor todo-parent
           :initarg :parent
           :initform nil)))


(defun accumulate-work-time (the-todo)
  (let ((time-diff (- (get-universal-time) (last-selected-time the-todo))))
    (setf (todo-selected-duration the-todo)
          (+ (todo-selected-duration the-todo) time-diff))
    (if (not (null (todo-parent the-todo)))
        (setf (todo-selected-duration (todo-parent the-todo))
              (+ (todo-selected-duration
                  (todo-parent the-todo))
                 time-diff)))

    (values (todo-selected-duration the-todo) time-diff)))


(defun symbol-exists-p (sym)
  (fboundp sym))


(defun todo->a-symbol-name (description)
  (reduce
   #'(lambda (str1 str2)
       (concatenate 'string str1 str2))
   (let ((x 0))
     (mapcar
      #'(lambda (to-be-sym)
          (if (= 0 x)
              (progn
                (incf x)
                (if (not (integerp to-be-sym))
                    (symbol-name to-be-sym)
                    (write-to-string to-be-sym)))
              (progn
                (incf x)
                (concatenate 'string "_" (if (not (integerp to-be-sym))
                                             (symbol-name to-be-sym)
                                             (write-to-string to-be-sym))))))
      description))))


(defun make-todo-action-hook-symbol (action description)
  (intern (concatenate 'string "HOOK-" (string action)
                        "-"
                        (todo->a-symbol-name description))))



(defun todo-complete (the-todo)
  (let ((the-hook-sym
         (make-todo-action-hook-symbol 'complete
                                       (todo-description the-todo))))
    (if (symbol-exists-p the-hook-sym)
     (funcall (symbol-function the-hook-sym) the-todo))))

(defun todo-select (the-todo)
  (let ((the-hook-sym
          (make-todo-action-hook-symbol 'select
                                        (todo-description the-todo))))
    (if (symbol-exists-p the-hook-sym)
     (funcall (symbol-function the-hook-sym) the-todo))))

(defun add-todos (&optional (todos '()))
  ;;todos is a list of todos
  (loop for todo in todos
   do (add-todo todo)))

(defun say-selected-todo ()
  (let ((words (format nil "~a" (todo-description *selected-todo*))))
   (progn words nil)))
