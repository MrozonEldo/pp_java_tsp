package projekt.tsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static projekt.tsp.TSP.calculateTotalDistance;

public class TSP_ACS {
    public static List<Point> antColonySystem(List<Point> points) {
        final int numAnts = points.size();
        final int maxIterations = 50; // 50 dla TSP1000
        final double alpha = 1.0;
        final double beta = 2.0;
        final double evaporationRate = 0.5;
        final double initialPheromone = 1.0;
        final double Q = 100.0;
        double[][] pheromones = new double[numAnts][numAnts];
        double[][] distances = new double[numAnts][numAnts];
        Random rand = new Random(42); // Stałe ziarno dla powtarzalności

        // Inicjalizacja feromonów na podstawie zachłannej trasy
        List<Point> greedyRoute = greedyAlgorithm(points);
        double greedyDistance = calculateTotalDistance(greedyRoute);
        for (int i = 0; i < numAnts; i++) {
            for (int j = 0; j < numAnts; j++) {
                pheromones[i][j] = Q / greedyDistance;
            }
        }

        // Dystans
        for (int i = 0; i < numAnts; i++) {
            for (int j = 0; j < numAnts; j++) {
                distances[i][j] = points.get(i).distanceTo(points.get(j));
            }
        }

        // Inicjacja
        List<Point> bestRoute = new ArrayList<>();
        double bestDistance = Double.MAX_VALUE;
        long startTime = System.currentTimeMillis();

        // Główna pętla
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<Point>[] antRoutes = new List[numAnts];
            double[] antDistances = new double[numAnts];


            for (int ant = 0; ant < numAnts; ant++) {
                antRoutes[ant] = constructSolution(ant, points, pheromones, distances, alpha, beta, rand);
                antDistances[ant] = calculateTotalDistance(antRoutes[ant]);
            }

            updatePheromones(pheromones, antRoutes, antDistances, Q, evaporationRate);

            for (int ant = 0; ant < numAnts; ant++) {
                if (antDistances[ant] < bestDistance) {
                    bestDistance = antDistances[ant];
                    bestRoute = new ArrayList<>(antRoutes[ant]);
                }
            }

            // Logowanie najlepszej odległości w każdej iteracji
            System.out.println("Iteration " + iteration + ": Best Distance = " + bestDistance);

            long timeNow = System.currentTimeMillis();
            if ((timeNow - startTime) > 300000) { // 5 minut
                break;
            }
        }

        return bestRoute;
    }

    private static List<Point> constructSolution(int ant, List<Point> points, double[][] pheromones,
                                                 double[][] distances, double alpha, double beta, Random rand) {
        int numCities = points.size();
        List<Point> route = new ArrayList<>();
        boolean[] visited = new boolean[numCities];
        int currentCity = ant % numCities;

        for (int step = 0; step < numCities; step++) {
            route.add(points.get(currentCity));
            visited[currentCity] = true;

            double[] probabilities = new double[numCities];
            double totalProbability = 0.0;
            for (int nextCity = 0; nextCity < numCities; nextCity++) {
                if (!visited[nextCity]) {
                    probabilities[nextCity] = Math.pow(pheromones[currentCity][nextCity], alpha)
                            * Math.pow(1.0 / distances[currentCity][nextCity], beta);
                    totalProbability += probabilities[nextCity];
                }
            }

            double r = rand.nextDouble() * totalProbability;
            double sum = 0.0;
            for (int nextCity = 0; nextCity < numCities; nextCity++) {
                if (!visited[nextCity]) {
                    sum += probabilities[nextCity];
                    if (r <= sum) {
                        currentCity = nextCity;
                        break;
                    }
                }
            }
        }

        return route;
    }

    private static void updatePheromones(double[][] pheromones, List<Point>[] antRoutes,
                                         double[] antDistances, double Q, double evaporationRate) {
        int numAnts = antRoutes.length;
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] *= (1.0 - evaporationRate);
            }
        }

        for (int ant = 0; ant < numAnts; ant++) {
            List<Point> route = antRoutes[ant];
            double distance = antDistances[ant];
            for (int i = 0; i < route.size() - 1; i++) {
                int city1 = route.get(i).getId() - 1;
                int city2 = route.get(i + 1).getId() - 1;
                pheromones[city1][city2] += Q / distance;
                pheromones[city2][city1] += Q / distance;
            }
        }
    }

    private static List<Point> greedyAlgorithm(List<Point> points) {
        List<Point> route = new ArrayList<>();
        boolean[] visited = new boolean[points.size()];
        Point current = points.get(0);
        route.add(current);
        visited[0] = true;

        for (int i = 1; i < points.size(); i++) {
            Point nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            for (int j = 0; j < points.size(); j++) {
                if (!visited[j] && current.distanceTo(points.get(j)) < nearestDistance) {
                    nearest = points.get(j);
                    nearestDistance = current.distanceTo(nearest);
                }
            }
            route.add(nearest);
            visited[points.indexOf(nearest)] = true;
            current = nearest;
        }

        return route;
    }
}
