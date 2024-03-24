import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class TicTacToeNeuralNetwork {
    public static void main(String[] args) {
        // Tworzenie sieci neuronowej
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 9)); // 9 wejść (3x3 plansza)
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 18)); // Warstwa ukryta z 18 neuronami
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9)); // 9 wyjść (3x3 plansza)
        network.getStructure().finalizeStructure();
        network.reset();

        // Dane treningowe (plansze i oczekiwane wyjścia)
        double[][] input = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0}, // Plansza pusta
                // Dodaj więcej plansz treningowych
        };
        double[][] output = {
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                // Dodaj więcej oczekiwanych wyjść
        };

        // Przygotowanie danych treningowych
        BasicMLDataSet trainingSet = new BasicMLDataSet(input, output);

        // Trenowanie sieci
        ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        int epoch = 1;
        do {
            train.iteration();
            System.out.println("Epoka #" + epoch + ", Błąd: " + train.getError());
            epoch++;
        } while (train.getError() > 0.01);

        // Testowanie sieci
        MLData inputMLData = new BasicMLData(input[0]);
        MLData outputMLData = network.compute(inputMLData);
        System.out.println("Wynik: " + outputMLData.getData(4)); // Przykładowe pole na planszy

        Encog.getInstance().shutdown();
    }
}
