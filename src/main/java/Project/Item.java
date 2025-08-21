package Project;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;




abstract public class Item {
    Scanner sc = new Scanner(System.in);
    public String name;
    public String area;
    LocalDate date;
    public String color;
    String attribute;
    public void setName() {
        name = sc.nextLine();
    }

    public void setArea() {
        System.out.println("Enter the area it might have lost/found.Format:(nearest landmark,Area name, e.g Amber Tower , Sarkhej)");
        area = sc.nextLine();
    }
    public void setDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (true) {
            System.out.print("Enter the date when it got lost/found (yyyy-MM-dd): ");
            String input = sc.nextLine().trim();

            try {
                date = LocalDate.parse(input, formatter);
                System.out.println("You entered: " + date);
                break; // Exit loop once valid date is entered
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid format. Please use yyyy-MM-dd (e.g., 2025-08-18).");
            }
        }
    }

    public void setColor() {
        System.out.println("Enter the color of the lost/found thing");
        color = sc.nextLine();
    }
    public void setAttribute() {
        System.out.println("Describe any unique quality of your lost/found item that helps the system to validate its rightful owner");
        attribute =sc.nextLine();
    }
    abstract public String getClassName();
}
