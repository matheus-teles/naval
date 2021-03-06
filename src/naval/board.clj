(ns naval.board)

(defn place-boat-tile
  "Receive point and mark as boat in board"
  [board tile]
  (update-in board tile assoc :boat true))

(defn hit-tile
  "Receive point and mark as boat in board"
  [board tile]
  (update-in board tile assoc :hit true))

(defn place-boat
  "Receive a boat and a board and place it"
  [board boat]
  (reduce place-boat-tile board boat))

(defn has-boat?
  [tile]
  (contains? tile :boat))

(defn has-hit?
  [tile]
  (contains? tile :hit))

(defn has-healthy-boat?
  [tile]
  (and
    (has-boat? tile)
    (not (has-hit? tile))))

(defn get-tile
  [board tile]
  (get-in board tile))

(defn can-place-boat?
  "Return true if there is not a single boat in boat position"
  [board boat]
  (let [get-boat-tiles (partial get-tile board)]
    (->> boat
         (map get-boat-tiles)
         (some has-boat?)
         not)))

(defn end-game?
  [board]
  (->> board
       flatten
       (some has-healthy-boat?)
       not))



