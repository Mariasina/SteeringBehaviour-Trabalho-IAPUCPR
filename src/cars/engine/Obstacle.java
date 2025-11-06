package cars.engine;

import java.awt.*;
import java.awt.geom.*;
import java.util.function.Consumer;

import static cars.engine.Vector2.*;
import static java.lang.Math.toRadians;

public class Obstacle implements Cloneable {

    private final double mass;
    private Vector2 position;

    public Obstacle(Consumer<Settings> settings) {
        var cs = new Settings();
        settings.accept(cs);

        this.position = cs.position;
        this.mass = cs.mass;

    }

    public Vector2 getPosition() { return position.clone(); }

    public void setPosition(Vector2 position) {this.position = position;}

    public double getMass() { return mass; }

    void draw(Graphics2D g, boolean debug) {
        final var g2 = (Graphics2D) g.create();
        g2.translate(position.x, position.y);
        g2.scale(-0.5, 0.5);

        final var L = 140.0;
        final var W = 110.0;
        final var halfL = L / 2.0;
        final var halfW = W / 2.0;

        g2.setPaint(new Color(25, 25, 32, 230));
        g2.fill(new Ellipse2D.Double(-L * 0.12, -W * 0.18, L * 0.28, W * 0.36));
    }

    public void randomizePosition(double width, double height) {
        double w = width / 2.0;
        double h = height / 2.0;

        double x = (Math.random() * 2 * w) - w;
        double y = (Math.random() * 2 * h) - h;

        this.position = Vector2.vec2(x, y);
    }



}
