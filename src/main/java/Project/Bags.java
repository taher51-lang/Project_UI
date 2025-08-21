package Project;

public class Bags extends Item{
    public String getClassName() {
        return "Main.Bags";
    }
    public String material;
    public String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost/found item if any");
        brand = sc.nextLine();
    }
    public void setDetails(){
        System.out.println("Enter the category of bag: e.g(shoulder bag,suitcase etc)");
        setName();setDate();setArea();setBrand();setColor();
        System.out.println("Enter the material of the  bag");
        material=sc.nextLine();
        setAttribute();
    }
}
