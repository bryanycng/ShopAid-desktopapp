import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import java.io.BufferedReader;
import java.io.File;
import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.io.FileReader;

public class main {
    public static Connection conn =  null;
    public static void main(String[] args) {

        // have to link mysql jdbc driver first
        try
        {
            //read credentials needed for access to local mysql database
            String currDir= System.getProperty("user.dir");
            File file = new File(currDir + "/src/credentials.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String[] splitted;
            String url = "";
            String user = "";
            String password = "";

            while((line = br.readLine()) != null){
                splitted = line.split("\\s+");
                if (splitted[0].equals("Url:")) {
                    url = splitted[1];
                }
                if (splitted[0].equals("Username:")) {
                    user = splitted[1];
                }
                if (splitted[0].equals("Password:")) {
                    password = splitted[1];
                }
            }
            //right now manually adding the  mysql connector jar to this project, change when build
            conn = DriverManager.getConnection(url, user, password);
        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }


        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Shop Aid!");
        System.out.println("Try some of the following commands:");
        System.out.println("- Insert item price");
        System.out.println("- List all items");
        System.out.println("- List item");
        System.out.println("- Min/Max item");
        System.out.println("- Min/Max item since date");
        System.out.println("- Exit");
        System.out.print("Command:");



        //test addItemPriceAuto
        //addItemPriceAuto("Pirate Hat");



        while (true) {
            String input = scanner.nextLine();  // Read user input
            input = input.toLowerCase();
            if (input.equals("exit")) {
                try{
                    if(conn != null)
                    conn.close();
                }catch(SQLException ex){
                    System.out.println(ex.getMessage());
                }
                System.exit(0);
            }
            else if (input.equals("insert item price")) {
                System.out.print("Please enter an item name: ");
                String name = scanner.nextLine();
                System.out.print("Please enter a price: ");
                String price = scanner.nextLine();
                addItemPrice(Integer.parseInt(price), name);
            }
            else if (input.equals("check prices of item")) {
            }
            else if (input.equals("list all items")) {
                listAllItems();
            }
            else if (input.equals("list item")) {
                System.out.print("Please enter an item name: ");
                String name = scanner.nextLine();
                listItem(name);
            }
            /*
            else if (input.equals("min/max item")){
                System.out.print("Please enter an item name: ");
                String name = scanner.nextLine();
                minMax(name);
            }
            else if (input.equals("min/max item since date")) {
                System.out.print("Please enter an item name: ");
                String name = scanner.nextLine();
                System.out.print("Please enter a date in yy/mm/dd format: ");
                String date = scanner.nextLine();
                minMaxDate(name, date);
            }
            */
            else {
                System.out.println("Invalid command: " + input);
            }
        }
    }

    //can move to different class later
    //can actually put this in something called a stored procedure
    //and just parameters into a function that calls the stored procedure
    public static void addItemPrice(int price, String item) {
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM item WHERE name = '" + item + "';";
            ResultSet rs = stmt.executeQuery(sql);
            //if item not in the table already insert it
            if (!rs.next()) {
                sql = "INSERT INTO item (name) VALUES ('" + item + "'); ";
                stmt.executeUpdate(sql);
            }
            sql = "INSERT INTO price (price, date, item) VALUES (" + price +" , NOW(), '" + item + "'); ";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch(SQLException se){
            se.printStackTrace();
            System.out.println("Sql exception");
        }
    }

    // return the rows of all item prices
    public static void listAllItems() {
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM price;";
            ResultSet rs = stmt.executeQuery(sql);
            //can do something else to check if rs has anything in it, maybe just rs.empty() something instead of
            //setting empty to false in while loop everytime
            Boolean empty = true;
            while (rs.next()) {
                empty = false;
                String name = rs.getString("item");
                int price = rs.getInt("price");
                Date date = rs.getDate("date");
                System.out.println(name + ", " + price + ", " + date.toString());
            }
            if (empty) {
                System.out.println("There are no records in the database");
            }
            stmt.close();
        } catch(SQLException se){
            se.printStackTrace();
            System.out.println("Sql exception 2");
        }
    }

    // return the rows of specified item prices
    public static void listItem(String item) {
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM price WHERE item = '" + item + "';";
            ResultSet rs = stmt.executeQuery(sql);
            Boolean empty = true;
            while (rs.next()) {
                empty = false;
                String name = rs.getString("item");
                int price = rs.getInt("price");
                Date date = rs.getDate("date");
                System.out.println(name + ", " + price + ", " + date.toString());
            }
            if (empty) {
                System.out.println("There are no records for " + item+ "in the database");
            }
            stmt.close();
        } catch(SQLException se){
            se.printStackTrace();
            System.out.println("Sql exception 3");
        }
    }

    /*
    // return the rows of specified item prices
    public static void minMax(String item) {
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM price WHERE item = '" + item + "' AND ";";
            ResultSet rs = stmt.executeQuery(sql);
            Boolean empty = true;
            while (rs.next()) {
                empty = false;
                String name = rs.getString("item");
                int price = rs.getInt("price");
                Date date = rs.getDate("date");
                System.out.println(name + ", " + price + ", " + date.toString());
            }
            if (empty) {
                System.out.println("There are no records for " + item+ "in the database");
            }
            stmt.close();
        } catch(SQLException se){
            se.printStackTrace();
        }
    }
    */

    /*
    // return the rows of specified item prices
    public static void minMaxDate(String item, String dateSince) {
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM price WHERE item = '" + item + "';";
            ResultSet rs = stmt.executeQuery(sql);
            Boolean empty = true;
            while (rs.next()) {
                empty = false;
                String name = rs.getString("item");
                int price = rs.getInt("price");
                Date date = rs.getDate("date");
                System.out.println(name + ", " + price + ", " + date.toString());
            }
            if (empty) {
                System.out.println("There are no records for " + item+ "in the database");
            }
            stmt.close();
        } catch(SQLException se){
            se.printStackTrace();
        }
    }
    */


    //Scrapes "http://db.kakia.org/" to find item price and insert into database
    //first navigate to the url
    //find the search bar ui element and search our string item
    //maybe can shortcut this step by putting into url http://db.kakia.org/search/item (but its a single page
    //application? so maybe can't do this have to search up
    //then find all <span> tag elements and put in list, loop through all of them to find the inner text that matches
    //our search string completely
    //can do some more magic to do it smarter separate english and japanese
    //then find the data row of this element and get the hidden row price to exctract ship 2's price
    //(or any ship we want) and insert to database


    public static void addItemPriceAuto(String item) {
        WebClient client = new WebClient();
        //makes it faster since we don't need to see page
        //client.getOptions().setCssEnabled(false);
        //client.getOptions().setJavaScriptEnabled(false);
        try {
            HtmlPage page = client.getPage("http://db.kakia.org/");
            DomNodeList inputs = page.getElementsByTagName("input");
            if (inputs.getLength() < 1) {
                System.out.println(("Could not find the search widget"));
                return;
            }
            for (int i = 0; i < inputs.getLength(); i++) {
                HtmlInput input = (HtmlInput) inputs.get(i);
                //should check if input == search, not sure why placeholder returning ..
                System.out.println(input.getPlaceholder());
                if (input.getPlaceholder().equals("Search")) {
                    input.setValueAttribute(item);
                    System.out.println("hello");
                    break;
                }
            }

            //what we want is contained in span type elements
            //maybe get smarter html selector for now try this
            //learn xpath?

            /*
            Thread.sleep(5000);
            DomNodeList spanList = page.getElementsByTagName("span");
            for (int i = 0; i < spanList.getLength(); i++) {
                HtmlElement element = (HtmlElement) spanList.get(i);
                //should check if input == search, not sure why placeholder returning ..
                System.out.println(element.asText());
                if (element.asText().equals(item)) {
                    System.out.println("working");
                    break;

                }
            }
            */

            /*
            Thread.sleep(5000);
            DomNodeList tableRows = page.getElementsByTagName("tr");
            for (int i = 0; i < tableRows.getLength(); i++) {
                TableRowGroup tr = (TableRowGroup) tableRows.get(i);
                List<HtmlTableRow> listOfTd = tr.getRows();
                //fourth td is english name
                //seventh td is hidden class, price json list
                //System.out.println(listOfTd.get(3).getChildren();
                if (element.asText().equals(item)) {
                    System.out.println("working");
                    break;

                }
            }
            */

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Something wrong");
        }
    }
}