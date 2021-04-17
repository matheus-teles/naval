(ns naval.game
  ; :use não é muito comum, geralmente se use require com um alias, pra evitar conflito de nomes
  (:require [naval.cli :as cli]
            [naval.board :as board]))

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
  (let [player-move (cli/shoot! (:board player-2))]
    (if (board/end-game? player-move)
      (println "Fim de jogo")
      (recur (assoc player-2 :board player-move) player-1))))

; {:player "Player Name" :board []}
(defn start!
  []
  (let [player-1 {:player
                  (cli/get-player-name! "1")
                  :board
                  (reduce cli/place-boat! start-board boats)}
        player-2 {:player
                  (cli/get-player-name! "2")
                  :board
                  (reduce cli/place-boat! start-board boats)}]
    (turn-phase player-1 player-2)))