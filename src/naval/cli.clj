(ns naval.cli
  (:use clojure.string)
  (:use naval.utils)
  (:use naval.board))

(defn get-player-name
  "Wait for the user to input their name"
  [player]
  (println "Digite aqui o nome do jogador" player)
  (let [input (trim (read-line))]
    (if (empty? input)
      (get-player-name player)
      input)))

(defn valid-boat-position?
  "Validate if by both positions the boat given can be placed"
  [[initial-x initial-y] [final-x final-y] boat]
  (let [boat-size (:size boat)
        horizontal-diff (abs (- initial-x final-x))
        vertical-diff (abs (- initial-y final-y))]
    (and
      (xor (= (inc horizontal-diff) boat-size) (= (inc vertical-diff) boat-size))
      (xor (= horizontal-diff 0) (= vertical-diff 0)))))

(defn get-position
  "Wait for the user to input a position"
  [message]
  (println message)
  (let [input (re-find #"(\d)\s(\d)" (trim (read-line)))]
    (if input
      (->> input
           rest
           (map #(Long/parseLong %)))
      (do
        (println "Posição inválida")
        (get-position message)))))

; - -> Water
; O -> Miss
; X -> Hit

(defn translate-tile-enemy
  "Translate a tile to show for the enemy"
  [tile]
  (cond
    (and (:boat tile) (:hit tile))  "X"
    (:hit tile)                     "O"
    :else                           "-"))

; # -> Boat
; - -> Water

(defn translate-tile-self
  "Translate tile to show for the players themselves"
  [tile]
  (cond
    (:boat tile)  "#"
    :else         "-"))

(defn print-board
  ([board] (print-board board translate-tile-enemy))
  ([board translation]
  (let [translate-row #(map translation %)]
    (println (->> board
                  (map (comp (partial join " ") translate-row))
                  (join "\n"))))))

(defn place-boat-cli
  [board boat]
  (print-board board translate-tile-self)
  (println "Posicione o barco de tamanho" (:size boat))
  (let [initial-position (get-position "Digite a posição inicial do barco da seguinte forma: x y")
        final-position (get-position "Agora a posição final")]
    (cond
      (not (valid-boat-position? initial-position final-position boat)) (place-boat-cli board boat)
      (can-place-boat? board (expand-initial-final initial-position final-position)) (place-boat board (expand-initial-final initial-position final-position))
      :else (place-boat-cli board boat))))

(defn shoot
  [board]
  (print-board board)
  (let [position (get-position "Digite a posição que você quer acertar")]
    (if (has-hit? (get-tile board position))
      (do
        (println "Já foi efetuado um diaparo para esse ponto")
        (shoot board))
      (hit-tile board position))))
