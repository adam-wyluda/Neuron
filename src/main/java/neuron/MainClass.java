package neuron;

import neuron.classifiers.Perceptron;
import neuron.view.MainWindow;

import javax.swing.*;

public class MainClass {

    public static void main(String... args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainWindow(new Perceptron()).setVisible(true);
            }
        });
    }
}
