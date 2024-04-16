package projekt.tsp;

import java.util.ArrayList;
import java.util.List;

import static projekt.tsp.TSP.calculateTotalDistance;

public class TSP_ACS {
    public static List<Point> antColonySystem(List<Point> points) {
        final int numAnts = points.size();
        final int maxIterations = 1000;
        final double alpha = 1.0;
        final double beta = 2.0;
        final double evaporationRate = 0.5;
        final double initialPheromone = 1.0;
        final double Q = 100.0;
        double[][] pheromones = new double[numAnts][numAnts];
        double[][] distances = new double[numAnts][numAnts];

        // feromowy mrufek
        for (int i = 0; i < numAnts; i++) {
            for (int j = 0; j < numAnts; j++) {
                pheromones[i][j] = initialPheromone;
            }
        }

        // dystans
        for (int i = 0; i < numAnts; i++) {
            for (int j = 0; j < numAnts; j++) {
                distances[i][j] = points.get(i).distanceTo(points.get(j));
            }
        }

        //inicjacja
        List<Point> bestRoute = new ArrayList<>();
        double bestDistance = Double.MAX_VALUE;

        // gluwna pentla
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<Point>[] antRoutes = new List[numAnts]; // Ant routes
            double[] antDistances = new double[numAnts]; // Ant distances

            // dzialanie
            for (int ant = 0; ant < numAnts; ant++) {
                antRoutes[ant] = constructSolution(ant, points, pheromones, distances, alpha, beta);
                antDistances[ant] = calculateTotalDistance(antRoutes[ant]);
            }

            // po kazdym punktcie update feromonow
            updatePheromones(pheromones, antRoutes, antDistances, Q, evaporationRate);

            // najlepsza sciezka
            for (int ant = 0; ant < numAnts; ant++) {
                if (antDistances[ant] < bestDistance) {
                    bestDistance = antDistances[ant];
                    bestRoute = new ArrayList<>(antRoutes[ant]);
                }
            }
        }

        return bestRoute;
    }

    private static List<Point> constructSolution(int ant, List<Point> points, double[][] pheromones,
                                                 double[][] distances, double alpha, double beta) {
        int numCities = points.size();
        List<Point> route = new ArrayList<>();
        boolean[] visited = new boolean[numCities];
        int currentCity = ant % numCities; // kazda mrufka start w innym miejscu

        for (int step = 0; step < numCities; step++) {
            route.add(points.get(currentCity));
            visited[currentCity] = true;

            // obliczanie dystansu dla miast
            double[] probabilities = new double[numCities];
            double totalProbability = 0.0;
            for (int nextCity = 0; nextCity < numCities; nextCity++) {
                if (!visited[nextCity]) {
                    probabilities[nextCity] = Math.pow(pheromones[currentCity][nextCity], alpha)
                            * Math.pow(1.0 / distances[currentCity][nextCity], beta);
                    totalProbability += probabilities[nextCity];
                }
            }

            // wybor <0,1>
            double rand = Math.random() * totalProbability;
            double sum = 0.0;
            for (int nextCity = 0; nextCity < numCities; nextCity++) {
                if (!visited[nextCity]) {
                    sum += probabilities[nextCity];
                    if (rand <= sum) {
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
}
