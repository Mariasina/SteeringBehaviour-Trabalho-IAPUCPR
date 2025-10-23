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
    @Override
    public Vector2 calculateSteering(final World world) {
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

        return seek(target);
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

}
