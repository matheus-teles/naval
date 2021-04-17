(ns naval.cli
  (:require [clojure.string :as string]
            [naval.utils :as utils]
            [naval.board :as board]))

(defn get-player-name!
  "Wait for the user to input their name"
  [player]
  (println "Digite aqui o nome do jogador" player)
  (let [input (string/trim (read-line))]
    (if (empty? input)
      (get-player-name! player)
      input)))

(defn valid-boat-position?
  "Validate if by both positions the boat given can be placed"
  [[initial-x initial-y] [final-x final-y] boat]
  (let [boat-size (:size boat)
        horizontal-diff (utils/abs (- initial-x final-x))
        vertical-diff (utils/abs (- initial-y final-y))]
    (and
      (utils/xor (= (inc horizontal-diff) boat-size) (= (inc vertical-diff) boat-size))
      (utils/xor (= horizontal-diff 0) (= vertical-diff 0)))))

(defn get-position!
  "Wait for the user to input a position"
  [message]
  (println message)
  (let [input (re-find #"(\d)\s(\d)" (string/trim (read-line)))]
    (if input
      (->> input
           rest
           (map #(Long/parseLong %)))
      (do
        (println "Posição inválida")
        (get-position! message)))))

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

(defn print-board!
  ([board] (print-board! board translate-tile-enemy))
  ([board translation]
  (let [translate-row #(map translation %)]
    (println (->> board
                  (map (comp (partial string/join " ") translate-row))
                  (string/join "\n"))))))

(defn place-boat!
  [board boat]
  (print-board! board translate-tile-self)
  (println "Posicione o barco de tamanho" (:size boat))
  (let [initial-position (get-position! "Digite a posição inicial do barco da seguinte forma: x y")
        final-position (get-position! "Agora a posição final")
        boat-tiles (utils/expand-initial-final initial-position final-position)]
    (if (and (valid-boat-position? initial-position final-position boat)
             (board/can-place-boat? board boat-tiles)) 
      (board/place-boat board boat-tiles)
      (place-boat! board boat))))

(defn shoot!
  [board]
  (print-board! board)
  (let [position (get-position! "Digite a posição que você quer acertar")]
    (if (board/has-hit? (board/get-tile board position))
      (do
        (println "Já foi efetuado um diaparo para esse ponto")
        (shoot! board))
      (board/hit-tile board position))))
