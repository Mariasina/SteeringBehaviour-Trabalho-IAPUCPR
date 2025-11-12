package cars.student;

import cars.engine.Car;
import cars.engine.Vector2;
import cars.engine.World;
import jdk.jshell.spi.ExecutionControl;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.List;

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
    private boolean useFollowPath = false;
    private List<Vector2> pathFollowTargets = new ArrayList<>();
    private int currentPathIndex = 0;


    @Override
    public Vector2 calculateSteering(final World world) {
        if (useFollowPath) {
            if (pathFollowTargets.isEmpty()) {
                for (int i = 0; i < 4; i++) {
                    pathFollowTargets.add(new Vector2(100 * i, 100 * i));
                }
            }
            return followPath();
        }

        if (useWander) {
            return wander();
        }

        Vector2 avoidance = obstacleAvoidance(world);
        if (avoidance.size() > 0.001) {
            return avoidance;
        }


        final Vector2 target = world.getClickPos();
        if (target == null) return vec2();

        // Parte aonde iria o avoidable obstacle no método calculateSteering ;)

        final double dist = Vector2.distance(getPosition(), target);
        if (dist < 150) return arrive(target);
        return seek(target);
    }

    public void toggleWander() { useWander = !useWander; }
    public void toggleFollowPath() { useFollowPath = !useFollowPath; }


    private Vector2 seek(Vector2 target) {
        Vector2 desired = Vector2
                .subtract(target, getPosition())
                .normalize()
                .multiply(getMaxSpeed());
        Vector2 steering = Vector2.subtract(desired, getVelocity());
        return Vector2.truncate(steering, getMaxForce());
    }

    private Vector2 arrive(Vector2 target) {
        Vector2 toTarget = Vector2.subtract(target, getPosition());
        double distance = toTarget.size();

        double stopRadius = 20;
        double slowingRadius = 100;

        if (distance < stopRadius && getVelocity().size() < 5) {
            return vec2();
        }

        double desiredSpeed = (distance < slowingRadius)
                ? getMaxSpeed() * (distance / slowingRadius)
                : getMaxSpeed();

        Vector2 desiredVelocity = toTarget.normalize().multiply(desiredSpeed);
        Vector2 steering = Vector2.subtract(desiredVelocity, getVelocity());
        return Vector2.truncate(steering, getMaxForce());
    }

    // Wander state
    private Vector2 wanderTarget = Vector2.byAngle(0);
    private double wanderRadius = 50.0;
    private double wanderDistance = 100.0;
    private double wanderJitter = 10.0;

    private Vector2 wander() {
        double jitterX = (Math.random() * 2 - 1) * wanderJitter;
        double jitterY = (Math.random() * 2 - 1) * wanderJitter;
        wanderTarget = wanderTarget.add(Vector2.vec2(jitterX, jitterY))
                .normalize()
                .multiply(wanderRadius);

        Vector2 targetLocal = wanderTarget.add(getDirection().multiply(wanderDistance));
        Vector2 targetWorld = targetLocal.add(getPosition());
        return seek(targetWorld);
    }

    private Vector2 obstacleAvoidance(World world) {
        List<Obstacle> obstacles = world.getObstacles();
        if (obstacles == null || obstacles.isEmpty()) return Vector2.vec2();

        double detectionLength = 100;
        double detectionWidth = 80;

        Vector2 carPosition = getPosition();
        Vector2 carDirection = getDirection();
        Obstacle closestObstacle = null;
        double closestDist = Double.MAX_VALUE;

        for (Obstacle obstacle : obstacles) {
            Vector2 toObstacle = Vector2.subtract(obstacle.getPosition(), carPosition);

            double forwardDist = carDirection.dot(toObstacle);
            double sideDist = toObstacle.x * carDirection.y - toObstacle.y * carDirection.x;

            if (forwardDist > 0 && forwardDist < detectionLength && Math.abs(sideDist) < detectionWidth / 2) {
                if (forwardDist < closestDist) {
                    closestDist = forwardDist;
                    closestObstacle = obstacle;
                }
            }
        }

        if (closestObstacle == null) return Vector2.vec2();

        Vector2 toClosest = Vector2.subtract(closestObstacle.getPosition(), carPosition);
        double side = toClosest.x * carDirection.y - toClosest.y * carDirection.x;

        double sideSign = (side > 0) ? 1.0 : -1.0;
        double lateralOffset = 50.0 * sideSign;

        Vector2 right = Vector2.vec2(-carDirection.y, carDirection.x);
        Vector2 avoidTarget = carPosition.add(right.multiply(lateralOffset));

        Vector2 desired = Vector2
                .subtract(avoidTarget, carPosition)
                .normalize()
                .multiply(getMaxSpeed());

        Vector2 steering = Vector2
                .subtract(desired, getVelocity());

        return Vector2.truncate(steering, getMaxForce());
    }



    private Vector2 followPath() {
        Vector2 carPosition = getPosition();
        Vector2 currentTarget = pathFollowTargets.get(currentPathIndex);
        double distanceToTarget = Vector2.distance(carPosition, currentTarget);
        double targetRadius = 15.0;

        if (distanceToTarget < targetRadius) {
            currentPathIndex++;

            if (currentPathIndex >= pathFollowTargets.size()) {
                currentPathIndex = 0;
            }
        }

        currentTarget = pathFollowTargets.get(currentPathIndex);
        System.out.println(currentPathIndex + ": " + currentTarget);
        System.out.println(carPosition);

        Vector2 desired = Vector2
                .subtract(currentTarget, getPosition())
                .normalize()
                .multiply(getMaxSpeed());

        Vector2 steering = Vector2
                .subtract(desired, getVelocity());

        return Vector2.truncate(steering, getMaxForce());
    }

}
