package Project;

public class Accessories extends Item{
    public String getClassName() {
        return "Main.Accessories";
    }
    public String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the  item if any");
        brand = sc.nextLine();
    }
    String material;
    public void setDetails(){
        System.out.println("Enter the type of Accessory . e.g(Watch,cloth type(jacket,sweater) etc)");
        setName();setDate();setArea();setBrand();setColor();
        System.out.println("Enter the material of the  item:(In one word)");
        material= sc.next();
        setAttribute();
    }
}
