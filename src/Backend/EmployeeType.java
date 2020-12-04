package Backend;

public enum EmployeeType {

    Default("Default"),
    FrontOfHouse("Front of House"),
    BackOfHouse("Back of House");

    private EmployeeType(String name){
        this.name = name;
    }
    public String name;


    public EmployeeType getValue(String value){
        EmployeeType foundType = null;
        if (value == "FOH"){
            foundType = FrontOfHouse;
        }else if(value == "BOH"){
            foundType = BackOfHouse;
        }
        return foundType;
    }
}
