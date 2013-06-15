package neuron.view;

/**
 * y = ax + b
 */
public class Line {

    private final double a;
    private final double b;

    public Line(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }
}
