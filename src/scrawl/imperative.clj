(ns scrawl.imperative
  (:use
    [scrawl.window])
  (:import
    [java.awt
     Polygon
     Color]))

(set! *warn-on-reflection* true)

(defn draw-triangle
  "Draws an equilateral triangle at the origin"
  []
  (.fillPolygon *graphics*
    (int-array [-1 0 1])
    (int-array [-1 1 -1])
    3))

;;;

(def colors
  {:red       Color/RED
   :black     Color/BLACK
   :green     Color/GREEN
   :blue      Color/BLUE
   :firebrick (Color. 178 34 34)})

(defmacro with-color [color & body]
  `(let [original-color# (.getColor *graphics*)]
     (.setColor *graphics* (colors ~color))
     (try
       ~@body
       (finally
         (.setColor *graphics* original-color#)))))

;;;

(defn rotate [degrees]
  (let [radians (Math/toRadians degrees)]
    (.rotate *graphics* radians)))

(defn translate [x y]
  (.translate *graphics* (int x) (int y)))

(defn scale [x y]
  (.scale *graphics* x y))

(defmacro with-scoped-transforms
  "Any transforms (rotate, translate, scale) within the scope of this macro won't escape."
  [& body]
  `(let [original-transform# (.getTransform *graphics*)]
     (try
       ~@body
       (finally
         (.setTransform *graphics* original-transform#)))))

;;;

(defn overly-complex-example []
  (scale 0.1 0.1)
  (dotimes [_ 8]
    (rotate 45)
    (with-scoped-transforms
      (translate 0 5)
      (rotate 180) ;; what happens if you comment this out? try it and see!
      (draw-triangle))))

(comment
  (update-window overly-complex-example))

;;;

(defmacro sierpinski
  "Repeats 'body' three times, scaling and translating appropriately."
  [& body]
  `(let [body-fn# (fn [] ~@body)]
     (with-scoped-transforms
       (scale 0.5 0.5)
       
       ;; bottom-left
       (with-scoped-transforms
         (translate -1 -1)
         (body-fn#))
       
       ;; bottom-right
       (with-scoped-transforms
         (translate 1 -1)
         (body-fn#))
       
       ;; top
       (with-scoped-transforms
         (translate 0 1)
         (body-fn#)))))

(defmacro sierpinskis
  "Nests 'n'-many sierpinski macros around 'body'."
  [n & body]
  `(-> (do ~@body) ~@(repeat n 'sierpinski)))

(comment
  (update-window #(sierpinskis 6 (draw-triangle))))


