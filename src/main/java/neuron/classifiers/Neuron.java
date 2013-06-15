package neuron.classifiers;

public interface Neuron {
    double getWx();

    double getWy();

    void train(double x, double y, PointPosition position);

    PointPosition classify(double x, double y, PointPosition position);
}
