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

    // --- Flags de modo ---
    private boolean useWander = false;
    private boolean useFollowPath = false;

    // --- Path following ---
    private final List<Vector2> path = new ArrayList<>();
    private int currentPathIndex = 0;
    private final double waypointRadius = 30; // distância para “considerar chegou”

    // --- Wander state ---
    private Vector2 wanderTarget = Vector2.byAngle(0);
    private final double wanderRadius   = 50.0;
    private final double wanderDistance = 100.0;
    private final double wanderJitter   = 10.0;

    @Override
    public Vector2 calculateSteering(final World world) {
        // 1) Follow path (se ligado) mantém prioridade, como estava
        if (useFollowPath) {
            if (path.isEmpty()) {
                final int nPoints = 4;
                for (int i = 0; i < nPoints; i++) {
                    // usa dimensões do mundo em vez de números mágicos
                    double x = Math.random() * world.getWidth()  - world.getWidth()  / 2.0;
                    double y = Math.random() * world.getHeight() - world.getHeight() / 2.0;
                    path.add(new Vector2(x, y));
                }
            }
            return followPath();
        }

        // 2) Clique tem prioridade sobre wander
        final Vector2 click = world.getClickPos();
        if (click != null) {
            final double dist = Vector2.distance(getPosition(), click);
            if (dist < 150) return arrive(click);
            return seek(click);
        }

        // 3) Sem clique: opcionalmente vagueia
        if (useWander) {
            return wander();
        }

        // 4) Sem alvo: não aplica força
        return vec2();
    }

    // Atalhos para Window
    public void toggleWander()    { useWander = !useWander; }
    public void toggleFollowPath(){ useFollowPath = !useFollowPath; }

    // ----------------- Behaviors -----------------
    private Vector2 seek(Vector2 target) {
        Vector2 desired = Vector2.subtract(target, getPosition()).normalize().multiply(getMaxSpeed());
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

    private Vector2 wander(){
        // jitter aleatório
        double jx = (Math.random() * 2 - 1) * wanderJitter;
        double jy = (Math.random() * 2 - 1) * wanderJitter;
        wanderTarget = wanderTarget.add(Vector2.vec2(jx, jy));

        // mantém no círculo
        wanderTarget = wanderTarget.normalize().multiply(wanderRadius);

        // leva o círculo à frente do carro
        Vector2 aheadLocal = getDirection().multiply(wanderDistance);
        Vector2 targetWorld = getPosition().add(aheadLocal).add(wanderTarget);

        return seek(targetWorld);
    }

    private void maybeInitRandomPath(World world, int nPoints) {
        if (!path.isEmpty()) return;
        // use a janela atual para sortear dentro da área visível
        double hw = world.getWidth() / 2.0;
        double hh = world.getHeight() / 2.0;
        for (int i = 0; i < nPoints; i++) {
            path.add(new Vector2(
                    (Math.random() * (hw * 1.6)) - hw * 0.8,  // um pouco além do meio
                    (Math.random() * (hh * 1.6)) - hh * 0.8
            ));
        }
        currentPathIndex = 0;
    }

    private Vector2 followPath() {
        if (path.isEmpty()) return vec2();

        Vector2 wp = path.get(currentPathIndex);

        // troca de waypoint quando chega perto
        if (Vector2.distance(getPosition(), wp) < waypointRadius) {
            currentPathIndex = (currentPathIndex + 1) % path.size();
            wp = path.get(currentPathIndex);
        }

        return seek(wp);
    }
}