import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ExecutionTimePlot {

    public static void main(String[] args) {
        // Create a list to store the points and bars
        List<Point> points = new ArrayList<>();
        List<LineStrip> bars = new ArrayList<>();

        File folder = new File("results");
        File[] listOfFiles = folder.listFiles();

        // Create a color mapper
        ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), 0, listOfFiles.length);

        int fileIndex = 0;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.endsWith(".csv") ) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                        for (int i = 0; i < 8; i++) {
                            reader.readLine();
                        }
                        // The next line contains the average execution time
                        String line = reader.readLine();
                        int averageExecutionTime = Integer.parseInt(line.split(": ")[1].trim());

                        // Extract mean and variance from the file name
                        String[] parts = fileName.split("_");
                        if (parts.length < 7) {
                            System.err.println("Unexpected file name format: " + fileName);
                            continue;
                        }
                        double mean = Double.parseDouble(parts[4]);
                        double variance = Double.parseDouble(parts[6].substring(0, parts[6].length() - 4));
                        int dataSize = Integer.parseInt(parts[2]);

                        // Create a point and add it to the list
                        Point point = new Point(new Coord3d(mean, variance, averageExecutionTime), getColor(dataSize));
                        point.setWidth(10.0f); // Set the point width to 10.0
                        points.add(point);

                        // Create a bar (line strip) from the zero to the point along the z-axis
//                        LineStrip bar = new LineStrip();
//                        bar.add(new Point(new Coord3d(mean, variance, 0)));
//                        bar.add(point);
//                        bars.add(bar);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                fileIndex++;
            }
        }

        // Create a chart and add the points and bars to it
        AWTChart chart = new AWTChart(Quality.Advanced);
        for (Point point : points) {
            chart.getScene().add(point);
        }
        for (LineStrip bar : bars) {
            chart.getScene().add(bar);
        }

        // Add a mouse controller to enable zooming in and out
        AWTCameraMouseController mouseController = new AWTCameraMouseController(chart);
        chart.addController(mouseController);

        // Display the chart
        chart.open("Execution Times", 1000, 1000);
    }

    private static Color getColor(int dataSize) {
        switch (dataSize) {
            case 100:
                return Color.RED;
            case 200:
                return Color.BLUE;
            case 300:
                return Color.GREEN;
            case 400:
                return Color.BLACK;
            case 1000:
                return Color.YELLOW;
            default:
                return Color.GRAY; // Default color for other data sizes
        }
    }
}