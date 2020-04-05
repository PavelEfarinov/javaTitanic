import com.opencsv.CSVReader;
import org.tensorflow.Graph;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

    public static void main(String[] args) {
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
        try (SavedModelBundle b = SavedModelBundle.load("model", "serve")) {
            // create the session from the Bundle
            Session sess = b.session();
            // create an input Tensor, value = 2.0f
            float[] f = new float[]{(float) passenger.pClass, (float) passenger.sex, (float) passenger.familySize, (float) passenger.fare, (float) passenger.age};

            Tensor x = Tensor.create(
                    new long[]{5},
                    FloatBuffer.wrap(f)
            );

            // run the model and get the result, 4.0f.
            float[] y = sess.runner()
                    .feed("input", x)
                    .fetch("y")
                    .run()
                    .get(0)
                    .copyTo(new float[1]);

            // print out the result.
            System.out.println(y[0]);
        }

//        evaluate_passenger(passengers.get(0));
    }

//    private static void evaluate_passenger(Passenger passenger) {
//        String modelDir = "";
//
//        byte[] graphDef = readAllBytesOrExit(Paths.get("./", "saved_model.pb"));
//        String[] labels = new String[]{"dead", "alive"};
//
//
//        float[] labelProbabilities = executeGraph(graphDef, Tensor.create(new long[]{5}, FloatBuffer.wrap(f)));
//
//        int bestLabelIdx = maxIndex(labelProbabilities);
//        System.out.println(
//                String.format("BEST MATCH: %s (%.2f%% likely)", labels[bestLabelIdx],
//                        labelProbabilities[bestLabelIdx] * 100f));
//
//    }

    private static float[] executeGraph(byte[] graphDef, Tensor<Float> image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 // Generally, there may be multiple output tensors, all of them must be closed to prevent resource leaks.
                 Tensor<Float> result =
                         s.runner().feed("input", image).fetch("output").run().get(0).expect(Float.class)) {
                final long[] rshape = result.shape();
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)));
                }
                int nlabels = (int) rshape[1];
                return result.copyTo(new float[1][nlabels])[0];
            }
        }
    }

    private static int maxIndex(float[] probabilities) {
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }

    private static byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }
}