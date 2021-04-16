(ns naval.utils)

(defmacro xor
  ([] nil)
  ([a] a)
  ([a b]
   `(let [a# ~a
          b# ~b]
      (if a#
        (if b# nil a#)
        (if b# b# nil))))
  ([a b & more]
   `(xor (xor ~a ~b) (xor ~@more))))

(defn abs [n] (max n (- n)))

(defn expand-initial-final
  [[initial-x initial-y] [final-x final-y]]
  (for [x (vec (range (min initial-x final-x) (inc (max initial-x final-x))))
        y (vec (range (min initial-y final-y) (inc (max initial-y final-y))))]
    [x y]))
