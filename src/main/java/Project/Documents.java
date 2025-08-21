package Project;

public class Documents extends Item{
    public String getClassName() {
        return "Main.Documents";
    }
    public String issueAuthority;
    public void setDetails(){
        System.out.println("Enter the name of Document . e.g(Aadhar card,Pan card etc)");
        setName();setDate();setArea();setColor();
        System.out.println("Specify The issuing authority of the lost Document");
        issueAuthority=sc.nextLine();
        setAttribute();
    }
}
