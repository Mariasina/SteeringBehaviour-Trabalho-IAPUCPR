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

    private boolean useWander = false;
    private boolean useFollowPath = false;

    private final List<Vector2> path = new ArrayList<>();
    private int currentPathIndex = 0;

    @Override
    public Vector2 calculateSteering(final World world) {
        if (useFollowPath) {
            if (path.isEmpty()) {
                for (int i = 0; i < 4; i++) {
                    path.add(new Vector2(Math.random() * 800, Math.random() * 600));
                }
            }
            return followPath();
        }

        if (useWander) {
            return wander();
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


    private Vector2 followPath() {
        if (currentPathIndex >= path.size()) currentPathIndex = 0;

        Vector2 waypoint = path.get(currentPathIndex);

        Vector2 desired = Vector2
                .subtract(waypoint, getPosition())
                .normalize()
                .multiply(getMaxSpeed());
        Vector2 steering = Vector2.subtract(desired, getVelocity());

        currentPathIndex++;

        return Vector2.truncate(steering, getMaxForce());
    }
}