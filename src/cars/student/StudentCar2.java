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

public class StudentCar2 extends Car {
    public StudentCar2() {
        super(s -> s
                .color(Color.RED)
                .randomOrientation());
    }

    // por padrão o carro fica parado; só se move em flee ou wander
    private boolean useWander = false;

    // Wander state (mesmo algoritmo do 1 para consistência visual)
    private Vector2 wanderTarget = Vector2.byAngle(0);
    private final double wanderRadius   = 50.0;
    private final double wanderDistance = 100.0;
    private final double wanderJitter   = 10.0;

    @Override
    public Vector2 calculateSteering(final World world) {
        // 1) Flee do mouse (zona de pânico)
        final Vector2 mouse = world.getMousePos();
        if (mouse != null) {
            double dist = Vector2.distance(getPosition(), mouse);
            double panic = 250.0;
            if (dist < panic) {
                // Avoidance vai aqui

                return flee(mouse);
            }
        }
        if (useWander) {
            return wander();
        }

        return vec2();
    }

    public void toggleWander() { useWander = !useWander; }

    // ----------------- Behaviors -----------------
    private Vector2 flee(Vector2 target) {
        Vector2 desired = Vector2.subtract(getPosition(), target).normalize().multiply(getMaxSpeed());
        Vector2 steering = Vector2.subtract(desired, getVelocity());
        return Vector2.truncate(steering, getMaxForce());
    }

    private Vector2 wander(){
        double jx = (Math.random() * 2 - 1) * wanderJitter;
        double jy = (Math.random() * 2 - 1) * wanderJitter;
        wanderTarget = wanderTarget.add(Vector2.vec2(jx, jy));

        wanderTarget = wanderTarget.normalize().multiply(wanderRadius);

        Vector2 aheadLocal = getDirection().multiply(wanderDistance);
        Vector2 targetWorld = getPosition().add(aheadLocal).add(wanderTarget);

        return seek(targetWorld);
    }

    private Vector2 seek(Vector2 target) {
        Vector2 desired = Vector2.subtract(target, getPosition()).normalize().multiply(getMaxSpeed());
        Vector2 steering = Vector2.subtract(desired, getVelocity());
        return Vector2.truncate(steering, getMaxForce());
    }

}
