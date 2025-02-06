package Structures.Quadtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class QuadTrees {
    public static final int CHECKPOINT = 10;

    public static final double MAX_BOUND_X = 2000;
    public static final double MAX_BOUND_Y = 2000;

    //ADDED FOR CONVIENCE: USED TO DISTINGUISH BETWEEN THE DIFFERENT SIDES OF THE RECTANGLE FOR THE SUBDIVISION
    private static final int[] regionXIdx = {0, 2, 2, 0};
    private static final int[] regionYIdx = {1, 1, 3, 3};

    public QuadNode root;
    public int op = 0;

    //QUADTREE OPERATIONS
    public void insertNode(double[] vals){
        if(root == null){
            root = new QuadNode(vals, MAX_BOUND_X, MAX_BOUND_Y);
            root.addVal(vals);
            return;
        }
        
        insertNode(root, vals);
        op++;

        // checkOp();
    }
    public void deleteNode(double[] vals){
        if(root == null || !contains(root, vals)){
            return;
        }

        deleteNode(root, null, vals);
        op++;

        // checkOp();
    }
    public double[] nearestNeighbor(double[] vals){
        return nearestNeighbor(root, vals, null, Double.MAX_VALUE);
    }
    public int rangeQuery(double[][] bounds){
        return rangeQuery(root, bounds);
    }

    //QUADTREE INSERTION
    private void insertNode(QuadNode node, double[] vals){
        if(!inBounds(node.getRect(), vals)){
            // System.out.println("====");
            return;
        }
        node.setAmt(node.getAmt() + 1);

        if(node.getVal().size() < QuadNode.CAPACITY){
            node.addVal(vals);
            return;
        }

        double[] mids = getMid(node);
        double[] rect = node.getRect();

        if(!node.getDivide()){
            node.setDivideTrue();
            for (double[] e : node.getVal()) {
                add(mids[0], mids[1], e, rect, node);
            }
        }
        add(mids[0], mids[1], vals, rect, node);
    }

    //QUADTREE DELETION
    private QuadNode deleteNode(QuadNode node, QuadNode par, double[] vals){
        if(node == null || node.getAmt() == 0){
            return null;
        }

        int contain = contains(node.getVal(), vals);

        if(contain != -1 && !node.getDivide()){
            node.getVal().remove(contain);
            if(par != null){
                int sum = 0;
                for(QuadNode e : par.getChildren()){
                    if(e == null){
                        continue;
                    }
                    sum += e.getVal().size();
                }
                if(sum <= QuadNode.CAPACITY){
                    par.setDivideFalse();
                    par.clearVal();
                    for(QuadNode e1 : par.getChildren()){
                        if(e1 == null){
                            continue;
                        }
                        for(double[] e2 : e1.getVal()){
                            par.addVal(e2);
                        }
                        e1.clearVal();
                    }
                }
            }
        }else{
            if(contain != -1){
                node.getVal().remove(contain);
            }

            double[] mids = getMid(node);
            int idx = findRegion(vals, mids[0], mids[1]);

            deleteNode(node.getChildren()[idx], node, vals);
        }
        int sum = 0;
        for(QuadNode e : node.getChildren()){
            if(e == null){
                continue;
            }
            if(e.getVal().size() > 0){
                sum += e.getAmt();
            }
        }  
        node.setAmt(sum);
        return node;
    }

    //QUADTREE NEARESTNEIGHBOR
    private double[] nearestNeighbor(QuadNode node, double[] vals, double[] bestNode, double bestDist){
        if(node == null || node.getAmt() == 0){
            return bestNode;
        }

        double[] curBest = minEucDist(node.getVal(), vals);
        double curDist = eucDist(curBest, vals);

        if(bestNode == null || curDist < bestDist){
            bestNode = curBest;
            bestDist = curDist;
        }
        // System.out.println("==" + Arrays.toString(bestNode) +" "  +bestDist);

        if(!node.getDivide()){
            for (int i = 0; i < node.getVal().size(); i++) {
                curDist = eucDist(node.getVal().get(i), vals);
                if(curDist < bestDist){
                    bestNode = node.getVal().get(i);
                    bestDist = curDist;
                }
            }
        
            return bestNode;
        }

        double[] rect = node.getRect();
        double xMid = (rect[0] + rect[2])/2; double yMid = (rect[1] + rect[3])/2;
        int idx = findRegion(vals, xMid, yMid);

        bestNode = nearestNeighbor(node.getChildren()[idx], vals, bestNode, bestDist);
        bestDist = eucDist(bestNode, vals);

        if(idx == 0){
            if(Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[1], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
            if(Math.abs(yMid - vals[1]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[3], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
            if(Math.abs(yMid - vals[1]) < bestDist || Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[2], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
        }else if(idx == 1){
            if(Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[0], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
            if(Math.abs(yMid - vals[1]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[2], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
            if(Math.abs(yMid - vals[1]) < bestDist || Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[3], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
        }else if(idx == 2){
            if(Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[3], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
            if(Math.abs(yMid - vals[1]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[1], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
            if(Math.abs(yMid - vals[1]) < bestDist || Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[0], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
        }else{
            if(Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[2], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
            if(Math.abs(yMid - vals[1]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[0], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
            if(Math.abs(yMid - vals[1]) < bestDist || Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[1], vals, bestNode, bestDist);
                bestDist = eucDist(bestNode, vals);
            }
        }
        return bestNode;
    }

    //QUADTREE RANGE QUERY
    private int rangeQuery(QuadNode node, double[][] bounds){
        int count = 0;
        double[] rect = node.getRect();

        if(in(new double[][]{{rect[0], rect[2]}, {rect[3], rect[1]}}, bounds)){
            // System.out.println(Arrays.toString(rect) + " " + node.getAmt());
            return node.getAmt();
        }

        if(!node.getDivide()){
            for(double[] e : node.getVal()){
                boolean inside = true;
                for (int i = 0; i < e.length; i++) {
                    if(!(bounds[i][0] <= e[i] && e[i] <= bounds[i][1])){
                        inside = false;
                    }
                }
                if(inside){
                    count++;
                }
            }
            return count;
        }
        
        for(QuadNode e : node.getChildren()){
            if(e == null){
                continue;
            }
            double[] childRect = e.getRect();
            if(intersect(new double[][]{{childRect[0], childRect[2]}, {childRect[3], childRect[1]}}, bounds)){
                count += rangeQuery(e, bounds);
            }
        }

        return count;
    }

    //HELPER FUNCTIONS
    //CHECKS TO SEE IF VALS LIES INSIDEOF RECT
    private boolean inBounds(double[] rect, double[] vals){
        return rect[0] <= vals[0] && vals[0] <= rect[2] && rect[3] <= vals[1] && vals[1] <= rect[1];
    }

    //CHECKS THE REGION AND WHETHER YOU CAN ADD INTO THE REGION
    private void add(double xMid, double yMid, double[] vals, double[] rect, QuadNode node){
        int idx = findRegion(vals, xMid, yMid);

        QuadNode next = node.getChildren()[idx];
        if(next == null){
            double[] center = new double[]{(rect[regionXIdx[idx]] + xMid)/2, (rect[regionYIdx[idx]] + yMid)/2};
            node.setChildren(new QuadNode(center, node.getLength()/2, node.getHeight()/2), idx);
        }
        insertNode(node.getChildren()[idx], vals);
    }

    //FIND THE REGION WHERE VAL IS LOCATED GIVEN THE MIDDLE X AND MIDDLE Y
    private int findRegion(double[] vals, double xMid, double yMid){
        boolean left = vals[0] <= xMid;
        boolean top = vals[1] >= yMid;

        //CHECK EACH REGION
        if(left && top){
            return 0;
        }else if(!left && top){
            return 1;
        }else if(!left && !top){
            return 2;
        }else{
            return 3;
        }
    }

    //USED TO CHECK IF VALS IS CONTAINED INSIDE ARR
    private int contains(ArrayList<double[]> arr, double[] vals){
        for (int i = 0; i < arr.size(); i++) {
            if(arr.get(i)[0] == vals[0] && arr.get(i)[1] == vals[1]){
                return i;
            }
        }
        return -1;
    }
    
    //USED TO CHECK IF VALS IS IS THE NODE REGION
    public boolean contains(QuadNode node, double[] vals){
        if(node == null){
            return false;
        }
        if(contains(node.getVal(), vals) != -1){
            return true;
        }else{
            double[] mids = getMid(node);
            int idx = findRegion(vals, mids[0], mids[1]);

            return contains(node.getChildren()[idx], vals);
        }
    }
    
    //USED TO CHECK IF ARR1 IS CONTAINED INSIDE OF ARR2
    private boolean in(double[][] arr1, double[][] arr2){
        for (int i = 0; i < arr1.length; i++) {
            if(!(arr2[i][0] <= arr1[i][0] && arr1[i][1] <= arr2[i][1])){
                return false;
            }
        }
        return true;
    }

    //USED TO CHECK IF ARR1 INTERSECTS WITH ARR2 (SAME THING VICE VERSA)
    private boolean intersect(double[][] arr1, double[][] arr2){
        for (int i = 0; i < arr1.length; i++) {
            if(Math.max(arr1[i][0], arr2[i][0]) > Math.min(arr1[i][1], arr2[i][1])){
                return false;
            }
        }
        return true;
    }
    
    //USED TO FIND THE X-MID AND Y-MID OF A NODE REGION
    private double[] getMid(QuadNode node){
        double[] rect = node.getRect();
        double xMid = (rect[0] + rect[2])/2;
        double yMid = (rect[1] + rect[3])/2;
        return new double[]{xMid, yMid};
    }
    
    //FINDS THE MINIMUM DISTANCE INSIDE A REGION TO A POINT
    private double[] minEucDist(ArrayList<double[]> points, double[] val){
        double minDist = Double.MAX_VALUE;
        int idx = 0;
        for(int i = 0; i < points.size(); i++){
            double curDistt = eucDist(points.get(i), val);
            if(curDistt < minDist){
                minDist = curDistt;
                idx = i;
            }
        }
        return points.get(idx);
    }
    
    //FINDS THE EUCLIDEAN DISTANCE BETWEEN TWO POINTS
    public double eucDist(double[] p1, double[] p2){
        double dist = 0;
        for (int i = 0; i < p1.length; i++) {
            dist += (p1[i] - p2[i]) * (p1[i] - p2[i]);
        }
        dist = Math.sqrt(dist);
        return dist;
    }

    //REBUILD OPERATION
    private void checkOp(){
        if(op % CHECKPOINT == 0){
            ArrayList<double[]> points = collectPoints();
            rebalanceTree(points);
        }
    }

    //RETURNING ALL THE POINTS INSIDE THE QUADTREE
    private ArrayList<double[]> collectPoints(){
        ArrayList<double[]> totalPoints = new ArrayList<>();

        Stack<QuadNode> cur = new Stack<>();
        cur.add(root);

        while(!cur.isEmpty()){
            QuadNode x = cur.pop();
            
            if(!x.getDivide()){
                for(double[] e : x.getVal()){
                    totalPoints.add(e);
                }
            }else{
                for(QuadNode e : x.getChildren()){
                    if(e == null){
                        continue;
                    }
                    cur.add(e);
                }
            }
        }
        return totalPoints;
    }
    private QuadNode rebalanceTree(ArrayList<double[]> points){
        double[] med = quickSelect(points, points.size()/2, 0);
        root = new QuadNode(med, MAX_BOUND_X, MAX_BOUND_Y);

        for(double[] e : points){
            insertNode(root, e);
        }
        return root;
    }   
    private double[] quickSelect(ArrayList<double[]> points, int k, int idx){
        if(points.size() == 1){
            return points.get(0);
        }

        double[] pivot = points.get(points.size()/2);

        ArrayList<double[]> lowPoints = new ArrayList<>();
        ArrayList<double[]> highPoints = new ArrayList<>();
        ArrayList<double[]> equalPoints = new ArrayList<>();

        for(double[] e : points){
            if(e[idx] < pivot[idx]){
                lowPoints.add(e);
            }else if(e[idx] == pivot[idx]){
                equalPoints.add(e);
            }else{
                highPoints.add(e);
            }
        }
        if(k < lowPoints.size()){
            return quickSelect(lowPoints, k, idx);
        }else if(k < lowPoints.size() + equalPoints.size()){
            return pivot;
        }else{
            return quickSelect(highPoints, k - lowPoints.size() - equalPoints.size(), idx);
        }
    }
}