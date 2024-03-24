import java.io.*;

public class DataIO {

    double[][] input;
    double[][] output;

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
