import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;

@Getter
public class DataModel {
    double[] input;
    double[] output;

    public DataModel(double[] doubles, double[] v) {
        this.input = new double[9];
        this.output = new double[9];
        for (int i = 0; i < doubles.length; i++) {
            this.input[i] = doubles[i];
            this.output[i] = v[i];
        }
    }
}
