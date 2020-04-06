import com.opencsv.CSVReader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {


    public static List<Passenger> readFromCSV(String filename) {
        CSVReader reader = null;
        ArrayList<Passenger> passengers = new ArrayList<>();
        ArrayList<RawPassenger> rawPassengers = new ArrayList<>();
        try {
            reader = new CSVReader(new FileReader(filename));
            String[] nextLine;
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                rawPassengers.add(new RawPassenger(nextLine));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // calculating median age for absent info
        rawPassengers.sort(Comparator.comparingInt(p -> (p.age.equals("") ? 0 : (int) Double.parseDouble(p.age))));
        double medianAge = rawPassengers.get(rawPassengers.size() / 2).age.equals("") ? 0 : Double.parseDouble(rawPassengers.get(rawPassengers.size() / 2).age);
        // dropping unused fields
        for (RawPassenger rp : rawPassengers) {
            passengers.add(new Passenger(rp, medianAge));
        }
        return passengers;
    }


    public static void main(String[] args) throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {


        List<Passenger> passengers = readFromCSV("test.csv");

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