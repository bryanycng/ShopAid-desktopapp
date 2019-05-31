import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.List;

public class Scraper {
    private static String url = "http://db.kakia.org/";

    //Scrapes "http://db.kakia.org/" to find item price and insert into database
    //first navigate to the url
    //find the search bar ui element and search our string item
    //maybe can shortcut this step by putting into url http://db.kakia.org/search/item (but its a single page
    //application? so maybe can't do this have to search up
    //then find all <span> tag elements and put in list, loop through all of them to find the inner text that matches
    //our search string completely
    //can do some more magic to do it smarter separate english and japanese
    //then find the data row of this element and get the hidden row price to extract ship 2's price
    //(or any ship we want) and insert to database


    public static void addItemPriceAuto(String item) {
        WebClient client = new WebClient();
        //makes it faster since we don't need to see page
        //client.getOptions().setCssEnabled(false);
        //client.getOptions().setJavaScriptEnabled(false);
        try {
            HtmlPage page = client.getPage(url);
            DomNodeList inputs = page.getElementsByTagName("input");
            System.out.println(inputs.size());

            if (inputs.getLength() < 1) {
                System.out.println(("Could not find the search form"));
                return;
            }

            for (int i = 0; i < inputs.getLength(); i++) {
                HtmlInput input = (HtmlInput) inputs.get(i);
                System.out.println(input.getPlaceholder());

                if (input.getPlaceholder().equals("Search")) {
                    input.setValueAttribute(item);
                    System.out.println("search successful");
                    System.out.println(input.asText());
                    break;
                }
            }

            Thread.sleep(10000);

            DomNodeList tableRows = page.getElementsByTagName("tr");
            for (int i = 1; i < tableRows.getLength(); i++) {
                HtmlTableRow tr = (HtmlTableRow) tableRows.get(i);

                List<HtmlTableCell> tdList = tr.getCells();

//                for  (HtmlTableCell t : tdList) {
//                    System.out.println(t.toString() + t.getIndex());
//                }

                HtmlTableCell itemNameTD;
                HtmlTableCell priceTD;

                if (tdList.size() >= 7) {
                    itemNameTD = tdList.get(3);
                    priceTD = tdList.get(6);
                } else {
                    continue;
                }

                //fourth td is english name
                //seventh td is hidden class, price json list
                //System.out.println(listOfTd.get(3).getChildren();

                //learn how to use this selector instead of hardcoding the path
                //#item_table > tbody > tr:nth-child(1) > td:nth-child(4) > a > span

                String itemName = itemNameTD.getFirstChild().getLastChild().asText();

                System.out.println(itemName);
                String price;
//                price = priceTD.getFirstChild().getNodeValue();
//                System.out.println("printing out price");
//                System.out.println(price);

                if (itemName.equals(item)) {
                    price = priceTD.getFirstChild().getNodeValue();
                    System.out.println(price);
                    break;
                }

                System.out.println("end of for loop");
            }


        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Something wrong");
        }
    }

//    HtmlTableRow getTableRow(String item, HtmlPage page) {
//        DomNodeList tableRows = page.getElementsByTagName("tr");
//
//        for (int i = 0; i < tableRows.getLength(); i++) {
//            HtmlTableRow tr = (HtmlTableRow) tableRows.get(i);
//
//            List<HtmlTableCell> tdList = tr.getCells();
//
//            for (HtmlTableCell t : tdList) {
//                System.out.println(t.toString() + t.getIndex());
//            }
//
//            HtmlTableCell itemNameTD = tr.getCell(3);
//            HtmlTableCell priceTD = tr.getCell(6);
//        }
//
//        return null;
//    }


}
