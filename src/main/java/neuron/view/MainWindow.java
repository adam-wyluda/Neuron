package neuron.view;

import neuron.classifiers.Neuron;
import neuron.util.ModelViewTransformUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    private Neuron neuron;

    private Chart chart;
    private JLabel infoLabel;

    public MainWindow(Neuron neuron) {
        this.neuron = neuron;

        initUI();
    }

    void initUI() {
        setSize(450, 450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        chart = new Chart();
        chart.setLine(new Line(0.5, 0.0));
        List<Point> points = new ArrayList<Point>();
        points.add(new Point(0.5, -0.5, Point.Type.BLUE));
        chart.setPoints(points);
        contentPane.add(chart, BorderLayout.CENTER);

        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new FlowLayout());
        addChartOptionsToPanel(toolPanel);
        contentPane.add(toolPanel, BorderLayout.PAGE_START);

        JButton trainButton = new JButton("TRAIN");
        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                train();
                repaint();
            }
        });
        toolPanel.add(trainButton);

        infoLabel = new JLabel();
        toolPanel.add(infoLabel);

        refreshTitle();
    }

    void train() {
        for (Point point : chart.getPoints()) {
            ModelViewTransformUtil.trainFromPoint(neuron, point);
        }
        chart.setLine(ModelViewTransformUtil.lineFromPerceptron(neuron));
        refreshInfoLabel();
    }

    void addChartOptionsToPanel(JPanel panel) {
        for (final Chart.Mode mode : Chart.Mode.values()) {
            final JButton button = new JButton(mode.name());
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    chart.setMode(mode);
                    refreshTitle();
                }
            });
            panel.add(button);
        }
    }

    void refreshTitle() {
        setTitle(chart.getMode().name());
    }

    int successfulPredictions() {
        int count = 0;
        for (Point point : chart.getPoints()) {
            if (ModelViewTransformUtil.classifyFromPoint(neuron, point)) {
                count++;
            }
        }
        return count;
    }

    void refreshInfoLabel() {
        int predictions = successfulPredictions();
        int points = chart.getPoints().size();
        double percentage = 100.0 * predictions / points;
        infoLabel.setText(String.format("wx: %.3f, wy: %.3f || a: %.3f, b: %.3f || success: %.2f%%" +
                " (%d / %d)",
                neuron.getWx(), neuron.getWy(),
                chart.getLine().getA(), chart.getLine().getB(),
                percentage, predictions, points
        ));
    }
}
