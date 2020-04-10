import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.util.*;

public class DataPreprocessor {

    /**
     * @param filename path to file to read data from
     * @return list of passengers with "raw" data
     */
    public static List<RawPassenger> readFromCsv(String filename) {

        try {
            CsvToBean<RawPassenger> csvToBean = new CsvToBeanBuilder(new FileReader(filename))
                    .withType(RawPassenger.class)
                    .build();
            return csvToBean.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Preprocesses data to fit into the model afterwards.
     * This method itself contains only calculation of median age of te passengers.
     * Other details you can find in the Passenger class constructor.
     *
     * @param rawPassengers list of "raw" passengers to modify
     * @return list of passengers with modified data
     */
    public static List<Passenger> getModifiedData(List<RawPassenger> rawPassengers) {

        ArrayList<Passenger> passengers = new ArrayList<>();
        // calculating median age for absent info
        rawPassengers.sort(Comparator.comparingInt(p -> (p.age.equals("") ? 0 : (int) Double.parseDouble(p.age))));
        double medianAge = rawPassengers.get(rawPassengers.size() / 2).age.equals("") ? 0 : Double.parseDouble(rawPassengers.get(rawPassengers.size() / 2).age);
        // dropping unused fields
        for (RawPassenger rp : rawPassengers) {
            passengers.add(new Passenger(rp, medianAge));
        }
        return passengers;
    }

    /**
     * @param filename
     * @param passengers
     */

    public static void writeToCsv(String filename, List<Passenger> passengers) {

    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("You should provide a path to .csv file!");
        } else {
            List<RawPassenger> list = readFromCsv(args[0]);

            for (RawPassenger rawPassenger : list) {
                System.out.println(rawPassenger.name + "\n");
            }
//            writeToCsv(args[0], modifyData(readFromCsv(args[0])));
        }
    }

}
