package Project;

public class Keys extends Item {

    public String keyType;
    public String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost key if any(Written on the key.)");
        brand = sc.nextLine();
    }
    public void setDetails() {
        name = "keys";
        setDate();
        setArea();
        setBrand();
        setColor();
        System.out.println("Specify the type of key you have lost(Vehicle keys,Lock keys etc)");
        keyType = sc.nextLine();
        setAttribute();
    }
    public String getClassName() {
        return "Main.Keys";
    }
}
