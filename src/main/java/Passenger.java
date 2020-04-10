import com.opencsv.bean.CsvBindByName;

/**
 * The class to work with in my model
 *
 * @author fools
 */

public class Passenger {

    @CsvBindByName(column = "PassengerId")
    public String passengerId;

    @CsvBindByName(column = "Sex")
    public double sex;

    @CsvBindByName(column = "Pclass")
    public double pClass;

    @CsvBindByName(column = "Age")
    public double age;

    @CsvBindByName(column = "Fare")
    public double fare;

    @CsvBindByName(column = "FamilySize")
    public double familySize;

    @CsvBindByName(column = "Survived")
    public boolean survived;


    public Passenger(RawPassenger rp, double medianAge) {
        survived = !rp.survived.equals("") && (Integer.parseInt(rp.survived) == 1);
        passengerId = rp.passengerId;
        pClass = rp.pClass.equals("") ? 0 : Double.parseDouble(rp.pClass);
        fare = rp.fare.equals("") ? 0 : Double.parseDouble(rp.fare);
        familySize = 1 + (rp.sibSp.equals("") ? 0 : Double.parseDouble(rp.sibSp)) + (rp.parch.equals("") ? 0 : Double.parseDouble(rp.parch));
        sex = rp.sex.equals("male") ? 1 : -1;
        if (rp.age.equals("")) {
            if (fare == 0) {
                age = 0;
            } else {
                age = medianAge;
            }
        } else {
            age = Double.parseDouble(rp.age);
        }
    }

    public float[] toFloatArray() {
        return new float[]{(float) age, (float) familySize, (float) fare, (float) pClass, (float) sex};
    }

}
