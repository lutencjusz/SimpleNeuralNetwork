import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataIO {

    double[][] input;
    double[][] output;

    List<DataModel> dataModels = new ArrayList<>();

    public void generateDataIO() {

        // Dane treningowe (plansze i oczekiwane wyjścia)
        this.input = new double[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0}, // Pusta plansza
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Krzyżyk w lewym górnym rogu
                {0, 1, 0, 0, 0, 0, 0, 0, 0}, // Krzyżyk na górze na środku
                {0, 0, 1, 0, 0, 0, 0, 0, 0}, // Krzyżyk w prawym górnym rogu
                {0, 0, 0, 1, 0, 0, 0, 0, 0}, // Krzyżyk w lewym środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Krzyżyk na środku
                {0, 0, 0, 0, 0, 1, 0, 0, 0}, // Krzyżyk w prawym środku
                {0, 0, 0, 0, 0, 0, 1, 0, 0}, // Krzyżyk w lewym dolnym rogu
                {0, 0, 0, 0, 0, 0, 0, 1, 0}, // Krzyżyk na dole na środku
                {0, 0, 0, 0, 0, 0, 0, 0, 1}, // Krzyżyk w prawym dolnym rogu
                {1, 0, 0, 0, -1, 0, 0, 0, 0}, // Krzyżyk w lewym górnym rogu
                {0, 1, 0, 0, -1, 0, 0, 0, 0}, // Krzyżyk na górze na środku
                {0, 0, 1, 0, -1, 0, 0, 0, 0}, // Krzyżyk w prawym górnym rogu
                {0, 0, 0, 1, -1, 0, 0, 0, 0}, // Krzyżyk w lewym środku
                {-1, 0, 0, 0, 1, 0, 0, 0, 0}, // Krzyżyk na środku
                {0, 0, 0, 0, -1, 1, 0, 0, 0}, // Krzyżyk w prawym środku
                {0, 0, 0, 0, -1, 0, 1, 0, 0}, // Krzyżyk w lewym dolnym rogu
                {0, 0, 0, 0, -1, 0, 0, 1, 0}, // Krzyżyk na dole na środku
                {0, 0, 0, 0, -1, 0, 0, 0, 1}, // Krzyżyk w prawym dolnym rogu
        };

        output = new double[][]{
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 1, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 1, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
        };
    }

    public static void saveData(String filename, double[][] input) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(input);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            System.out.println("Błąd przy zapisie: " + i.getMessage());
        }
    }

    public void saveInputData(String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.input);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            System.out.println("Błąd przy zapisie: " + i.getMessage());
        }
    }

    public void saveOutputData(String filename) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.output);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            System.out.println("Błąd przy zapisie: " + i.getMessage());
        }
    }

    public void saveDataToFileInJSON(String filename, List<DataModel> data) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Zapis do pliku
            mapper.writeValue(new File(filename), data);
        } catch (IOException e) {
            System.out.println("Błąd przy zapisie: " + e.getMessage());
        }
    }

    public static void addDataToFileInJSON(String filename, List<DataModel> newData) {
        Gson gson = new Gson();
        List<DataModel> allData = new ArrayList<>();

        try {
            // Sprawdzenie czy plik istnieje
            if (Files.exists(Paths.get(filename))) {
                // Odczytanie danych z pliku i zdeserializowanie do listy obiektów DataModel
                String fileContent = new String(Files.readAllBytes(Paths.get(filename)));
                DataModel[] existingDataArray = gson.fromJson(fileContent, DataModel[].class);
                allData.addAll(List.of(existingDataArray));
            }

            // Dodanie nowych danych do listy
            allData.addAll(newData);

            // Konwersja listy obiektów na format JSON
            String jsonData = gson.toJson(allData);

            // Zapis danych do pliku
            FileWriter writer = new FileWriter(filename);
            writer.write(jsonData);
            writer.close();

            System.out.println("Dane zostały dodane do pliku JSON.");

        } catch (IOException e) {
            System.err.println("Wystąpił błąd podczas zapisywania danych do pliku.");
            System.out.println(e.getMessage());
        }
    }

    public double[][] loadInputData(String filename) {
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            this.input = (double[][]) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            System.out.println("Błąd przy wczytywaniu: " + i.getMessage());
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Nie znaleziono klasy: " + c.getMessage());
            return null;
        }
        return input;
    }

    public double[][] loadOutputData(String filename) {
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            this.output = (double[][]) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            System.out.println("Błąd przy wczytywaniu: " + i.getMessage());
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Nie znaleziono klasy: " + c.getMessage());
            return null;
        }
        return output;
    }
}
