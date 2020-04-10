import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {

        List<Passenger> passengers = DataPreprocessor.getModifiedData(DataPreprocessor.readFromCsv("test.csv"));

        StringBuilder outputfile = new StringBuilder("PassengerId,Survived\n");

        for (Passenger passenger : passengers) {
            MultiLayerNetwork model = KerasModelImport.importKerasSequentialModelAndWeights("model.h5");

            int inputs = 5;
            float[] f = new float[]{(float) passenger.pClass, (float) passenger.sex, (float) passenger.familySize, (float) passenger.fare, (float) passenger.age};
            INDArray features = Nd4j.create(f, 1, inputs);

            double prediction = model.output(features).getDouble(0);
            outputfile.append(passenger.passengerId).append(",").append(prediction > 0.5 ? 1 : 0).append("\n");
        }

        Files.writeString(Paths.get("./submission.csv"), outputfile.subSequence(0, outputfile.length()));
    }
}