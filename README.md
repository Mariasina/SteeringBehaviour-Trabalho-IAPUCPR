# SteeringBehaviour-Trabalho-IAPUCPR

Equipe: Caio Roque, Felipe Charello e Maria Carolina

### Projeto que implementa comportamentos clássicos de Steering Behaviour em uma simulação de carros de corrida simples. O objetivo foi implementar os comportamentos mais conhecidos, sendo eles seek, arrive, flee, wander, obstacle avoidance e path following. 

## Principais funcionalidades
### Para o projeto foram criados dois carros com combinações de comportamentos diferentes: 
1. StudentCar: seek, arrive, wander e obstacle avoidance (obs: obstacle avoidance mas não está ativado na simulação pois não está funcionando 100%). 
2. StudentCar2: flee, wander e seek. 

## Comportamentos

### Seek
No comportamento *seek*, é recebido um alvo, que nesse caso é a última posição em que o mouse clicou. A partir disso, é calculado um vetor de movimentação que vai da posição atual do carro até a posição do clique, apontando na direção do alvo e tentando atingir a velocidade máxima nessa direção.

### Arrive
No comportamento *arrive*, o carro também busca um alvo, assim como no *seek*, mas reduz gradualmente sua velocidade conforme se aproxima do destino. É calculada a distância até o alvo, e, se estiver dentro de uma zona de desaceleração, a velocidade desejada é ajustada proporcionalmente à distância. Dessa forma, o carro chega suavemente e para exatamente no ponto desejado.

### Flee
No comportamento *flee*, o carro faz o oposto do *seek*: em vez de se mover em direção ao alvo, ele se afasta dele. É calculado um vetor na direção contrária ao ponto de perigo (por exemplo, o cursor do mouse), e o carro tenta atingir sua velocidade máxima nessa direção. Esse comportamento é usado quando o carro precisa fugir ao detectar algo próximo, como em uma “zona de pânico”.

### Wander
No comportamento *wander*, o carro se move de forma aparentemente aleatória, mas ainda suave e natural. É projetado um ponto alvo à frente do carro, e esse ponto muda levemente a cada instante através de pequenas variações (jitter). O carro então aplica o *seek* para esse alvo em movimento, resultando em um deslocamento fluido e imprevisível.

### Obstacle Avoidance
No comportamento *obstacle avoidance*, o carro tenta evitar colisões detectando obstáculos à frente dentro de uma área de detecção (como um raio ou retângulo). Para cada obstáculo, é calculada a projeção à frente e a distância lateral. Se o obstáculo estiver dentro da região de detecção, o carro escolhe o mais próximo e gera um vetor de desvio para um ponto lateral, contornando o obstáculo. Nesta implementação, os obstáculos são tratados como pontos simples.

### Follow Path
No comportamento *follow path*, o carro segue uma sequência de pontos (waypoints). Ele aplica o *seek* em direção ao ponto atual, e, ao chegar suficientemente perto (dentro de um pequeno raio), passa a buscar o próximo ponto da lista. Quando chega ao final, o índice retorna ao início, permitindo seguir o caminho continuamente.
