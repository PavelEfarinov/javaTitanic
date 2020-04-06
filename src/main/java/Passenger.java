/**
 * The class to work with in my model
 *
 * @author fools
 */

public class Passenger {

    public String passengerId;
    public double pClass;
    public double sex;
    public double age;
    public double fare;
    public double familySize;

    Passenger(RawPassenger rp, double medianAge) {
        passengerId = rp.passengerId;
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
