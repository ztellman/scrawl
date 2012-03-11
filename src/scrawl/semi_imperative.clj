(ns scrawl.semi-imperative
  (:use
    [scrawl window])
  (:require
    [scrawl.imperative :as i]))

;;;

(def triangle-renderer i/draw-triangle)

(defn render [renderer]
  (renderer))

;;;

(defn scale [f x y]
  #(i/with-scoped-transforms
     (i/scale x y)
     (f)))

(defn translate [f x y]
  #(i/with-scoped-transforms
     (i/translate x y)
     (f)))

(defn rotate [f degrees]
  #(i/with-scoped-transforms
     (i/rotate degrees)
     (f)))

(defn color [f color]
  #(i/with-color color
     (f)))

;;;

(def offsets
  [[-1 -1]
   [1  -1]
   [0   1]])

(defn sierpinski
  [f]
  (let [transform (fn [[x y]]
                    (-> f
                      (translate x y)
                      (scale 0.5 0.5)))
        [a b c] (map transform offsets)]
    (fn []
      (a)
      (b)
      (c))))

;; an infinite sequence!
(def sierpinskis (iterate sierpinski triangle-renderer))

(comment
  (update-window #(render (nth sierpinskis 6))))
