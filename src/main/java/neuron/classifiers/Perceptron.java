package neuron.classifiers;

import java.util.Random;

public class Perceptron implements Neuron {

    private static final Random random = new Random();

    /**
     * X weight.
     */
    private double wx;
    /**
     * Y weight.
     */
    private double wy;

    private double w0;

    public Perceptron() {
        wx = ((double) random.nextInt(200)) / 100.0 - 1.0;
        wy = ((double) random.nextInt(200)) / 100.0 - 1.0;
        w0 = 1.0;
    }

    @Override
    public double getWx() {
        return wx;
    }

    @Override
    public double getWy() {
        return wy;
    }

    @Override
    public void train(double x, double y, PointPosition position) {
        PointPosition result = classify(x, y, position);
        if (result != position) {
            double dt = positionToValue(position);
            wx = wx + x * dt;
            wy = wy + y * dt;
        }
    }

    @Override
    public PointPosition classify(double x, double y, PointPosition position) {
        return valueToPosition(output(x, y));
    }

    double output(double x, double y) {
        return wx * x + wy * y + w0 >= 0.0 ? 1.0 : -1.0;
    }

    static PointPosition valueToPosition(double value) {
        if (value >= 0.0) {
            return PointPosition.ABOVE;
        }
        return PointPosition.BELOW;
    }

    static double positionToValue(PointPosition position) {
        if (position == PointPosition.ABOVE) {
            return 1.0;
        }
        return -1.0;
    }
}
