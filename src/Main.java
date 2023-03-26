import GetData.HtmlParser;
import GetData.PreProcessPair;
import PostProcessData.DataProcessing;
import PostProcessData.StarGazingRankingAlgorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        HtmlParser htmlParser = new HtmlParser();
        HashMap<String, HashMap<String, List<PreProcessPair<String,String>>>> data = htmlParser.CountryAndStateData;

        System.out.println("What country");
        System.out.println(data.keySet());
        Scanner scan = new Scanner(System.in);
        String country = "USA";
//        String country = scan.nextLine();
//        while (!data.containsKey(country)){
//            System.out.println("Error country");
//            country = scan.nextLine();
//        }

        System.out.println("What State");
        System.out.println(data.get(country).keySet());
        String state = "California";
//        String state = scan.nextLine();
//        while (!data.get(country).containsKey(state)){
//            System.out.println("Error State");
//            state = scan.nextLine();
//        }

        htmlParser.bestInState(country, state);
//        System.out.println(htmlParser.LocAndConData);
        DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.convert(htmlParser.LocAndConData);
//        System.out.println(dataProcessing.LocAndConData);
        StarGazingRankingAlgorithm starGazingRankingAlgorithm = new StarGazingRankingAlgorithm( dataProcessing.LocAndConData, 3);
        starGazingRankingAlgorithm.printResults();
        try {
            FileWriter writer = new FileWriter("tes0t.txt");
            writer.write(String.valueOf(dataProcessing.LocAndConData));
            writer.close();
            System.out.println("Data has been written to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }

    }


    public static void getAllLocationsData(){
        String content = null;
        URLConnection connection = null;
        try {
            connection =  new URL("https://www.cleardarksky.com/csk/big_clist.html").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        System.out.println(content);

    }


}
