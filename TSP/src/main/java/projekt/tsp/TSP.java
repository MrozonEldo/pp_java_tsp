/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package projekt.tsp;

/*
 *
 * @author Mrozik un Alem
 */
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static projekt.tsp.TSP_ACS.antColonySystem;

public class TSP {

    public static void main(String[] args) throws IOException, InterruptedException {
        //cialo programu
        boolean isRunning = true;
        Scanner s = new Scanner(System.in);


        //od komentuj ten który potrzebujesz
        //Mrozon
        String path = "C:\\Users\\Mrozik\\Documents\\GitHub\\pp_java_tsp\\TSP\\";
        //Alem
        //String path = "C:\\projects\\pp_java_tsp\\TSP\\";

        int numCities; // Przykładowa liczba miast
        String fileName = "test", helpVal;

        List<Point> points = readCitiesFromFile(path+fileName+".txt");


        while(isRunning){
            mainMenu();
            String wyborAkcji = s.nextLine();
            switch(wyborAkcji){
                case "1" -> {
                    System.out.println("Wpisz nazwe docelowego pliku:");
                    fileName = s.nextLine();
                    System.out.println("Wpisz liczbe miast:");
                    helpVal = s.nextLine();
                    numCities = Integer.parseInt(helpVal);
                    generateCitiesFile(numCities, fileName);
                    points = readCitiesFromFile(path+fileName+".txt");
                    System.out.println("Plik zostal wygenerowany!");
                }
                case "2" ->{
                    System.out.println("Wpisz nazwe pliku:");
                    fileName = s.nextLine();
                    points = readCitiesFromFile(path+fileName+".txt");
                    System.out.println("Plik zostal wczytany!");
                }
                case "3" -> {
                    System.out.println("Obecnie wczytana instancja:");
                    for (Point point : points) {
                        System.out.println(point.getId()+".("+point.getX()+","+point.getY()+")");
                        }
                }
                case "4" -> {
                    List<Point> bestRoute = TSPGreedy.findBestRoute(points);
                    double totalDistance = calculateTotalDistance(bestRoute);
                    System.out.println("Najlepsza trasa:");
                    int counter = 0;
                    for(Point point : bestRoute){
                        counter = counter + 1;
                        System.out.println(counter+".  "+point.getId()+".("+point.getX()+","+point.getY()+")");
                    }
                    System.out.println("Długość trasy: " + Math.round(totalDistance*100.0)/100.0);
                }
                case "5" -> {
                    System.out.println("Running Ant Colony System...");
                    List<Point> bestRoute = antColonySystem(points);
                    double totalDistance = calculateTotalDistance(bestRoute);
                    System.out.println("Best route found by Ant Colony System:");
                    int counter = 0;
                    for(Point point : bestRoute){
                        counter++;
                        System.out.println(counter+".  "+point.getId()+".("+point.getX()+","+point.getY()+")");
                    }
                    System.out.println("Route length: " + Math.round(totalDistance*100.0)/100.0);
                }
                case "6" -> {
                    System.out.println("Koncze prace programu, do zobaczenia!");
                    isRunning = false;
                }
                default -> {System.out.println("Blad, sprobuj jeszcze raz.");}
            }
        }
    }

    //funkcje
    public static void generateCitiesFile(int numCities, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName+".txt"));

        writer.write(numCities + "\n");

        Random rand = new Random();
        List<Point> generatedPoints = new ArrayList<>();
        for (int i = 0; i < numCities; i++) {
            int id = i + 1,x,y;
            do {
                x = rand.nextInt(101); // Zakładamy zakres współrzędnych od 0 do 100
                y = rand.nextInt(101);
            } while (containsPoint(generatedPoints, x, y)); // Sprawdzenie, czy współrzędne nie powtarzają się

            generatedPoints.add(new Point(id, x, y));
            writer.write(id + " " + x + " " + y + "\n");
        }

        writer.close();
    }

    private static boolean containsPoint(List<Point> points, int x, int y) {
        for (Point point : points) {
            if (point.getX() == x && point.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public static List<Point> readCitiesFromFile(String fileName) throws IOException {
        List<Point> points = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        int numCities = Integer.parseInt(reader.readLine());

        for (int i = 0; i < numCities; i++) {
            String line = reader.readLine();
            String[] parts = line.split(" ");
            int id = Integer.parseInt(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            points.add(new Point(id, x, y));
        }

        reader.close();
        return points;
    }

    public static double calculateTotalDistance(List<Point> route) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += route.get(i).distanceTo(route.get(i + 1));
        }
        totalDistance += route.get(route.size() - 1).distanceTo(route.get(0));
        return totalDistance;
    }

    static void mainMenu(){
        System.out.println("""
        \nWitaj w naszym programie!
        Wpisz cyfre odpowiadajaca akcji ktora chcesz wykonac.
        1. Wygeneruj (zapisz do pliku) i wczytaj instancje.
        2. Wczytaj instancje z pliku.
        3. Pokaz instancje.
        4. TSP - metoda zachlanna.
        5. Mrufki.
        6. Wyjdz z programu
        """);
    }
}
