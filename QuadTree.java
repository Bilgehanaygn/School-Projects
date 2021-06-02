import java.util.*;


public class QuadTree {
    public QTreeNode root;
    private String imageRoot;

    public QuadTree(String imageRoot) {
        // Instantiate the root element of the tree with depth 0
        // Use the ROOT_ULLAT, ROOT_ULLON, ROOT_LRLAT, ROOT_LRLON static variables of MapServer class
        // Call the build method with depth 1
        // Save the imageRoot value to the instance variable
        /* Code here */
        this.root = new QTreeNode("root", MapServer.ROOT_ULLAT, MapServer.ROOT_ULLON, MapServer.ROOT_LRLAT, MapServer.ROOT_LRLON,
                0);
        this.imageRoot = imageRoot;
        build(this.root, 1);
    }


    public void build(QTreeNode subTreeRoot, int depth) {
        // Recursive method to build the tree as instructed
        /* Code here */

        if(depth == 7){
            return;
        }
        else{
            String parentName = "";
            if(!subTreeRoot.getName().equals("root") && !subTreeRoot.getName().equals("img/root")){
                parentName = subTreeRoot.getName();
            }

            subTreeRoot.NW = new QTreeNode(parentName + "1",
                    subTreeRoot.getUpperLeftLatitude(),
                    subTreeRoot.getUpperLeftLongtitude(),
                    subTreeRoot.getLowerRightLatitude() + (subTreeRoot.getUpperLeftLatitude() - subTreeRoot.getLowerRightLatitude())/2,
                    subTreeRoot.getUpperLeftLongtitude() + (-subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude())/2,
                    subTreeRoot.getDepth() + 1);

            subTreeRoot.NE = new QTreeNode(parentName + "2",
                    subTreeRoot.getUpperLeftLatitude(),
                    subTreeRoot.getUpperLeftLongtitude() + (-subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude())/2,
                    subTreeRoot.getLowerRightLatitude() + (subTreeRoot.getUpperLeftLatitude() - subTreeRoot.getLowerRightLatitude())/2,
                    subTreeRoot.getLowerRightLongtitude(),
                    subTreeRoot.getDepth() +1);

            subTreeRoot.SW = new QTreeNode(parentName + "3",
                    subTreeRoot.getLowerRightLatitude() + (subTreeRoot.getUpperLeftLatitude()-subTreeRoot.getLowerRightLatitude())/2,
                    subTreeRoot.getUpperLeftLongtitude(),
                    subTreeRoot.getLowerRightLatitude(),
                    subTreeRoot.getUpperLeftLongtitude() + (-subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude())/2,
                    subTreeRoot.getDepth() + 1);

            subTreeRoot.SE = new QTreeNode(parentName + "4",
                    subTreeRoot.getLowerRightLatitude() + (subTreeRoot.getUpperLeftLatitude() - subTreeRoot.getLowerRightLatitude())/2,
                    subTreeRoot.getUpperLeftLongtitude() + (-subTreeRoot.getUpperLeftLongtitude() + subTreeRoot.getLowerRightLongtitude())/2,
                    subTreeRoot.getLowerRightLatitude(),
                    subTreeRoot.getLowerRightLongtitude(),
                    subTreeRoot.getDepth() + 1);
        }
        build(subTreeRoot.NW, subTreeRoot.getDepth()+1);
        build(subTreeRoot.NE, subTreeRoot.getDepth()+1);
        build(subTreeRoot.SW, subTreeRoot.getDepth()+1);
        build(subTreeRoot.SE, subTreeRoot.getDepth()+1);

    }


    public Map<String, Object> search(Map<String, Double> params) {
        /*
         * Parameters are:
         * "ullat": Upper left latitude of the query box
         * "ullon": Upper left longitude of the query box
         * "lrlat": Lower right latitude of the query box
         * "lrlon": Lower right longitude of the query box
         * */

        // Instantiate a QTreeNode to represent the query box defined by the parameters
        // Calculate the lonDpp value of the query box
        // Call the search() method with the query box and the lonDpp value
        // Call and return the result of the getMap() method to return the acquired nodes in an appropriate way
        /* Code here */

        QTreeNode queryBoxNode = new QTreeNode("queryBox", params.get("ullat"), params.get("ullon"), params.get("lrlat"), params.get("lrlon"),0);
        Double lonDppQuery = (params.get("lrlon") - params.get("ullon"))/params.get("w");
        ArrayList<QTreeNode> intersects = new ArrayList<>();
        search(queryBoxNode, this.root, lonDppQuery, intersects);
        Map<String, Object> returnMap = getMap(intersects);
        return returnMap;
    }

    private Map<String, Object> getMap(ArrayList<QTreeNode> list) {
        Map<String, Object> map = new HashMap<>();

        // Check if the root intersects with the given query box
        if (list.contains(this.root)) {
            map.put("query_success", false);
            return map;
        }
        String[][] imagesArray = get2D(list);
        // Use the get2D() method to get organized images in a 2D array
        map.put("render_grid", imagesArray/* Code here */);

        // Upper left latitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_ul_lat", findNodeByName(imagesArray[0][0], list).getUpperLeftLatitude()/* Code here */);

        // Upper left longitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_ul_lon", findNodeByName(imagesArray[0][0], list).getUpperLeftLongtitude()/* Code here */);

        // Upper lower right latitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_lr_lat", findNodeByName(imagesArray[imagesArray.length-1][imagesArray[0].length-1], list).getLowerRightLatitude()/* Code here */);

        // Upper lower right longitude of the retrieved grid (Imagine the
        // 2D string array you have constructed as a big picture)
        map.put("raster_lr_lon", findNodeByName(imagesArray[imagesArray.length-1][imagesArray[0].length-1], list).getLowerRightLongtitude()/* Code here */);

        // Depth of the grid (can be thought as the depth of a single image)
        map.put("depth", list.get(0).getDepth()/* Code here */);

        map.put("query_success", true);
        return map;
    }

    private QTreeNode findNodeByName(String name, ArrayList<QTreeNode> list){
        for(QTreeNode node: list){
            if(name.equals("img/" + node.getName() + ".png")){
                return node;
            }
        }
        return null;
    }


    private String[][] get2D(ArrayList<QTreeNode> list) {

        // After you retrieve the list of images using the recursive search mechanism described above, you
        // should order them as a grid. This grid is nothing more than a 2D array of file names. To order
        // the images, you should determine correct row and column for each image (node) in the retrieved
        // list. As a hint, you should consider the latitude values of images to place them in the row, and
        // the file names of the images to place them in a column.
        /* Code here */
        Map<Double, ArrayList<QTreeNode>> latMap = new TreeMap<>();
        for(QTreeNode iterNode : list){
            if(!latMap.containsKey(iterNode.getUpperLeftLatitude())){
                latMap.put(iterNode.getUpperLeftLatitude(), new ArrayList<>());
                latMap.get(iterNode.getUpperLeftLatitude()).add(iterNode);
            }
            else{
                latMap.get(iterNode.getUpperLeftLatitude()).add(iterNode);
            }
        }
        int rowSize = latMap.size();
        int columnSize = list.size()/rowSize;



        //initialize 2D string array
        String[][] images = new String[rowSize][columnSize];


        ArrayList<Double> latOrderedList = returnMaxLat(latMap);
        for(int i=0;i<latMap.size();i++){
            ArrayList<QTreeNode> orderedList = nameOrderedList(latOrderedList, latMap);

            for(int x=0;x<columnSize;x++){
                images[i][x] = "img/" + orderedList.get(x).getName() + ".png";
            }

        }

        return images;
    }

    //returns an arraylist in decreasing order of latitudes
    private ArrayList<Double> returnMaxLat(Map<Double, ArrayList<QTreeNode>> treeMap){
        ArrayList<Double> orderedKeys = new ArrayList<>();

        for (Map.Entry<Double, ArrayList<QTreeNode>> entry : treeMap.entrySet()) {
            orderedKeys.add(entry.getKey());
        }
        //sort the list
        Collections.sort(orderedKeys);
        Collections.reverse(orderedKeys);
        return orderedKeys;
    }


    //returns list of images in increasing order of name which has the same latitude
    private ArrayList<QTreeNode> nameOrderedList(ArrayList<Double> latOrderedList, Map<Double, ArrayList<QTreeNode>> treeMap){
        // return arraylist sorted by increasing order of ullon

        ArrayList<QTreeNode> orderedList = new ArrayList<>();
        ArrayList<QTreeNode> originalList = new ArrayList<>();
        for(QTreeNode node: treeMap.get(latOrderedList.get(0))){
            originalList.add(node);
        }

        while(!originalList.isEmpty()){
            int min = Integer.parseInt(originalList.get(0).getName());
            for(QTreeNode node: originalList){
                if(Integer.parseInt(node.getName()) < min){
                    min = Integer.parseInt(node.getName());
                }
            }
            for(QTreeNode node: originalList){
                if(Integer.parseInt(node.getName()) == min){
                    orderedList.add(node);
                    originalList.remove(node);
                    break;
                }
            }


        }

        latOrderedList.remove(0);

        return orderedList;

    }


    public void search(QTreeNode queryBox, QTreeNode tile, double lonDpp, ArrayList<QTreeNode> list) {
        // The first part includes a recursive search in the tree. This process should consider both the
        // lonDPP property (discussed above) and if the images in the tree intersect with the query box.
        // (To check the intersection of two tiles, you should use the checkIntersection() method)
        // To achieve this, you should retrieve the first depth (zoom level) of the images which intersect
        // with the query box and have a lower lonDPP than the query box.
        // This method should fill the list given by the "ArrayList<QTreeNode> list" parameter
        /* Code here */

        if(checkIntersection(tile, queryBox)){
            if(tile.getLonDPP() <= lonDpp){
                list.add(tile);
                return;
            }
            else {
                if (tile.NW != null) {
                    search(queryBox, tile.NW, lonDpp, list);
                }
                if (tile.NE != null) {
                    search(queryBox, tile.NE, lonDpp, list);
                }
                if (tile.SW != null) {
                    search(queryBox, tile.SW, lonDpp, list);
                }
                if (tile.SE != null) {
                    search(queryBox, tile.SE, lonDpp, list);
                }
            }

        }


    }

    public boolean checkIntersection(QTreeNode tile, QTreeNode queryBox) {
        // Return true if two tiles are intersecting with each other
        /* Code here */

        if(tile.getUpperLeftLongtitude() > queryBox.getLowerRightLongtitude()){
            return false;
        }
        if(tile.getLowerRightLongtitude() < queryBox.getUpperLeftLongtitude()){
            return false;
        }
        if(tile.getLowerRightLatitude() > queryBox.getUpperLeftLatitude()){
            return false;
        }
        if(tile.getUpperLeftLatitude() < queryBox.getLowerRightLatitude()){
            return false;
        }

        return true;
    }
}