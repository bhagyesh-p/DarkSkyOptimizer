package GetData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HtmlParser {
    public HashMap<String, HashMap<String, List<PreProcessPair<String, String>>>> CountryAndStateData = new HashMap<>();
    public HashMap<String, HashMap<String, PreProcessDay>> LocAndConData = new HashMap<>();

    public HtmlParser() {
        processData();
    }

    public void processData() {
        String url = "https://www.cleardarksky.com/csk/big_clist.html"; // Replace with your desired URL
        try {
            Document doc = Jsoup.connect(url).get(); // Connect to URL and retrieve HTML
            Element root = doc.child(0); // Get the root element of the HTML tree
            Element body = root.children().get(1);
            Element rawTable = body.child(2).child(0);
            List<Node> ListOfCountry = rawTable.childNodes();
            for (int i = 1; i < ListOfCountry.size(); i += 2) {
                HashMap<String, List<PreProcessPair<String, String>>> subStateData = new HashMap<>();

                Node country = ListOfCountry.get(i);
                String countryName = country.childNodes().get(0).childNodes().get(0).attributes().get("name");
//                System.out.println(countryName);

                Node subStates = ListOfCountry.get(i + 1);
                for (Node n : subStates.childNodes().get(0).childNodes().get(0).childNodes()) {
                    Node possibleName = n.childNodes().get(0);

                    // not straight name, nested lower
                    if (possibleName.attributes().hasKey("size") && possibleName.attributes().get("size").equals("-1")) {
                        possibleName = possibleName.childNodes().get(0);
                        String name = possibleName.toString().replaceAll("<a>", "");
                        name = name.replaceAll("</a>", "");
//                        System.out.println("\t" + name);
                        subStateData.put(name, loadCountryAndStateData(possibleName));
                    } else {
                        // name not nested lower
//                        System.out.println("\t" + possibleName);
                        subStateData.put(possibleName.toString(), loadCountryAndStateData(possibleName));
                    }
                }
                CountryAndStateData.put(countryName, subStateData);
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private List<PreProcessPair<String, String>> loadCountryAndStateData(Node state) {
        int count = 0;
        List<PreProcessPair<String, String>> filteredListOfLocations = new LinkedList<>();
        Node stateDataRaw = Objects.requireNonNull(state.parent()).childNode(1);
        List<Node> listOfLocations = stateDataRaw.childNodes();
        for (int i = 0; i < listOfLocations.size(); i++) {
            if (state.toString().equals("<a>Iowa</a>")) {
                if (i == 0) {
                    continue;
                }
                Node location = listOfLocations.get(i);
                Node locationData;
                if (location.childNodes().size() == 4) {
                    locationData = location.childNode(1);
                } else {
                    locationData = location.childNode(0);
                }
                String link;
                // sponsor tag
                if (locationData.toString().contains("tbody")) {
                    Node data = locationData.childNode(0).childNode(0).childNode(1).childNode(0);
                    locationData = data.childNode(0);
                    link = data.attr("href");
                    filteredListOfLocations.add(new PreProcessPair<>(locationData.toString(), link));
//                    System.out.println("\t \t" + locationData + " " + link);
                }

                // messed up tag
                else if (locationData.toString().contains("<a")) {
                    int dataIndex = 0;
                    if (count == 0) {
                        count++;
                        dataIndex++;
                    }
                    locationData = location.childNode(dataIndex).childNode(0);
                    link = location.childNode(dataIndex).attr("href");
                    filteredListOfLocations.add(new PreProcessPair<>(locationData.toString(), link));
//                    System.out.println("\t \t" + locationData + " " + link);
                } else {
                    link = location.childNode(0).attr("href");

                    filteredListOfLocations.add(new PreProcessPair<>(locationData.toString(), link));
//                    System.out.println("\t \t" + locationData + " " + link);
                }
            } else {
                Node location = listOfLocations.get(i);
                Node locationData = location.childNode(0).childNode(0);
                String link;
                // sponsor tag
                if (locationData.toString().contains("tbody")) {
                    Node data = locationData.childNode(0).childNode(1).childNode(0);
                    locationData = data.childNode(0);
                    link = data.attr("href");

                    filteredListOfLocations.add(new PreProcessPair<>(locationData.toString(), link));
//                    System.out.println("\t \t" + locationData + " " + link);
                }

                // messed up tag
                else if (locationData.toString().contains("<a")) {
                    locationData = location.childNode(0).childNode(0).childNode(0);
                    link = location.childNode(0).childNode(0).attr("href");

                    filteredListOfLocations.add(new PreProcessPair<>(locationData.toString(), link));
//                    System.out.println("\t \t" + locationData + " " + link);
                } else {
                    link = location.childNode(0).attr("href");

                    filteredListOfLocations.add(new PreProcessPair<>(locationData.toString(), link));
//                    System.out.println("\t \t" + locationData + " " + link);
                }
            }

        }
        return filteredListOfLocations;
    }

    // Recursive function to print the HTML tree structure
    private static void printTree(Element element, int depth) {
        // Print the current element with indentation based on depth
        System.out.println(getIndent(depth) + element.tagName());

        // Print the child elements recursively
        Elements children = element.children();
        for (Element child : children) {
            printTree(child, depth + 1);
        }
    }

    // Helper function to generate the indentation based on depth
    private static String getIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(Math.max(0, depth)));
        return sb.toString();
    }

    public void loadStateConData(String country, String state) {
        List<PreProcessPair<String, String>> listOfLocations = CountryAndStateData.get(country).get(state);
        // limited to 4 values 15m/per aka 1h
        Stack<String> darknessValues = new Stack<>();
        List<String> seeingData = new LinkedList<>();
        List<String[]> darkData = new LinkedList<>();


        for (PreProcessPair<String, String> location : listOfLocations) {
            System.out.println(location.getFirst());
            String url = "https://www.cleardarksky.com/" + location.getSecond(); // Replace with your desired URL
            try {
                Document doc = Jsoup.connect(url).get(); // Connect to URL and retrieve HTML
                Element root = doc.child(0); // Get the root element of the HTML tree
                Node dataRaw = null;
                for (int i = 0; i < root.childNodeSize() - 1; i++) {
                    if (root.child(i).toString().contains("ckmap")) {
                        dataRaw = root.child(i);
                        break;
                    }
                }
                if (dataRaw == null) {
                    return;
                }
                Node tableData = dataRaw.childNode(5).childNode(0).childNode(0).childNode(1).childNode(0).childNode(0).childNode(1).childNode(0).childNode(0).childNode(1);

                HashMap<String, PreProcessDay> D2D_Data = new HashMap<>();
                List<Node> childNodes = tableData.childNodes();
                locationLoop:for (int i = 0; i < childNodes.size() - 1; i++) {
                    Node n = childNodes.get(i);
                    if (n instanceof TextNode) {
                        continue;
                    }

                    String data = n.attr("title");
                    String dateLink = n.attr("href");
                    String[] decoded;
                    // skip data if it out of bounds time range (6pm-5am) local
                    // go home link skip
                    if (!data.contains(":")) {
                        continue;
                    }
                    int time = Integer.parseInt(data.substring(0, data.indexOf(":")));
                    if (time > 5 && time < 18) {
                        continue;
                    }
                    double avgLumMag = 0;
                    double avgMoonIlum = 0;

                    if (dateLink.contains("php")) {
                        decoded = decode(dateLink);
                    } else if(data.contains("Cloud") || data.contains("Clear Sky") || data.contains("Overcast")){
                        // case for ECMWF Cloud data https://www.cleardarksky.com//c/GrySksObCAkey.html?1
                        // will have reg. cloud data so we can skip this
                        continue;
                    } else {
                        darknessValues.push(data);
                        // digest data
                        if (darknessValues.size() == 4) {
                            while (!darknessValues.isEmpty()) {
                                String currDataDarkness = darknessValues.pop();
                                avgLumMag += Double.parseDouble(currDataDarkness.substring(currDataDarkness.indexOf("Limiting Mag:") + 13, currDataDarkness.indexOf(",")));
                                avgMoonIlum += Double.parseDouble(currDataDarkness.substring(currDataDarkness.indexOf("MoonIllum ") + 10, currDataDarkness.indexOf("%")));
                            }
                            avgLumMag = avgLumMag / 4;
                            avgMoonIlum = avgMoonIlum / 4;
                            darkData.add(new String[]{Integer.toString(time), String.valueOf(avgLumMag), String.valueOf(avgMoonIlum)});
                        }
                        continue;
                    }

                    PreProcessDay currDay = D2D_Data.getOrDefault("1", new PreProcessDay());

                    String type = decoded[1];
                    PreProcessTimeData currTimeData = currDay.daysData.getOrDefault(decoded[2], new PreProcessTimeData());
                    switch (type) {
                        case "C" -> currTimeData.cloud = data;
                        case "T" -> currTimeData.trans = data;
                        case "S" -> {
                            // three count
                            // dont add a new obj currDay.daysData.put(decoded[2],currTimeData);
                            // skip after adding to list
                            seeingData.add(data + " " + dateLink);
                            continue;
                        }
                        case "W" -> currTimeData.smoke = data;
                        case "D" -> currTimeData.wind = data;
                        case "H" -> currTimeData.humidity = data;
                        case "R" -> currTimeData.temp = data;
                        case "L" -> {
                            // if we see the sponored feature location, there are extra data
                            // we can see a href value with L as the case, if that is the case stop processing
                            // we can go to the next location
                            continue locationLoop;
                        }
                        default -> {
                            // the case for darkness
                            currTimeData.alumMag = avgLumMag;
                            currTimeData.moonAlum = avgMoonIlum;
                        }
                    }
                    currDay.daysData.put(decoded[2], currTimeData);
                    D2D_Data.put("1", currDay);
                }
                // process seeing data
                // process dark data
                // todo there is only one day, not multiple. they are based on the zulu value, not the decode arr [0] value
                PreProcessDay value = D2D_Data.get("1");
                List<String> seqTimeKeys = value.getSeqDateKeys();
                for (int i = 0; i < seqTimeKeys.size(); i++) {
                    String currTimeIdx = seqTimeKeys.get(i);

                    HashMap<String, PreProcessTimeData> daysData = value.daysData;
                    // get the timedata from the hashmap above
                    PreProcessTimeData currTimeData = daysData.get(currTimeIdx);
                    // update the value
                    // update see data

                    currTimeData.see = seeingData.get(i);
                    // update the dark data
                    currTimeData.alumMag = Double.valueOf(darkData.get(i)[1]);
                    currTimeData.moonAlum = Double.valueOf(darkData.get(i)[2]);
                    //back into day obj
                    daysData.put(currTimeIdx, currTimeData);
                }
                seeingData.clear();
                D2D_Data = cleanup(D2D_Data);
//                System.out.println(D2D_Data);
                LocAndConData.put(location.getFirst(), D2D_Data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void bestInState(String country, String state) {
        loadStateConData(country, state);
    }

    public HashMap<String, PreProcessDay> cleanup(HashMap<String, PreProcessDay> D2D_Data){
        HashMap<String, PreProcessDay> newD2D = new HashMap<>();
        HashMap<String, PreProcessTimeData> hourlyData = new HashMap<>();

        PreProcessDay singleDay = D2D_Data.get("1");
        Map<String, PreProcessTimeData> sortedMap = new TreeMap<>(singleDay.daysData);
        int dayCounter = 0;
        for (Map.Entry<String, PreProcessTimeData> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            PreProcessTimeData value = entry.getValue();

            int hour = Integer.parseInt(value.cloud.substring(0,value.cloud.indexOf(":")));

            // make a copy for the new mapping that will exist in the {some day count} GetData.Day obj
            hourlyData.put(key, value);
            if(hour == 5){
                // create a new day
                PreProcessDay newDay = new PreProcessDay();
                // set the temp data to the obj
                newDay.daysData = hourlyData;
                // take that obj and set it as  {some day count}, GetData.Day
                newD2D.put(String.valueOf(dayCounter), newDay);
                dayCounter++;
                hourlyData = new HashMap<>();
            }
        }
        return newD2D;
    }

    private String[] decode(String dateLink) {
        String[] data = new String[4];
        String dateNumLoc = dateLink.substring(11);
        // date
        data[0] = dateNumLoc.substring(0, 9);
        // type
        data[1] = dateNumLoc.substring(9, 10);
        // time
        data[2] = dateNumLoc.substring(10, 15);
        // Loc
        data[3] = dateNumLoc.substring(15);
        return data;
    }


    private static void printListOfLists(List<List<String>> listOfLists) {
        int maxItemLength = 0;
        for (List<String> list : listOfLists) {
            for (String item : list) {
                int itemLength = item.length();
                if (itemLength > maxItemLength) {
                    maxItemLength = itemLength;
                }
            }
        }

        for (List<String> list : listOfLists) {
            for (String item : list) {
                String formattedItem = String.format("%-" + maxItemLength + "s", item);
                System.out.print(formattedItem + " ");
            }
            System.out.println();
        }
    }

}
