package Structures.KDTree;

import java.util.*;
import java.io.*;

public class KDTrees{  
    public static final int CHECKPOINT = 10;

    private KDNode root;  
    private int total = 0;
    private int op = 0;

    //KD TREE OPERATIONS
    public void insertNode(double[] vals){
        if(findNode(vals)){ //IF IT ALREADY EXISTS YOU DON'T NEED TO ADD IT
            return;
        }
        KDNode node = insertNode(root, vals, 0);
        if(root == null){
            root = node;
        }
        op++;
        total++;

        // checkOp();
    }
    public void deleteNode(double[] vals){
        if(!findNode(vals)){
            return;
        }
        deleteNode(root, vals, 0);
        if(total == 1){
            root = null;
        }
        total--;
        op++;
        
        // checkOp();
    }
    public boolean findNode(double[] vals){
        return findNode(root, vals, 0);
    }
    public double[] nearestNeighbor(double[] vals){
        return nearestNeighbor(root, vals, null, Double.MAX_VALUE, 0).getVal();
    }
    public int rangeQuery(double[][] vals){
        double[][] bounds = new double[KDNode.DIMENSIONS][2];
        
        for (int i = 0; i < bounds.length; i++) {
            bounds[i][0] = findMin(root, 0, i)[i];
            bounds[i][1] = findMax(root, 0, i)[i];
        }

        return rangeQuery(root, vals, bounds, 0);
    }

    //NODE INSERTION
    private KDNode insertNode(KDNode node, double[] vals, int depth){
        if(node == null){
            return new KDNode(vals);
        }

        int curDim = depth % KDNode.DIMENSIONS;
        node.setAmt(node.getAmt() + 1);

        if(vals[curDim] <= node.getVal()[curDim]){
            node.setLeft(insertNode(node.getLeft(), vals, depth + 1));
        }else{
            node.setRight(insertNode(node.getRight(), vals, depth + 1));
        }

        return node;
    }

    //NODE DELETION
    private KDNode deleteNode(KDNode node, double[] vals, int depth){
        if(node == null){
            return null;
        }

        int curDim = depth % KDNode.DIMENSIONS;

        if(isEquals(node.getVal(), vals)){
            if(node.getLeft() == null && node.getRight() == null){
                return null;
            }
            if(node.getLeft() == null){
                double[] min = findMin(node.getRight(), depth + 1, curDim);
                node.setVals(min);

                node.setRight(deleteNode(node.getRight(), min, depth + 1));
            }else{
                double[] max = findMax(node.getLeft(), depth + 1, curDim);
                node.setVals(max);
                
                node.setLeft(deleteNode(node.getLeft(), max, depth + 1));
            }   
        }else{
            if(vals[curDim] <= node.getVal()[curDim]){
                deleteNode(node.getLeft(), vals, depth + 1);
            }else{
                deleteNode(node.getRight(), vals, depth + 1);
            }
        }
        node.setAmt(((node.getLeft() == null) ? 0 : node.getLeft().getAmt()) + ((node.getRight() == null) ? 0 : node.getRight().getAmt()) + 1);
        return node;
    }

    //NODE DETECTION
    private boolean findNode(KDNode node, double[] vals, int depth){
        if(node == null){
            return false;
        }
        if(isEquals(node.getVal(), vals)){
            return true;
        }

        int curDim = depth % KDNode.DIMENSIONS;

        if(vals[curDim] <= node.getVal()[curDim]){
            return findNode(node.getLeft(), vals, depth + 1);
        }else{
            return findNode(node.getRight(), vals, depth + 1);
        }
    }

    //NEAREST NEIGHBOR
    private KDNode nearestNeighbor(KDNode node, double[] vals, KDNode bestNode, double bestDist, int depth){        
        if(node == null){
            return bestNode;
        }

        double curDist = eucDistance(node.getVal(), vals);

        if(bestNode == null || curDist < bestDist){
            bestNode = node;
            bestDist = curDist;
        }

        int curDim = depth % KDNode.DIMENSIONS;
        KDNode branchOne = null; 
        KDNode branchTwo = null;

        if(vals[curDim] <= node.getVal()[curDim]){
            branchOne = node.getLeft();
            branchTwo = node.getRight();
        }else{
            branchOne = node.getRight();
            branchTwo = node.getLeft();
        }

        bestNode = nearestNeighbor(branchOne, vals, bestNode, bestDist, depth + 1);
        bestDist = eucDistance(vals, bestNode.getVal());

        if(Math.abs(node.getVal()[curDim] - vals[curDim]) < bestDist){
            bestNode = nearestNeighbor(branchTwo, vals, bestNode, bestDist, depth + 1);
            bestDist = eucDistance(vals, bestNode.getVal());
        }
        return bestNode;
    }
    
    //RANGE QUERIES
    private int rangeQuery(KDNode node, double[][] vals, double[][] bounds, int depth){
        if(node == null){
            return 0;
        }

        int count = 0;
        int curDim = depth % KDNode.DIMENSIONS;

        if(contains(bounds, vals)){
            return node.getAmt();
        }
        
        //CHECK YOUR LEFT
        if(vals[curDim][0] <= node.getVal()[curDim]){
            double original = bounds[curDim][1];
            bounds[curDim][1] = node.getVal()[curDim];
            count += rangeQuery(node.getLeft(), vals, bounds, depth + 1);
            bounds[curDim][1] = original;
        }

        //CHECK YOUR RIGHT
        if(node.getVal()[curDim] <= vals[curDim][1]){
            double original = bounds[curDim][0];
            bounds[curDim][0] = node.getVal()[curDim] + 1;
            count += rangeQuery(node.getRight(), vals, bounds, depth + 1);
            bounds[curDim][0] = original;
        }

        //CHECK YOUR NODE
        if(contains(node.getVal(), vals)){
            count++;
        }
        return count;
    }

    //HELPER FUNCTIONS
    private boolean isEquals(double[] a, double[] b){
        assert a.length == b.length;
        
        for (int i = 0; i < a.length; i++) {
            if(a[i] != b[i]){
                return false;
            }
        }
        return true;
    }
    public double eucDistance(double[] a, double[] b){
        assert a.length == b.length;

        double dist = 0;
        for (int i = 0; i < a.length; i++) {
            dist += (a[i] - b[i]) * (a[i] - b[i]);
        }
        dist = Math.sqrt(dist);
        return dist;
    }
    private List<double[]> collectPoints(){
        ArrayList<double[]> cur = new ArrayList<>();
        Stack<KDNode> dfs = new Stack<>();
            dfs.add(root);

        while(!dfs.isEmpty()){
            KDNode x = dfs.pop();

            if(x == null){
                continue;
            }
            
            cur.add(x.getVal());
            dfs.add(x.getRight());
            dfs.add(x.getLeft());
        }

        return cur;
    }
    private KDNode rebalanceTree(List<double[]> points, int depth){
        if(points.isEmpty()){
            return null;
        }

        int medianIdx = points.size()/2;

        int curDim = depth % KDNode.DIMENSIONS;
        double[] medianPoint = quickSelect(points, medianIdx, curDim);
        KDNode median = new KDNode(medianPoint);

        List<double[]> leftPoints = new ArrayList<>();
        List<double[]> rightPoints = new ArrayList<>();
        
        for(double[] e : points){
            if(isEquals(e, median.getVal())){
                continue;
            }
            if(e[curDim] <= median.getVal()[curDim]){
                leftPoints.add(e);
            }else{
                rightPoints.add(e);
            }
        }

        KDNode left = rebalanceTree(leftPoints, depth + 1);
        KDNode right = rebalanceTree(rightPoints, depth + 1);

        median.setLeft(left);
        median.setRight(right);
        median.setAmt(leftPoints.size() + rightPoints.size() + 1);

        return median;
    }
    private double[] quickSelect(List<double[]> points, int k, int curDim){
        if(points.size() == 1){
            return points.get(0);
        }

        double[] pivot = points.get(points.size()/2);
        List<double[]> lowPoints = new ArrayList<>();
        List<double[]> highPoints = new ArrayList<>();
        List<double[]> equalPoints = new ArrayList<>();

        for(double[] e : points){
            if(e[curDim] < pivot[curDim]){
                lowPoints.add(e);
            }else if(e[curDim] == pivot[curDim]){
                equalPoints.add(e);
            }else{
                highPoints.add(e);
            }
        }
        if(k < lowPoints.size()){
            return quickSelect(lowPoints, k, curDim);
        }else if(k < lowPoints.size() + equalPoints.size()){
            return pivot;
        }else{
            return quickSelect(highPoints, k - lowPoints.size() - equalPoints.size(), curDim);
        }
    }
    private double[] findMax(KDNode node, int depth, int axis){
        int curDim = depth % KDNode.DIMENSIONS;

        if(node == null){
            return null;
        }
        if(curDim == axis){
            if(node.getRight() == null){
                return node.getVal();
            }else{
                return findMax(node.getRight(), depth + 1, axis);
            }
        }else{
            double[] leftMax = findMax(node.getLeft(), depth + 1, axis);
            double[] rightMax = findMax(node.getRight(), depth + 1, axis);
            double[] curMax = node.getVal();

            return maxPoint(maxPoint(leftMax, rightMax, axis), curMax, axis);
        }
    }
    private double[] findMin(KDNode node, int depth, int axis){
        int curDim = depth % KDNode.DIMENSIONS;

        if(node == null){
            return null;
        }
        if(curDim == axis){
            if(node.getLeft() == null){
                return node.getVal();
            }else{
                return findMin(node.getLeft(), depth + 1, axis);
            }
        }else{
            double[] leftMin = findMin(node.getLeft(), depth + 1, axis);
            double[] rightMin = findMin(node.getRight(), depth + 1, axis);
            double[] curMin = node.getVal();

            return minPoint(minPoint(leftMin, rightMin, axis), curMin, axis);
        }
    }
    private double[] maxPoint(double[] a, double[] b, int axis){
        if(a == null){
            return b;
        }else if(b == null){
            return a;
        }
        if(a[axis] > b[axis]){
            return a;
        }else{
            return b;
        }
    }
    private double[] minPoint(double[] a, double[] b, int axis){
        if(a == null){
            return b;
        }else if(b == null){
            return a;
        }
        if(a[axis] < b[axis]){
            return a;
        }else{
            return b;
        }
    }
    private void checkOp(){
        if(op % CHECKPOINT == 0){
            List<double[]> points = collectPoints();
            root = rebalanceTree(points, 0);
        }
    }
    private boolean contains(double[][] a, double[][] b){ //IF A FOLLOWS ALL THE VALUES OF B
        for (int i = 0; i < a.length; i++) {
            if(!(b[i][0] <= a[i][0] && a[i][1] <= b[i][1])){
                return false;
            }
        }
        return true;
    }
    private boolean contains(double[] a, double[][] b){
        for (int i = 0; i < b.length; i++) {
            if(!(b[i][0] <= a[i] && a[i] <= b[i][1])){
                return false;
            }
        }
        return true;
    }
    public void getTree(){
        Stack<KDNode> cur = new Stack<>();
            cur.add(root);
        while(!cur.isEmpty()){
            KDNode node = cur.pop();
            if(node == null){
                System.out.println(node);
                continue;
            }

            System.out.println(Arrays.toString(node.getVal()) + " " + node.getAmt());
            cur.add(node.getRight());
            cur.add(node.getLeft());
        }
    }
}