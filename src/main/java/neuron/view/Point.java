package neuron.view;

public class Point {

    public enum Type {
        RED, BLUE;
    }

    private final double x;
    private final double y;
    private final Type type;

    public Point(double x, double y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Type getType() {
        return type;
    }

    public double distance(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
