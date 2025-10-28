package cars.student;

import cars.engine.Car;
import cars.engine.Vector2;
import cars.engine.World;

import java.awt.*;

import static cars.engine.Vector2.vec2;

public class StudentCar extends Car {
    public StudentCar() {
        super(settings ->
          settings
            .color(Color.BLUE)
            .randomOrientation()
        );

    }

    /**
     * Deve calcular o steering behavior para esse carro
     * O parametro world contem diversos metodos utilitários:
     * world.getClickPos(): Retorna um vector2D com a posição do último click,
     * ou nulo se nenhum click foi dado ainda
     * - world.getMousePos(): Retorna um vector2D com a posição do cursor do mouse
     * - world.getNeighbors(): Retorna os carros vizinhos. Não inclui o próprio carro.
     * Opcionalmente, você pode passar o raio da vizinhança. Se o raio não for
     * fornecido retornará os demais carros.
     * - world.getSecs(): Indica quantos segundos transcorreram desde o último quadro
     * Você ainda poderá chamar os seguintes metodos do carro para obter informações:
     * - getDirection(): Retorna um vetor unitário com a direção do veículo
     * - getPosition(): Retorna um vetor com a posição do carro
     * - getMass(): Retorna a massa do carro
     * - getMaxSpeed(): Retorna a velocidade de deslocamento maxima do carro em píxeis / s
     * - getMaxForce(): Retorna a forca maxima que pode ser aplicada sobre o carro
     */

    private boolean useWander = false; // false por padrão, modo normal

    @Override
    public Vector2 calculateSteering(final World world) {
        if (useWander) {
            return wander();
        }

        Vector2 target = world.getClickPos();
        if (target == null) return Vector2.vec2();

        Vector2 mouse = world.getMousePos();
        Vector2 position = getPosition();

        if (mouse != null) {
            double distanceMouse = Vector2.distance(position, mouse);
            double panic = 250.0;
            if (distanceMouse < panic) {
                return flee(mouse);
            }
        }

        double distanceToTarget = Vector2.distance(position, target);

        if (distanceToTarget < 150) {
            return arrive(target);
        } else {
            return seek(target);
        }
    }

    public void toggleWander() {
        useWander = !useWander;
    }


    private Vector2 seek(Vector2 target) {
        Vector2 desired = Vector2
                .subtract(target, getPosition())
                .normalize()
                .multiply(getMaxSpeed());

        Vector2 steering = Vector2
                .subtract(desired, getVelocity());

        return Vector2.truncate(steering, getMaxForce());
    }

    private Vector2 flee(Vector2 target) {
        Vector2 desired = Vector2
                .subtract(getPosition(), target)
                .normalize()
                .multiply(getMaxSpeed());

        Vector2 steering = Vector2
                .subtract(desired, getVelocity());

        return Vector2.truncate(steering, getMaxForce());
    }

    private Vector2 arrive(Vector2 target) {
        Vector2 toTarget = Vector2.subtract(target, getPosition());
        double distance = toTarget.size();

        double stopRadius = 20;       // distância para considerar "parado"
        double slowingRadius = 100;   // distância para começar a desacelerar

        // Se estiver dentro do stopRadius e velocidade baixa, zera movimento e força
        if (distance < stopRadius && getVelocity().size() < 5) {
            // Zera velocidade do carro para evitar oscilar
            // (Aqui você pode implementar setVelocity zero se o código permitir)
            return Vector2.vec2();
        }

        double desiredSpeed;
        if (distance < slowingRadius) {
            desiredSpeed = getMaxSpeed() * (distance / slowingRadius);
        } else {
            desiredSpeed = getMaxSpeed();
        }

        Vector2 desiredVelocity = toTarget.normalize().multiply(desiredSpeed);
        Vector2 steering = Vector2.subtract(desiredVelocity, getVelocity());

        return Vector2.truncate(steering, getMaxForce());
    }

    private Vector2 wanderTarget = Vector2.byAngle(0);

    private double wanderRadius = 50.0;    // raio do circulo para escolher a direção
    private double wanderDistance = 100.0; // distância do circulo para frente do carro
    private double wanterJitter = 10.0;    // quando pode mudar a diração do circulo por frame

    private Vector2 wander(){
        // adiciona um pequeno deslocamento aleatório (*jitter*) à direção atual
        double jitterX = (Math.random() * 2 - 1) * wanterJitter;
        double jitterY = (Math.random() * 2 - 1) * wanterJitter;
        wanderTarget = wanderTarget.add(Vector2.vec2(jitterX, jitterY));

        // normaliza para ficar dentro do circulo de raio 1 e multiplica para manter tamanho
        wanderTarget = wanderTarget.normalize().multiply(wanderRadius);

        // posiciona o círculo na frente do carro
        Vector2 targetLocal = wanderTarget.add(getDirection().multiply(wanderDistance));

        // calcula o ponto de destino absoluto no mundo
        Vector2 targetWorld = targetLocal.add(getPosition());

        // retorna o steering force para buscar esse ponto
        return seek(targetWorld);

    }
}
