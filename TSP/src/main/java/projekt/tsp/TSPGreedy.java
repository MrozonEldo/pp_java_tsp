/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekt.tsp;

/**
 *
 * @author Mrozik
 */
// TSPGreedy.java
import java.util.ArrayList;
import java.util.List;

public class TSPGreedy {

    public static List<Point> findBestRoute(List<Point> points) {
        List<Point> route = new ArrayList<>();
        List<Point> remainingPoints = new ArrayList<>(points);

        Point currentPoint = remainingPoints.remove(0);
        route.add(currentPoint);

        while (!remainingPoints.isEmpty()) {
            Point closestPoint = null;
            double minDistance = Double.MAX_VALUE;

            for (Point point : remainingPoints) {
                double distance = currentPoint.distanceTo(point);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPoint = point;
                }
            }

            route.add(closestPoint);
            remainingPoints.remove(closestPoint);
            currentPoint = closestPoint;
        }

        return route;
    }
}
