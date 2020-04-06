/**
 * Class is used to store "raw" information from .csv inputs
 *
 * @author fools
 */
public class RawPassenger {

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
    RawPassenger(String[] csvRow) {
        passengerId = csvRow[0];
        pClass = csvRow[1];
        name = csvRow[2];
        sex = csvRow[3];
        age = csvRow[4];
        sibSp = csvRow[5];
        parch = csvRow[6];
        ticket = csvRow[7];
        fare = csvRow[8];
        cabin = csvRow[9];
        embarked = csvRow[10];
    }

}
