package Project;

public class AcademicSupplies extends Item {
    public String getClassName() {
        return "Main.AcademicSupplies";
    }
    public String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
    public void setDetails() {
        System.out.println("Enter the name of the lost item.e.g(Notebooks,Educational equipments)");
        name = sc.nextLine();
        setDate();
        setArea();
        setBrand();
        setColor();
        setAttribute();
    }
}
