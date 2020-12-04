package Backend;

public enum KitchenUse{

    Default("Default"),
    Storage("Storage"),
    Cooking("Cooking"),
    Preparation("Preparation"),
    Serving("Serving"),
    Sanitation("Sanitation"),
    Uniform("Uniform");

    public String name;
    private KitchenUse(String name){
        this.name = name;
    }

    public KitchenUse getValue(String name){
        KitchenUse foundUse = null;
        for (KitchenUse use: KitchenUse.values()){
            if (use.name.equalsIgnoreCase(name)){
                foundUse =  use;
            }
        }
        return foundUse;
    }
}