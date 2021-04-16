(ns naval.game
  (:use [naval.cli])
  (:use [naval.board]))

(def boats [{:size 2}
            {:size 3}
            {:size 3}
            {:size 4}
            {:size 5}])

(def start-board (->> {}
                      (repeat 10)
                      vec
                      (repeat 10)
                      vec))

(defn turn-phase
  [player-1 player-2]
  (let [player-move (shoot (:board player-2))]
    (if (end-game? player-move)
      (println "Fim de jogo")
      (recur (assoc player-2 :board player-move) player-1))))

; {:player "Player Name" :board []}
(defn start-game
  []
  (let [player-1 {:player
                  (get-player-name "1")
                  :board
                  (reduce place-boat-cli start-board boats)}
        player-2 {:player
                  (get-player-name "2")
                  :board
                  (reduce place-boat-cli start-board boats)}]
    (turn-phase player-1 player-2)))