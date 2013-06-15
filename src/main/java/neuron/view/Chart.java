package neuron.view;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Chart extends JPanel {

    public enum Mode {
        DRAG, ADD_RED, ADD_BLUE, REMOVE;
    }

    public static final int CHART_WIDTH = 400;
    public static final int CHART_HEIGHT = 400;

    public static final int POINT_RADIUS_ON_CHART = 5;
    public static final double POINT_RADIUS = 2 * ((double) POINT_RADIUS_ON_CHART) / ((double) CHART_WIDTH);

    public static Stroke LINE_STROKE =
            new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0);
    public static Color DIVIDING_LINE_COLOR = Color.GREEN;
    public static Color BACKGROUND_LINE_COLOR = Color.BLACK;

    private List<Point> points = Lists.newArrayList();
    private Line line = new Line(1, 0);

    private Mode mode = Mode.DRAG;

    private boolean dragging;
    private int draggingX;
    private int draggingY;
    private Point.Type draggingType;

    public Chart() {
        addMouseListener(createMouseListener());
        addMouseMotionListener(createMouseMotionListener());
    }

    @Override
    public void paint(Graphics g) {
        drawBackground(g);
        drawLine(line.getA(), line.getB(), g);
        for (Point point : points) {
            drawPoint(calculateXOnChart(point.getX()), calculateYOnChart(point.getY()), point.getType(), g);
        }
        if (dragging) {
            drawPoint(draggingX, draggingY, draggingType, g);
        }
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    void drawBackground(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        drawLine(-1, 1, 1, 1, g, BACKGROUND_LINE_COLOR, false);
        drawLine(1, 1, 1, -1, g, BACKGROUND_LINE_COLOR, false);
        drawLine(1, -1, -1, -1, g, BACKGROUND_LINE_COLOR, false);
        drawLine(-1, -1, -1, 1, g, BACKGROUND_LINE_COLOR, false);
        drawLine(-1, 0, 1, 0, g, BACKGROUND_LINE_COLOR, false);
        drawLine(0, -1, 0, 1, g, BACKGROUND_LINE_COLOR, false);
    }

    void drawPoint(int xOnChart, int yOnChart, Point.Type pointType, Graphics g) {
        g.setColor(Color.BLACK);
        int ovalX = xOnChart - POINT_RADIUS_ON_CHART;
        int ovalY = yOnChart - POINT_RADIUS_ON_CHART;
        int diameter = 2 * POINT_RADIUS_ON_CHART;
        g.drawOval(ovalX, ovalY, diameter, diameter);
        switch (pointType) {
            case BLUE:
                g.setColor(Color.BLUE);
                break;
            case RED:
                g.setColor(Color.RED);
                break;
        }
        g.fillOval(ovalX, ovalY, diameter, diameter);
    }

    void drawLine(double x1, double y1, double x2, double y2, Graphics g, Color lineColor,
                  boolean changeStroke) {
        g.setColor(lineColor);
        Graphics2D g2d = (Graphics2D) g;
        Stroke defaultStroke = g2d.getStroke();
        g2d.setStroke(changeStroke ? LINE_STROKE : defaultStroke);
        g.drawLine(calculateXOnChart(x1), calculateYOnChart(y1),
                calculateXOnChart(x2), calculateYOnChart(y2));
        g2d.setStroke(defaultStroke);
    }

    void drawLine(double a, double b, Graphics g) {
        final double h1 = 1.0;
        final double h2 = -1.0;
        final double w1 = 1.0;
        final double w2 = -1.0;
        double x1 = (h1 - b) / a;
        double x2 = (h2 - b) / a;
        double y1 = a * w1 + b;
        double y2 = a * w2 + b;
        List<Point> cuttingPoints = new ArrayList<>();
        if (x1 >= -1.0 && x1 <= 1.0) {
            cuttingPoints.add(new Point(x1, h1, Point.Type.BLUE));
        }
        if (x2 >= -1.0 && x2 <= 1.0) {
            cuttingPoints.add(new Point(x2, h2, Point.Type.BLUE));
        }
        if (y1 >= -1.0 && y1 <= 1.0) {
            cuttingPoints.add(new Point(w1, y1, Point.Type.BLUE));
        }
        if (y2 >= -1.0 && y2 <= 1.0) {
            cuttingPoints.add(new Point(w2, y2, Point.Type.BLUE));
        }
        // If there are two points which cut chart bounds then we draw it
        // in very rare case (floating point calculation inaccuracy)
        // this list size might be equal to 1, but then the line should not be visible anyway
        // as it barely gets inside
        if (cuttingPoints.size() == 2) {
            Point start = cuttingPoints.get(0);
            Point end = cuttingPoints.get(1);
            drawLine(start.getX(), start.getY(),
                    end.getX(), end.getY(),
                    g, DIVIDING_LINE_COLOR, true);
        }
    }

    int calculateXOnChart(double x) {
        return (int) (x * CHART_WIDTH / 2) + CHART_WIDTH / 2;
    }

    int calculateYOnChart(double y) {
        return -(int) (y * CHART_HEIGHT / 2) + CHART_HEIGHT / 2;
    }

    double calculateXFromChart(int xOnChart) {
        return ((double) xOnChart) * 2.0 / ((double) CHART_WIDTH) - 1.0;
    }

    double calculateYFromChart(int yOnChart) {
        return 1.0 - ((double) yOnChart) * 2.0 / ((double) CHART_HEIGHT);
    }

    void dragElement(int xOnChart, int yOnChart) {
        double x = calculateXFromChart(xOnChart);
        double y = calculateYFromChart(yOnChart);
        Optional<Point> optionalPoint = findPointAt(x, y);
        if (optionalPoint.isPresent()) {
            Point point = optionalPoint.get();
            points.remove(point);
            draggingType = point.getType();
            dragging = true;
        }
    }

    void removeElement(int xOnChart, int yOnChart) {
        double x = calculateXFromChart(xOnChart);
        double y = calculateYFromChart(yOnChart);
        Optional<Point> optionalPoint = findPointAt(x, y);
        if (optionalPoint.isPresent()) {
            Point point = optionalPoint.get();
            points.remove(point);
        }
    }

    Optional<Point> findPointAt(double x, double y) {
        for (Point point : points) {
            if (point.distance(x, y) <= POINT_RADIUS) {
                return Optional.of(point);
            }
        }
        return Optional.absent();
    }

    void mousePressed(int xOnChart, int yOnChart) {
        switch (mode) {
            case ADD_BLUE:
                draggingType = Point.Type.BLUE;
                dragging = true;
                break;
            case ADD_RED:
                draggingType = Point.Type.RED;
                dragging = true;
                break;
            case REMOVE:
                removeElement(xOnChart, yOnChart);
                break;
            case DRAG:
                draggingX = xOnChart;
                draggingY = yOnChart;
                dragElement(xOnChart, yOnChart);
                break;
        }
        repaint();
    }

    void mouseDragged(int xOnChart, int yOnChart) {
        if (dragging) {
            draggingX = xOnChart;
            draggingY = yOnChart;
            repaint();
        }
    }

    void mouseReleased(int xOnChart, int yOnChart) {
        if (dragging) {
            Point point = new Point(calculateXFromChart(draggingX),
                    calculateYFromChart(draggingY), draggingType);
            points.add(point);
            dragging = false;
            repaint();
        }
    }

    MouseListener createMouseListener() {
        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Chart.this.mousePressed(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Chart.this.mouseReleased(e.getX(), e.getY());
            }
        };
    }

    MouseMotionListener createMouseMotionListener() {
        return new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                Chart.this.mouseDragged(e.getX(), e.getY());
            }
        };
    }
}
