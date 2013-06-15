package neuron.util;

import neuron.classifiers.Neuron;
import neuron.classifiers.PointPosition;
import neuron.view.Line;
import neuron.view.Point;

public class ModelViewTransformUtil {

    private ModelViewTransformUtil() {
    }

    public static Line lineFromPerceptron(Neuron perceptron) {
        double a, b;
        a = -(perceptron.getWx() / perceptron.getWy());
        b = -(1.0 / perceptron.getWy());
        return new Line(a, b);
    }

    public static void trainFromPoint(Neuron perceptron, Point point) {
        perceptron.train(point.getX(), point.getY(), pointColorToPointPosition(point));
    }

    public static boolean classifyFromPoint(Neuron perceptron, Point point) {
        PointPosition pointPosition =
                perceptron.classify(point.getX(), point.getY(), pointColorToPointPosition(point));
        return pointPosition == pointColorToPointPosition(point);
    }

    public static PointPosition pointColorToPointPosition(Point point) {
        if (point.getType() == Point.Type.RED) {
            return PointPosition.ABOVE;
        }
        return PointPosition.BELOW;
    }
}
