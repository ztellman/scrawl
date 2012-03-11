(ns scrawl.window
  (:import
    [java.awt Graphics2D Color]
    [javax.swing JFrame JPanel]))

(set! *warn-on-reflection* true)

;; 1 unit = 200 pixels
(def unit 200)

(defn init-graphics
  [^JPanel panel ^Graphics2D graphics]
  (let [width (.getWidth panel)
        height (.getHeight panel)]

    ;; set the background color to white, and the render color to firebrick
    (.setColor graphics Color/WHITE)
    (.fillRect graphics 0 0 width height)
    (.setColor graphics (Color. 178 34 34)) ;;firebrick

    ;; put the origin in the middle of the window
    (.translate graphics (int (/ width 2)) (int (/ height 2)))

    ;; make 1 unit = n pixels, and make sure the positive y-axis points up
    (.scale graphics unit (- unit))))

(def callback (atom nil))

(def ^:dynamic ^Graphics2D *graphics*)

(defn ^JPanel create-panel []
  (proxy [JPanel] []
    (paint [graphics]
      (init-graphics this graphics)
      (when-let [f @callback]
        (binding [*graphics* graphics]
          (f))))))

(def window
  (delay
    (doto (JFrame. "Scrawl")
      (.add (create-panel))
      (.setDefaultCloseOperation javax.swing.WindowConstants/HIDE_ON_CLOSE)
      (.setSize 800 600))))

(defn update-window
  "Calls the render-fn with *graphics* bound, and brings the window to the foreground."
  [render-fn]
  (reset! callback render-fn)
  (let [^JFrame window @window]
    (.setVisible window true)
    (java.awt.EventQueue/invokeLater
      #(doto window
         (.setAlwaysOnTop true)
         .toFront
         .repaint
         .requestFocus
         (.setAlwaysOnTop false)))))

