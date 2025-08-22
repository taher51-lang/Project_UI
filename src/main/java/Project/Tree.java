package Project;

public class Tree{
    class Node{
        Node right;
        Node left;
        String data;
        Node(String data){
            this.data = data;
            left = null;
            right = null;
        }
    }
    Node root = null;
    Boolean searchTree(String subject){
        Node temp = root;
        if(root==null)
            return false;
        else{
            while (temp!=null){
                int result = subject.compareTo(temp.data);
                if(result==0)
                    return true;
                else if (result<0) {
                    temp=temp.left;
                }
                else{
                    temp=temp.right;
                }
            }
        }
        return false;
    }
    boolean insert(String subject){
        Node n = new Node(subject);
        if(root==null)
            root=n;
        else{
            Node temp = root;
            boolean duplicate = !searchTree(subject);
            if(searchTree(subject)){
                System.out.println("The username is already taken!! Kindly choose a different one");
                return true;
            }
            while (duplicate&&temp!=null){
                if(temp.left==null&&temp.data.compareTo(subject)<0){
                    temp.left = n;
                    return false;
                }
                else if(temp.right==null&&temp.data.compareTo(subject)>0){
                    temp.right=n;
                    return false;
                }
                else{
                    if(subject.compareTo(temp.data)<0)
                        temp=temp.left;
                    else
                        temp=temp.right;
                }
            }
        }
        return false;
    }
}
