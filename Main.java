import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Airport{
    private String cityName;
    private String airportName;
    private ArrayList<Flight> edgeArray;
    public Airport(String cityName, String airportName){
        this.cityName = cityName;
        this.airportName = airportName;
        this.edgeArray = new ArrayList<>();
    }

    public String getCityName(){
        return cityName;
    }
    public String getAirportName(){
        return airportName;
    }

    public ArrayList<Flight> getEdgeArray() {
        return edgeArray;
    }


}

class Flight{
    private String flightID;
    private String arrivalAirportName;
    private String flightDate;
    private String durationTime;
    private int price;

    public Flight(String flightID, String arrivalAirportName, String flightDate, String durationTime, int price){
        this.flightID = flightID;
        this.arrivalAirportName = arrivalAirportName;
        this.flightDate = flightDate;
        this.durationTime = durationTime;
        this.price = price;
    }

    public String getFlightID() {
        return flightID;
    }

    public String getArrivalAirport() {
        return arrivalAirportName;
    }

    public String getFlightDate() {
        return flightDate;
    }

    public String getDurationTime() {
        return durationTime;
    }

    public int getPrice() {
        return price;
    }
}

class Digraph{
    private int V; // number of vertices
    private Airport[] airportArray;

    public Digraph(int V){
        this.V = V;
        this.airportArray = new Airport[V];
    }

    public void addEdge(String departureAirportName, Flight edge){
        for(int i=0;i<V;i++){
            if(airportArray[i].getAirportName().equals(departureAirportName)){//find the departure Airport and add Edge to list of that airport
                airportArray[i].getEdgeArray().add(edge);
            }
        }
    }

    public Airport[] getAirportArray() {
        return airportArray;
    }

    public int getV() {
        return V;
    }

    public int findAirportIndex(String airportName){
        for(int i=0;i<V;i++){
            if(airportArray[i].getAirportName().equals(airportName)){
                return i;//return index of airport
            }
        }
        return -1;//if airport is not found return -1
    }

}

class ClientManager{
    Scanner scanner1;
    public ClientManager(){
        scanner1 = null;
    }

    public Digraph createGraph(int V){
        return new Digraph(V);
    }

    public Digraph readCreateAirports(String fileName){
        File file1 = new File(fileName);
        int airportCount = 0;
        Digraph mainGraph;
        try {
            scanner1 = new Scanner(file1);
            while (scanner1.hasNextLine()) {
                String[] lineItems = scanner1.nextLine().split("\t");
                airportCount = airportCount + (lineItems.length - 1);//find the numbers of airport
            }

            scanner1.close();//in order to set cursor to beginning of file close scanner and re-open
        }
        catch(IOException ex1){
            System.out.println("File not found exception; readCreateAirports method");
        }
        mainGraph = createGraph(airportCount);//crate a new digraph
        try {
            scanner1 = new Scanner(file1);
            int airportIndex = 0;
            while (scanner1.hasNextLine()) {
                String[] lineObjects = scanner1.nextLine().split("\t");
                for (int i = 1; i < lineObjects.length; i++) {//create new Airport and add to array of graph
                    mainGraph.getAirportArray()[airportIndex] = new Airport(lineObjects[0], lineObjects[i]);
                    airportIndex++;
                }
            }
            scanner1.close();
        }
        catch(IOException ex){
            System.out.println("File not found exception; readCreateAirports method");
        }
        return mainGraph;
    }

    public void readAddEdges(String fileName, Digraph graph){
        File file1 = new File(fileName);
        try {
            scanner1 = new Scanner(file1);
            while(scanner1.hasNextLine()){
                String[] lineItems = scanner1.nextLine().split("\t");
                int departureIndex = graph.findAirportIndex(lineItems[1].split("->")[0]);
                String flightDate = lineItems[2].split(" ")[0] + " " + lineItems[2].split(" ")[1];
                Flight newEdge = new Flight(lineItems[0], lineItems[1].split("->")[1], flightDate, lineItems[3], Integer.parseInt(lineItems[4]));
                graph.getAirportArray()[departureIndex].getEdgeArray().add(newEdge);
            }
        }
        catch(IOException ex){
            System.out.println("File not found exception readAddEdges method");
        }
    }


                                                ////COMMANDS////
    //Restrictions:
    //  Arrival point of the first flight and departure point of the second flight should be the same airport.
    //  Departure time of the second flight should be later than arrival time of the first flight.
    //  Transferring between two airports at the same city is not allowed.
    //  A flight plan may not pass through the same city more than once.

    public boolean restrictionCheck(String date1, Flight flight1, ArrayList<String> visitedCities,Digraph graph){
        String nextCity = graph.getAirportArray()[graph.findAirportIndex(flight1.getArrivalAirport())].getCityName();
        return dateSuitable(date1, flight1.getFlightDate()) & visitedCitiesCheck(visitedCities, nextCity);
    }

    public boolean visitedCitiesCheck(ArrayList<String> visitedCities, String nextCity){//helper function for restrictionCheck
        for(String iter: visitedCities){
            if(iter.equals(nextCity)){
                return false;
            }
        }
        return true;
    }

    public boolean dateSuitable(String dateStr1, String dateStr2){//helper function for restrictionCheck
        //if date2 is >= date1 return true else return false
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date1;
        Date date2;
        try{
            date1 = dateFormat.parse(dateStr1);
            date2 = dateFormat.parse(dateStr2);
            return date2.compareTo(date1) > 0;
        }
        catch(ParseException pEx){
            System.out.println("parse Exception in dateSuitable method");
        }
        return false;

    }


    public String addDuration(String departureDate, String durationTime) {//sum up departure date with duration

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date date = dateFormat.parse(departureDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR,Integer.parseInt(durationTime.substring(0,2)));
            calendar.add(Calendar.MINUTE,Integer.parseInt(durationTime.substring(3,5)));
            Date newDate = calendar.getTime();
            return dateFormat.format(newDate);
        }
        catch(ParseException ex1){
            System.out.println("parseException in addDuration Method");
        }
        return null;
    }


    public ArrayList<String> listAll(Digraph graph, String commandLine){
        String[] lineObjects = commandLine.split("\t");
        String departureName = lineObjects[1].split("->")[0];
        String arrivalName = lineObjects[1].split("->")[1];
        String startDate = lineObjects[2] + " 00:00";
        ArrayList<String> properChoice = new ArrayList<>();
        for(int i=0;i<graph.getAirportArray().length;i++){
            if(graph.getAirportArray()[i].getCityName().equals(departureName)){
                ArrayList<String> visitedCities = new ArrayList<>();
                listAllRecursive(graph, startDate, graph.getAirportArray()[i], arrivalName, visitedCities, "", startDate, 0,0, properChoice);
            }
        }
        return properChoice;
    }

    public String calculateTotalTime(String departureDate, String arrivalDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date d1 = dateFormat.parse(departureDate);
            Date d2 = dateFormat.parse(arrivalDate);

            long diff = d2.getTime() - d1.getTime();
            int minutes = (int) diff / 60000;
            String returnStr = "";
            if((minutes/60)<10){
                returnStr = returnStr + "0" + minutes/60;
            }
            else{
                returnStr = returnStr + minutes/60;
            }
            returnStr = returnStr + ":";
            if(minutes%60<10){
                returnStr = returnStr + "0" + minutes%60;
            }
            else{
                returnStr = returnStr + minutes%60;
            }
            return returnStr;
        }
        catch (ParseException ex){
            System.out.println("exception calculateTotalTimeMethod");
        }
        return "";
    }

    private void arrayListCopy(ArrayList<String> listEmpty, ArrayList<String> listSrc){
        for(int i=0;i<listSrc.size();i++){
            listEmpty.add(listSrc.get(i));
        }
    }

    private void listAllRecursive(Digraph graph,String currentDate, Airport currentAirport, String arrivalCityName, ArrayList<String> visitedCities, String returnLine,String firstDepartureDate, int totalPrice, int flightNum,ArrayList<String> properChoice){
        visitedCities.add(currentAirport.getCityName());
        if(currentAirport.getCityName().equals(arrivalCityName)){
            String totalTime = calculateTotalTime(firstDepartureDate, currentDate);
            returnLine = returnLine + "\t" + totalTime + "/" + totalPrice +"\n";
            properChoice.add(returnLine);
        }
        else{
            int availableFlightSize = currentAirport.getEdgeArray().size();
            if(availableFlightSize>=1){
                for(int i=0;i<availableFlightSize;i++){
                    ArrayList<String> visitedCopy = new ArrayList<>();
                    arrayListCopy(visitedCopy,visitedCities);
                    Flight nextFlight = currentAirport.getEdgeArray().get(i);
                    String returnLineCopy = returnLine;
                    int totalPriceCopy = totalPrice;
                    if(restrictionCheck(currentDate, nextFlight, visitedCities,graph)){
                        //update currentDate
                        String updatedCurrentDate = addDuration(nextFlight.getFlightDate(), nextFlight.getDurationTime());
                        //update currentAirport(its now arrival of flight) //update visitedCities //update returnLine//update totalPrice
                        returnLineCopy = returnLineCopy + nextFlight.getFlightID() + "\t" + currentAirport.getAirportName() +
                                "->" + nextFlight.getArrivalAirport();
                        String flightCityName = graph.getAirportArray()[graph.findAirportIndex(nextFlight.getArrivalAirport())].getCityName();
                        if(flightCityName.equals(arrivalCityName)){
                        }
                        else{
                            returnLineCopy = returnLineCopy + "||";
                        }
                        totalPriceCopy += nextFlight.getPrice();
                        Airport nextAirport = graph.getAirportArray()[graph.findAirportIndex(nextFlight.getArrivalAirport())];
                        if(flightNum==0){
                            firstDepartureDate = nextFlight.getFlightDate();
                        }

                        listAllRecursive(graph, updatedCurrentDate, nextAirport, arrivalCityName, visitedCopy, returnLineCopy, firstDepartureDate,totalPriceCopy,flightNum+1, properChoice);
                    }
                }
            }
        }
    }

    public void printListAllOutput(Digraph graph, String commandLine){

        try{
            FileWriter writer1 = new FileWriter(new File("output.txt"),true);
            ArrayList<String> returnCopy = listAll(graph,commandLine);
            for(int c=0;c<returnCopy.size();c++){
                writer1.write(returnCopy.get(c));
            }
            writer1.close();
        }
        catch(IOException ex){
            System.out.println("IOException in listAllRecursive method");
        }

    }
/*
    public ArrayList<String> chooseProper(Digraph graph, String commandLine, boolean print){
        ArrayList<String> properChoice = listAll(graph, commandLine);
        String priceVTime = properChoice.get(0).split("\t")[properChoice.get(0).split("\t").length - 1];
        priceVTime = priceVTime.replace("\n","");
        int cheapestPrice = Integer.parseInt(priceVTime.split("/")[1]);
        String durationTime = priceVTime.split("/")[0];
        int shortestTime = Integer.parseInt(durationTime.split(":")[0])*60 + Integer.parseInt(durationTime.split(":")[1]);
        int properIndex = 0;
        for(int x=0;x<properChoice.size();x++){
            String newPriceVTime = properChoice.get(x).split("\t")[properChoice.get(x).split("\t").length - 1];
            int newPrice = Integer.parseInt(priceVTime.split("/")[1]);
            String newDurationTime = newPriceVTime.split("/")[0];
            int newShortestTime = Integer.parseInt(newDurationTime.split(":")[0])*60 + Integer.parseInt(newDurationTime.split(":")[1]);
            if(newPrice > cheapestPrice & newShortestTime > shortestTime){
                properIndex = x;
                cheapestPrice = newPrice;
                shortestTime = newShortestTime;
            }
        }
        if(print) {
            try {
                FileWriter writer1 = new FileWriter(new File("output.txt"), true);
                writer1.write(properChoice.get(properIndex));
                writer1.close();
            } catch (IOException ex) {
                System.out.println("in chooseProper exception");
            }
        }

        return properChoice;
    }*/

    public ArrayList<String> chooseProper(Digraph graph, String commandLine, boolean print){
        ArrayList<String> properChoice = listAll(graph, commandLine);
        for(int i=0;i<properChoice.size();i++){
            for(int x=0;x<properChoice.size();x++){
                if(i!=x){
                    String dest1 = properChoice.get(i).split("->")[properChoice.get(i).split("->").length-1].substring(0,3);
                    String dest2 = properChoice.get(x).split("->")[properChoice.get(x).split("->").length-1].substring(0,3);
                    if(dest1.equals(dest2)){//now we have two flight plan with same destination
                        String dest1DurationString = properChoice.get(i).split("\t")[properChoice.get(i).split("\t").length-1].split("/")[0];
                        String dest2DurationString = properChoice.get(x).split("\t")[properChoice.get(x).split("\t").length-1].split("/")[0];
                        int dest1Duration = Integer.parseInt(dest1DurationString.split(":")[0])*60 + Integer.parseInt(dest1DurationString.split(":")[1]);
                        int dest2Duration = Integer.parseInt(dest2DurationString.split(":")[0])*60 + Integer.parseInt(dest2DurationString.split(":")[1]);
                        String dest1PriceStr = properChoice.get(i).split("\t")[properChoice.get(i).split("\t").length-1].split("/")[1];
                        String dest2PriceStr = properChoice.get(x).split("\t")[properChoice.get(x).split("\t").length-1].split("/")[1];
                        dest1PriceStr = dest1PriceStr.replace("\n","");
                        dest2PriceStr = dest2PriceStr.replace("\n","");
                        int dest1Price = Integer.parseInt(dest1PriceStr);
                        int dest2Price = Integer.parseInt(dest2PriceStr);
                        if(dest1Price > dest2Price & dest1Duration > dest2Duration){
                            properChoice.remove(i);
                        }
                        else if(dest2Price > dest1Price & dest2Duration > dest1Duration){
                            properChoice.remove(x);
                        }
                    }
                }
            }
        }

        if(print){
            try {
                FileWriter writer1 = new FileWriter(new File("output.txt"), true);
                for(int i=0;i<properChoice.size();i++){
                    writer1.write(properChoice.get(i));
                }
                writer1.close();
            } catch (IOException ex) {
                System.out.println("in chooseProper exception");
            }
        }

        return properChoice;

    }



    public void chooseCheapest(Digraph graph, String commandLine){
        ArrayList<String> cheapestChoice = listAll(graph, commandLine);
        ArrayList<Integer> cheapestIndexes = new ArrayList<>();
        String priceStr = cheapestChoice.get(0).split("\t")[cheapestChoice.get(0).split("\t").length-1].split("/")[1];
        priceStr = priceStr.replace("\n","");
        int cheapest = Integer.parseInt(priceStr);
        for(int x=0;x<cheapestChoice.size();x++){
            String newPriceStr = cheapestChoice.get(x).split("\t")[cheapestChoice.get(x).split("\t").length-1].split("/")[1];
            newPriceStr = newPriceStr.replace("\n","");
            if(Integer.parseInt(newPriceStr) < cheapest){
                cheapest = Integer.parseInt(newPriceStr);
            }
        }
        for(int x=0;x<cheapestChoice.size();x++){
            String newPriceStr = cheapestChoice.get(x).split("\t")[cheapestChoice.get(x).split("\t").length-1].split("/")[1];
            newPriceStr = newPriceStr.replace("\n","");
            if(Integer.parseInt(newPriceStr) == cheapest){
                cheapestIndexes.add(x);
            }
        }
        try {
            FileWriter writer1 = new FileWriter(new File("output.txt"), true);
            for (int x = 0; x < cheapestIndexes.size(); x++) {
                writer1.write(cheapestChoice.get(cheapestIndexes.get(x)));
            }
            writer1.close();
        }
        catch(IOException ex){
            System.out.println("exception findCheapestMethod");
        }
    }

    public void chooseQuickest(Digraph graph, String commandLine){
        ArrayList<String> quickestChoice = listAll(graph, commandLine);
        ArrayList<Integer> quickestIndexes = new ArrayList<>();
        String timeStr = quickestChoice.get(0).split("\t")[quickestChoice.get(0).split("\t").length-1].split("/")[0];
        int quickestMins = (Integer.parseInt(timeStr.split(":")[0])*60) + Integer.parseInt(timeStr.split(":")[1]);
        for(int x=0;x<quickestChoice.size();x++){
            String newTimeStr = quickestChoice.get(x).split("\t")[quickestChoice.get(x).split("\t").length-1].split("/")[0];
            int newQuickestMins = Integer.parseInt(newTimeStr.split(":")[0])*60 + Integer.parseInt(newTimeStr.split(":")[1]);
            if(newQuickestMins < quickestMins){
                quickestMins = newQuickestMins;
            }
        }
        for(int x=0;x<quickestChoice.size();x++){
            String newTimeStr = quickestChoice.get(x).split("\t")[quickestChoice.get(x).split("\t").length-1].split("/")[0];
            int newQuickestMins = Integer.parseInt(newTimeStr.split(":")[0])*60 + Integer.parseInt(newTimeStr.split(":")[1]);
            if(newQuickestMins == quickestMins){
                quickestIndexes.add(x);
            }
        }
        try{
            FileWriter writer1 = new FileWriter(new File("output.txt"),true);
            for(int x=0;x<quickestIndexes.size();x++){
                writer1.write(quickestChoice.get(quickestIndexes.get(x)));
            }
            writer1.close();
        }
        catch(IOException ex){
            System.out.println("IOException findQuickest method");
        }
    }

    public void listCheaper(Digraph graph, String commandLine){
        String maxPriceStr = commandLine.split("\t")[commandLine.split("\t").length-1];
        String commandLineClearified = "";
        ArrayList<String> suitableFlights = new ArrayList<>();
        for(int i=0;i<commandLine.split("\t").length-1;i++){
            commandLineClearified = commandLineClearified + commandLine.split("\t")[i];
            if(i!=commandLine.split("\t").length-2){
                commandLineClearified = commandLineClearified + "\t";
            }
        }
        ArrayList<String> allFlights = chooseProper(graph, commandLineClearified,false);
        int maxPrice = Integer.parseInt(maxPriceStr);
        for(int x=0;x<allFlights.size();x++){
            String currentPriceStr = allFlights.get(x).split("\t")[allFlights.get(x).split("\t").length-1].split("/")[1];
            currentPriceStr = currentPriceStr.replace("\n","");
            int currentPrice = Integer.parseInt(currentPriceStr);
            if(currentPrice < maxPrice){
                suitableFlights.add(allFlights.get(x));
            }
        }
        try{
            FileWriter writer1 = new FileWriter(new File("output.txt"),true);
            if(suitableFlights.size()==0){
                writer1.write("No suitable flight plan is found\n");
            }
            else {
                for (int x = 0; x < suitableFlights.size(); x++) {
                    writer1.write(suitableFlights.get(x));
                }
            }
            writer1.close();
        }
        catch(IOException ex){
            System.out.println("IOException in listCheaper method");
        }
    }

    public void listQuicker(Digraph graph, String commandLine){
        String commandLineClearified = "";
        String lastDate = commandLine.split("\t")[3];
        for(int x=0;x<commandLine.split("\t").length-1;x++){
            commandLineClearified = commandLineClearified + commandLine.split("\t")[x];
            if(x!=commandLine.split("\t").length-2){
                commandLineClearified = commandLineClearified + "\t";
            }
        }

        ArrayList<String> allFlights = chooseProper(graph, commandLineClearified,false);
        ArrayList<String> quickers = new ArrayList<>();
        String firstFlightStartDate = "";
        for(int x=0;x<allFlights.size();x++){
            String firstFlightDeptName = allFlights.get(x).split("\t")[1].split("->")[0];
            String firstFlightID = allFlights.get(x).split("\t")[0];
            int deptIndex = graph.findAirportIndex(firstFlightDeptName);
            for(int i=0;i<graph.getAirportArray()[deptIndex].getEdgeArray().size();i++){
                if(graph.getAirportArray()[deptIndex].getEdgeArray().get(i).getFlightID().equals(firstFlightID)){
                    firstFlightStartDate = graph.getAirportArray()[deptIndex].getEdgeArray().get(i).getFlightDate();
                }
            }
            String currentFlightDuration = allFlights.get(x).split("\t")[allFlights.get(x).split("\t").length-1].split("/")[0];
            String currentArrivalDate = addDuration(firstFlightStartDate, currentFlightDuration);
            if(!dateSuitable(lastDate,currentArrivalDate)){//compare dates if so add new arraylist
                quickers.add(allFlights.get(x));
            }
        }

        try{
            FileWriter writer1 = new FileWriter(new File("output.txt"),true);
            if(quickers.size()==0){
                writer1.write("No suitable flight plan is found\n");
            }
            else {
                for (int c = 0; c < quickers.size(); c++) {
                    writer1.write(quickers.get(c));
                }
            }
            writer1.close();
        }
        catch(IOException ex){
            System.out.println("Exception in listQuicker Method");
        }
    }

    public void listExcluding(Digraph graph, String commandLine){
        String commandLineClearified = "";
        for(int x=0;x<commandLine.split("\t").length-1;x++){
            commandLineClearified = commandLineClearified + commandLine.split("\t")[x];
            if(x!=commandLine.split("\t").length-2){
                commandLineClearified = commandLineClearified + "\t";
            }
        }
        String companyName = commandLine.split("\t")[commandLine.split("\t").length-1];
        companyName = companyName.replace("\n","");
        ArrayList<String> properFlights = chooseProper(graph,commandLineClearified,false);
        ArrayList<String> listExcluding = new ArrayList<>();
        for(int c=0;c< properFlights.size();c++){
            boolean contains = false;
            if(properFlights.get(c).contains(companyName)){
                contains = true;
            }
            if(!contains){
                listExcluding.add(properFlights.get(c));
            }
        }


        try{
            FileWriter writer1 = new FileWriter(new File("output.txt"),true);
            if(listExcluding.size()!=0) {
                for (int z = 0; z < listExcluding.size(); z++) {
                    writer1.write(listExcluding.get(z));
                }
            }
            else{
                writer1.write("No suitable flight plan is found\n");
            }
            writer1.close();
        }
        catch(IOException ex){
            System.out.println("IOException in listExcluding method");
        }

    }

    public void listOnlyFrom(Digraph graph, String commandLine){
        String commandLineClearified = "";
        for(int x=0;x<commandLine.split("\t").length-1;x++){
            commandLineClearified = commandLineClearified + commandLine.split("\t")[x];
            if(x!=commandLine.split("\t").length-2){
                commandLineClearified = commandLineClearified + "\t";
            }
        }
        String companyName = commandLine.split("\t")[commandLine.split("\t").length-1];
        companyName = companyName.replace("\n","");
        ArrayList<String> properFlights = chooseProper(graph,commandLineClearified,false);
        ArrayList<String> listIncluding = new ArrayList<>();
        for(int c=0;c< properFlights.size();c++){
            boolean containsDifferent = false;
            if(!properFlights.get(c).contains("||")) {
                String currentCompanyName = properFlights.get(c).split("\t")[0].substring(0, 2);
                if(!currentCompanyName.equals(companyName)){
                    containsDifferent=true;
                }
            }
            else{
                String[] lineSplit = properFlights.get(c).split("->");
                for(int v=0;v<lineSplit.length-2;v++){
                    if(v==0){
                        if(!lineSplit[0].substring(0,2).equals(companyName)){
                            containsDifferent = true;
                            break;
                        }
                    }
                    else{
                        if(!lineSplit[v].substring(5,7).equals(companyName)){
                            containsDifferent = true;
                            break;
                        }
                    }
                }
            }
            if(!containsDifferent){
                listIncluding.add(properFlights.get(c));
            }

        }

        try{
            FileWriter writer1 = new FileWriter(new File("output.txt"),true);
            if(listIncluding.size()!=0){
                for (int z = 0; z < listIncluding.size(); z++) {
                    writer1.write(listIncluding.get(z));
                }
            }
            else{
                writer1.write("No suitable flight plan is found\n");
            }
            writer1.close();
        }
        catch(IOException ex){
            System.out.println("IOException in listExcluding method");
        }

    }

    public Airport[] orderByBreadthFirst(Digraph graph){//A helper function for DiameterOfGraph method
        //this method will return the airports(vertices) in ordered way So we can run Dijkstra's algorithm on graph
        Airport[] returnArray = new Airport[graph.getAirportArray().length];
        for(int x=0;x< returnArray.length;x++){
            returnArray[x] = null;//initiate with null
        }
        boolean[] flightExists = new boolean[graph.getAirportArray().length];
        //find the airport which is the top of graph(there is no flight to this airport)
        String currentAirportName = "";
        for(int i=0;i<graph.getAirportArray().length;i++){
            currentAirportName = graph.getAirportArray()[i].getAirportName();
            for(int c=0;c<graph.getAirportArray()[i].getEdgeArray().size();c++){
                String flightDestination = graph.getAirportArray()[i].getEdgeArray().get(c).getArrivalAirport();
                int index = graph.findAirportIndex(flightDestination);
                flightExists[index] = true;
                //now we have marked the array(if there is exist to the airport then same index in our boolean array is true)
                //there should be only one false item in flightExists array which is the top of the graph
                //we will use this to start breadth first search
            }
        }
        int topAirportIndex = -1;
        int falseCounter = 0;
        for(int x=0;x< flightExists.length;x++){
            if(!flightExists[x] & graph.getAirportArray()[x].getEdgeArray().size()!=0){
                topAirportIndex = x;
                falseCounter++;
            }
        }
        //now by breadth first, return a sorted array
        int indexCounter = 1;
        returnArray[0] = graph.getAirportArray()[topAirportIndex];
        for(int x=0;x<returnArray.length;x++){
            if(returnArray[x]!=null) {
                for (int c = 0; c < returnArray[x].getEdgeArray().size(); c++) {
                    String arrivalName = returnArray[x].getEdgeArray().get(c).getArrivalAirport();
                    boolean exist = false;
                    for (int z = 0; z < indexCounter; z++) {
                        if (returnArray[z].getAirportName().equals(arrivalName)) {
                            exist = true;
                        }
                    }
                    if (!exist) {
                        returnArray[indexCounter] = graph.getAirportArray()[graph.findAirportIndex(arrivalName)];
                        indexCounter++;
                    }
                }
            }
        }

        //now we have a breadth first sorted array of airports which is returnArray
        return returnArray;

    }


    private int helperDiameterFindIndex(String airportName, Airport[] airportArr){
        for(int i=0;i<airportArr.length;i++){
            if(airportArr[i].getAirportName().equals(airportName)){
                return i;
            }
        }
        return -1;
    }

    public void diameterOfGraph(Digraph graph){
        Airport[] orderedArray = orderByBreadthFirst(graph);
        int[] distTo = new int[graph.getAirportArray().length];
        int[] edgeTo = new int[distTo.length];//edge to array holds the index of airport where we came from
        boolean[] initialized = new boolean[graph.getAirportArray().length];//to determine if distTo[] array empty or not
        for(int i=0;i<initialized.length;i++){
            initialized[i] = false;//initiate all as false
        }
        for(int x=0;x<orderedArray.length;x++){//for each airport
            if(orderedArray[x]!=null){
                Airport currentAirport = orderedArray[x];
                for (int i = 0; i < currentAirport.getEdgeArray().size(); i++) {//for every flight of that airport
                    Flight currentFlight = currentAirport.getEdgeArray().get(i);
                    String arrivalName = currentFlight.getArrivalAirport();
                    int currentPrice = currentFlight.getPrice();
                    int arrivalIndex = helperDiameterFindIndex(arrivalName, orderedArray);
                    if (!initialized[arrivalIndex]) {//if not initialized then assign currentPrice
                        distTo[arrivalIndex] = distTo[x] + currentPrice;
                        initialized[arrivalIndex] = true;//now its initialized
                        edgeTo[arrivalIndex] = x;
                    } else {
                        if (distTo[x] + currentPrice < distTo[arrivalIndex]) {//if initialized, compare and assign cheaper one
                            distTo[arrivalIndex] = distTo[x] + currentPrice;
                        }
                    }
                }
            }
        }
        //Now the distTo array is filled, choose max from distTo Array
        int max=distTo[0];
        for(int iter:distTo){
            if(iter>max){
                max = iter;
            }
        }

        try{
            FileWriter writer1 = new FileWriter(new File("output.txt"),true);
            writer1.write("The diameter of graph : " + max + "\n");
            writer1.close();
        }
        catch (IOException ex1){
            System.out.println("IOException in diameterGraph method");
        }

    }


    public void commandLineExecuter(String fileName, Digraph graph){
        try {
            Scanner scanner1 = new Scanner(new File(fileName));
            FileWriter writer1;
            while(scanner1.hasNextLine()){
               String line = scanner1.nextLine();
               String command = line.split("\t")[0];

               if(command.equals("listAll")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                    writer1.write("command : " + line + "\n");
                    writer1.close();
                    printListAllOutput(graph, line);
                    writer1 = new FileWriter(new File("output.txt"),true);
                    writer1.write("\n\n");
                    writer1.close();
               }
               else if(command.equals("listProper")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.close();
                   chooseProper(graph, line, true);
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("\n\n");
                   writer1.close();
               }
               else if(command.equals("listCheapest")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.close();
                   chooseCheapest(graph, line);
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("\n\n");
                   writer1.close();
               }
               else if(command.equals("listQuickest")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.close();
                   chooseQuickest(graph, line);
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("\n\n");
                   writer1.close();
               }
               else if(command.equals("listCheaper")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.close();
                   listCheaper(graph, line);
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("\n\n");
                   writer1.close();
               }
               else if(command.equals("listQuicker")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.close();
                   listQuicker(graph, line);
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("\n\n");
                   writer1.close();
               }
               else if(command.equals("listExcluding")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.close();
                   listExcluding(graph, line);
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("\n\n");
                   writer1.close();
               }
               else if(command.equals("listOnlyFrom")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.close();
                   listOnlyFrom(graph, line);
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("\n\n");
                   writer1.close();
               }
               else if(command.equals("diameterOfGraph")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.close();
                   diameterOfGraph(graph);
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("\n\n");
                   writer1.close();
               }
               else if(command.equals("pageRankOfNodes")){
                   writer1 = new FileWriter(new File("output.txt"),true);
                   writer1.write("command : " + line + "\n");
                   writer1.write("Not implemented");
                   writer1.close();
               }
               else{
                   System.out.println("error");
               }
            }
        }
        catch(IOException ex){
            System.out.println("IOException while opening commands.txt");
        }
    }




}




public class Main {
    public static void main(String[] args){

        ClientManager manager1 = new ClientManager();
        Digraph wholeGraph = manager1.readCreateAirports(args[0]);
        manager1.readAddEdges(args[1], wholeGraph);
        manager1.commandLineExecuter(args[2], wholeGraph);


    }
}
