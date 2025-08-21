package Project;

public class Electronics extends Item{
    public String getClassName() {
        return "Main.Electronics";
    }
    public String model;
    public String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost/found item if any");
        brand = sc.nextLine();
    }
    public void setDetails(){
        System.out.println("Enter the device category(e.g Laptop, Mobile , Power bank etc) ");
        setName();setDate();setArea();setBrand();setColor();
        System.out.println("Enter the model of the electronic device e.g(Dell series laptop:Inspiron 3511)");
        model=sc.nextLine();
        setAttribute();
    }
}

