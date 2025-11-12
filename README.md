# SteeringBehaviour-Trabalho-IAPUCPR

Equipe: Caio Roque, Felipe Charello e Maria Carolina

### Projeto que implementa comportamentos clássicos de Steering Behaviour em uma simulação de carros de corrida simples. O objetivo foi implementar os comportamentos mais conhecidos, sendo eles seek, arrive, flee, wander, obstacle avoidance e path following. 

## Principais funcionalidades
Para o projeto foram criados dois carros com combinações de comportamentos diferentes: 
1. StudentCar: seek, arrive, wander e obstacle avoidance (obs: obstacle avoidance mas não está ativado na simulação pois não está funcionando 100%). 
2. StudentCar2: flee (do cursor), wander e seek. 
Motor físico simples: forças → impulso → aceleração → velocidade → posição.
Visualização em Java2D com opção de debug (vetores desenhados).
Controles via mouse e teclado para interagir com a simulação.
Comportamentos (explicação simples e direta)
Cada descrição contém a ideia, como é calculado (em termos simples) e quando usar.

Seek — ir diretamente para um alvo

Ideia: apontar na direção do alvo e tentar assumir a velocidade máxima nessa direção.
Como funciona (resumido): calcula-se a direção até o alvo, multiplica por maxSpeed → velocidade desejada. Steering = velocidade desejada − velocidade atual. Trunca pelo maxForce.
Uso: clicar na tela para fazer o carro ir até o ponto.
Arrive — chegar desacelerando

Ideia: igual ao seek, mas reduz a velocidade conforme se aproxima para parar suavemente.
Como funciona: calcula distância ao alvo; se estiver dentro de uma "zona de desaceleração" reduz a velocidade desejada proporcionalmente à distância; aplica steering como no seek.
Uso: quando queremos que o carro pare no destino (evita overshoot).
Flee — fugir de um alvo

Ideia: oposto do seek: mover-se para longe de um ponto de perigo (ex.: cursor).
Como funciona: calcula a direção contrária ao perigo, define velocidade desejada = maxSpeed nessa direção, steering = desired − velocity.
Uso: StudentCar2 foge do cursor quando este se aproxima (zona de pânico).
Wander — andar aleatoriamente, mas suave

Ideia: gerar um ponto alvo projetado à frente do carro; esse ponto muda suavemente (jitter) a cada passo para produzir curvas naturais.
Como funciona: mantém um vetor alvo circular que recebe pequenos ruídos; o alvo é projetado à frente por uma distância fixa; aplica-se seek para esse alvo.
Uso: movimento autônomo sem objetivo fixo.
Obstacle avoidance — desviar de obstáculos simples

Ideia: detectar obstáculos à frente num retângulo/raio e escolher um ponto lateral para contornar.
Como funciona: para cada obstáculo calcula-se a projeção à frente (dot) e a distância lateral (cross-like). Se está dentro da região de detecção, escolhe o obstáculo mais próximo e gera um steering para um ponto lateral (offset).
Observação: implementação trata obstáculos como pontos; melhorar consideraria raio/size e predição de colisão.
Follow path — seguir pontos (waypoints)

Ideia: ter uma lista de pontos e sempre ir para o próximo; ao chegar muda para o seguinte.
Como funciona: seek para o waypoint atual; se estiver dentro de um raio pequeno, avança o índice (circular).
