import com.opencsv.CSVReader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {


    static class RawPassenger {
        public String passengerId;
        public String pClass;
        public String name;
        public String sex;
        public String age;
        public String sibSp;
        public String parch;
        public String ticket;
        public String fare;
        public String cabin;
        public String embarked;
    }


    static class Passenger {
        public double pClass;
        public double sex;
        public double age;
        public double fare;
        public double familySize;

        Passenger(RawPassenger rp, double medianAge) {
            pClass = rp.pClass.equals("") ? 0 : Double.parseDouble(rp.pClass);
            fare = rp.fare.equals("") ? 0 : Double.parseDouble(rp.fare);
            familySize = 1 + (rp.sibSp.equals("") ? 0 : Double.parseDouble(rp.sibSp)) + (rp.parch.equals("") ? 0 : Double.parseDouble(rp.parch));
            sex = rp.sex.equals("male") ? 1 : -1;
            if (rp.age.equals("") && fare == 0) {
                age = 0;
            } else {
                age = medianAge;
            }

        }
    }

    public static void main(String[] args) throws IOException, InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        CSVReader reader = null;
        ArrayList<Passenger> passengers = new ArrayList<>();
        ArrayList<RawPassenger> rawPassengers = new ArrayList<>();
        try {
            //parsing a CSV file into CSVReader class constructor
            reader = new CSVReader(new FileReader("test.csv"));
            String[] nextLine;
            //reads one line at a time
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {

                RawPassenger rp = new RawPassenger();
                rp.passengerId = nextLine[0];
                rp.pClass = nextLine[1];
                rp.name = nextLine[2];
                rp.sex = nextLine[3];
                rp.age = nextLine[4];
                rp.sibSp = nextLine[5];
                rp.parch = nextLine[6];
                rp.ticket = nextLine[7];
                rp.fare = nextLine[8];
                rp.cabin = nextLine[9];
                rp.embarked = nextLine[10];
                rawPassengers.add(rp);

                for (String token : nextLine) {
                    System.out.print(token + "|");
                }
                System.out.print("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rawPassengers.sort(Comparator.comparingInt(p -> (p.age.equals("") ? 0 : (int) Double.parseDouble(p.age))));
        double medianAge = rawPassengers.get(rawPassengers.size() / 2).age.equals("") ? 0 : Double.parseDouble(rawPassengers.get(rawPassengers.size() / 2).age);
        for (RawPassenger rp : rawPassengers) {
            passengers.add(new Passenger(rp, medianAge));
        }
        Passenger passenger = passengers.get(0);
        float[] f = new float[]{(float) passenger.pClass, (float) passenger.sex, (float) passenger.familySize, (float) passenger.fare, (float) passenger.age};

        MultiLayerNetwork model = KerasModelImport.importKerasSequentialModelAndWeights("model.h5");
        int inputs = 5;
        INDArray features = Nd4j.create(f, new long[]{1, 5});

        double prediction = model.output(features).getDouble(0);
        for (float i : f) {
            System.out.print(i + " | ");
        }
        System.out.println(prediction);
    }
}