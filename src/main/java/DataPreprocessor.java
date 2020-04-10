import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class is used to manipulate data in .csv
 */

public class DataPreprocessor {

    /**
     * @param filename path to file to read data from
     * @return list of passengers with "raw" data
     */
    public static List<RawPassenger> readFromCsv(String filename) {

        try (
                Reader reader = Files.newBufferedReader(Paths.get(filename))
        ) {
            CsvToBean<RawPassenger> csvToBean = new CsvToBeanBuilder(reader)
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
        List<RawPassenger> passengerList = new ArrayList<>(rawPassengers);
        passengerList.removeIf(rawPassenger -> rawPassenger.age.equals(""));
        passengerList.sort(Comparator.comparingInt(p -> ((int) Double.parseDouble(p.age))));

        double medianAge = Double.parseDouble(passengerList.get(rawPassengers.size() / 2).age);
        
        for (RawPassenger rp : rawPassengers) {
            passengers.add(new Passenger(rp, medianAge));
        }
        return passengers;
    }

    /**
     * @param filename   path to file to save the modified data to
     * @param passengers list of passengers
     */

    public static void writeToCsv(String filename, List<Passenger> passengers) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {

        try (
                Writer writer = Files.newBufferedWriter(Paths.get(filename))
        ) {
            StatefulBeanToCsv<Passenger> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            for (Passenger passenger : passengers) {
                beanToCsv.write(passenger);
            }
        }
    }


    public static void main(String[] args) throws CsvRequiredFieldEmptyException, IOException, CsvDataTypeMismatchException {
        if (args.length < 2) {
            System.err.println("You should provide a path to input and output .csv file!");
        } else {
            writeToCsv(args[1], getModifiedData(readFromCsv(args[0])));
        }
    }

}
