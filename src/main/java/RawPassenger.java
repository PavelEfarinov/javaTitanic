import com.opencsv.bean.CsvBindByName;

/**
 * Class is used to store "raw" information from .csv inputs
 *
 * @author fools
 */
public class RawPassenger {

    @CsvBindByName(column = "PassengerId", required = true)
    public String passengerId;

    @CsvBindByName(column = "Pclass", required = true)
    public String pClass;

    @CsvBindByName(column = "Name")
    public String name;

    @CsvBindByName(column = "Sex", required = true)
    public String sex;

    @CsvBindByName(column = "Age")
    public String age;

    @CsvBindByName(column = "SibSp")
    public String sibSp;

    @CsvBindByName(column = "Parch")
    public String parch ;

    @CsvBindByName(column = "Ticket")
    public String ticket;

    @CsvBindByName(column = "Fare")
    public String fare;

    @CsvBindByName(column = "Cabin")
    public String cabin;

    @CsvBindByName(column = "Embarked")
    public String embarked;

    @CsvBindByName(column = "Survived")
    public String survived;

    public RawPassenger() {
    }
}
