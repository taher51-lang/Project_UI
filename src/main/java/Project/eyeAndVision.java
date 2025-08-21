package Project;

public class eyeAndVision extends Item{
    public String FrameType;
    public String lensGrade;
    public String brand;
    public void setBrand() {
        System.out.println("Enter the brand of the lost item if any");
        brand = sc.nextLine();
    }
    public void setDetails(){
        name = "lens";
        setDate();
        setArea();
        setBrand();
        setColor();
        setAttribute();
        System.out.println("Specify the Frame type of the lens(e.g Full rim,Half rim)");
        FrameType=sc.nextLine();
        System.out.println("Specify Lens Grade. e.g(Polymer,Glass)");
        lensGrade=sc.nextLine();
    }

    public String getClassName() {
        return "Main.eyeAndVision";
    }
}

