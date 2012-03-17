(ns scrawl.functional
  (:use
    [scrawl.window])
  (:require
    [scrawl.imperative :as i])
  (:import
    [java.awt.geom
     AffineTransform]))

(set! *warn-on-reflection* true)

;;;

(defn transformation []
  (AffineTransform.))

;; this is *not* a very robust defn macro, since it doesn't support
;; doc-strings or function metadata.  This is okay if you're the only
;; consumer of the macro, but not if you want other people to use it
(defmacro defn-transform [name args affine-transform]
  `(defn ~name ~args
     (let [^AffineTransform original-transformation# ~(first args)
           transformation# (AffineTransform. original-transformation#)]
       (.concatenate transformation# ~affine-transform)
       transformation#)))

(defn-transform scale [transformation x y]
  (AffineTransform/getScaleInstance x y))

(defn-transform translate [transformation x y]
  (AffineTransform/getTranslateInstance x y))

(defn-transform rotate [transformation degrees]
  (AffineTransform/getRotateInstance (Math/toRadians degrees)))

;;;

(defprotocol Shape
  (transform [_ transformation])
  (render [_]))

(defrecord Polygon [^java.awt.Polygon polygon color]
  Shape
  (render [_]
    (i/with-color color
      (.fill *graphics* polygon)))
  (transform [this transformation]
    (assoc this
      :polygon (.createTransformedShape ^AffineTransform transformation polygon))))

(defn triangle
  ([]
     (triangle :firebrick))
  ([color]
     (Polygon.
       (java.awt.Polygon.
          (int-array [-1 0 1])
          (int-array [-1 1 -1])
          3)
       color)))

(defn render-all [shapes]
  (doseq [s shapes]
    (render s)))

;;;

(def offsets
  [[-1 -1]
   [1  -1]
   [0   1]])

(def transformations
  (map
    (fn [[x y]]
      (-> (transformation) (scale 0.5 0.5) (translate x y)))
    offsets))

(defn sierpinski
  "Takes a list of shapes, and returns a list of three times as many transformed polygons."
  [shapes]
  (mapcat
    (fn [transformation]
      (map #(transform % transformation) shapes))
    transformations))

;; an infinite sequence, again!
(def sierpinskis (iterate sierpinski [(triangle)]))

(comment
  (update-window #(render-all (nth sierpinskis 6))))


