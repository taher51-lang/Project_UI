package Project;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.JaroWinkler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


public class DoublyLinkedList{
    public DoublyLinkedList flFilter(Item subject) {
        DoublyLinkedList filter = new DoublyLinkedList();
        Node temp = first;
        while (temp!=null){
            StringMetric a = new JaroWinkler();
            float scoreName = a.compare(temp.record.get("name"), subject.name);
            float scoreAddress =  a.compare(temp.record.get("area"), subject.area);
            float scorecolor = a.compare(temp.record.get("color"), subject.color);
            float sum = scorecolor+scoreAddress+scoreName;
            if(sum>=1.95) {
                filter.insertAtLast(temp.record);
            }
            temp=temp.next;
        }
        return filter;
    }

    public DoublyLinkedList filterMoreAttributes(Item subject, String result) {
        Node temp = first;
        StringMetric match = new JaroWinkler();
        DoublyLinkedList finalFilter = new DoublyLinkedList();
        while (temp!=null){
            switch (result.toLowerCase()) {
                case "main.electronics":
                    Electronics e = (Electronics) subject;
                    float brandScore1 = match.compare(e.brand,temp.record.get("brand"));
                    float modelScore = match.compare(e.model,temp.record.get("model"));
                    if((brandScore1+modelScore)>=1.3){
                        finalFilter.insertAtLast(temp.record);
                    }
                    break;
                case "main.bags":
                    Bags b = (Bags) subject;
                    System.out.println(b.brand);
                    float MaterialScore = match.compare(b.material,temp.record.get("material"));
                    float brandScore2 = match.compare(b.brand,temp.record.get("brand"));
                    if((brandScore2+MaterialScore)>=1.3){
                        finalFilter.insertAtLast(temp.record);
                    }
                    break;
                case "main.accessories":
                    Accessories ac = (Accessories) subject;
                    float brandScore3 = match.compare(ac.brand,temp.record.get("brand"));
                    if(brandScore3>0.65){
                        finalFilter.insertAtLast(temp.record);
                    }
                    break;
                case "main.childstuff":
                    ChildStuff ch = (ChildStuff) subject;
                    float brandScore4 = match.compare(ch.brand,temp.record.get("brand"));
                    if(brandScore4>0.65){
                        finalFilter.insertAtLast(temp.record);
                    }
                    break;
                case "main.academicsupplies":
                    AcademicSupplies as = (AcademicSupplies) subject;
                    float brandScore5 = match.compare(as.brand,temp.record.get("brand"));
                    if(brandScore5>0.65){
                        finalFilter.insertAtLast(temp.record);
                    }
                    break;
                case "main.entertainmentgears":
                    EntertainmentGears et = (EntertainmentGears) subject;
                    float brandScore6 = match.compare(et.brand,temp.record.get("brand"));
                    if(brandScore6>0.65){
                        finalFilter.insertAtLast(temp.record);
                    }
                    break;
                case "main.keys":
                    Keys k = (Keys) subject;
                    float keyScore = match.compare(k.keyType,temp.record.get("keytype"));
                    float brandscore7 = match.compare(k.brand,temp.record.get("brand"));
                    if((brandscore7+keyScore)>=1.3)
                        finalFilter.insertAtLast(temp.record);
                    break;
                case "main.eyeandvision":
                    eyeAndVision ev = (eyeAndVision) subject;
                    float frameScore = match.compare(ev.FrameType,temp.record.get("frametype"));
                    float lensScore = match.compare(ev.lensGrade,temp.record.get("lensgrade"));
                    float brandScore8 = match.compare(ev.brand,temp.record.get("brand"));
                    if((frameScore+lensScore+brandScore8)>=1.95)
                        finalFilter.insertAtLast(temp.record);
                    break;
                case "main.documents":
                    Documents d = (Documents) subject;
                    float isScore = match.compare(d.issueAuthority,temp.record.get("issueauthority"));
                    if(isScore>0.65)
                        finalFilter.insertAtLast(temp.record);
                    break;
            }
            temp=temp.next;
        }
        return finalFilter;
    }

    public boolean exists() {
        return first!=null;
    }

    public void setAdminVerification(int lid,int LostUserID) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Project", "root", "");
        Node temp = first;
        while (temp!=null){
            int id = Integer.parseInt(temp.record.get("Id"));
            String sql1 = "update lostAndFound set status = 'Under Verification' where id = "+id;
            String sql2 = "update lostAndFound set status = 'Under Verification' where id = "+lid;
            String sql3 = "INSERT INTO adminverification(lostUserID, lostId, foundUserID, fid, status) VALUES ("
                    + LostUserID + ", "
                    + lid + ", "
                    + temp.record.get("uid") + ", "
                    + id+ ", 'Pending')";
            temp=temp.next;
            Statement st = con.createStatement();
            int r1 = st.executeUpdate(sql1);
            int r2 = st.executeUpdate(sql2);
            int r3 = st.executeUpdate(sql3);
            if(r1>0&&r2>0&&r3>0)
                System.out.println("Status updated");
        }
    }

    class Node{
        Node prev;
        Node next;
        HashMap<String,String> record;
        Node(HashMap<String,String> a){
            record = a;
            prev=null;
            next=null;
        }
    }
    Node first = null;
    public  void insertAtLast(HashMap<String,String> a){
        Node n = new Node(a);
        if(first==null)
            first=n;
        else{
            Node temp = first;
            while (temp.next!=null){
                temp=temp.next;
            }
            temp.next=n;
            n.prev=temp;
        }
    }
    public void display(){
        Node temp = first;
        while (temp!=null){
            System.out.println(temp.record);
            temp=temp.next;
        }
    }
}
