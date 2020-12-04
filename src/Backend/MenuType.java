package Backend;

public enum MenuType{

    Default("Default"),
    Breakfast("Breakfast"),
    Lunch("Lunch"),
    Dinner("Dinner"),
    Dessert("Dessert"),
    Alcohol("Alcohol");

    public String name;

    private MenuType(String name){
        this.name = name;
    }

    public MenuType getValue(String name){
        MenuType foundMenu = null;
        for (MenuType menu: MenuType.values()){
            if (menu.name.equalsIgnoreCase(name)){
                foundMenu =  menu;
            }
        }
        return foundMenu;
    }
}