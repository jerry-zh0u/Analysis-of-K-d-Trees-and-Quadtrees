package Structures;

import java.util.*;
import java.io.*;

public class KDTrees{  
    private KDNode root;  
    private int total = 0;
    private int op = 0;

    //KD TREE OPERATIONS
    public void insertNode(int[] vals){
        if(findNode(vals)){ //IF IT ALREADY EXISTS YOU DON'T NEED TO ADD IT
            return;
        }
        KDNode node = insertNode(root, vals, 0);
        if(root == null){
            root = node;
        }
        op++;
        total++;

        checkOp();
    }
    public void deleteNode(int[] vals){
        deleteNode(root, vals, 0);
        if(total == 0){
            root = null;
        }
        total--;
        op++;
        
        checkOp();
    }
    public boolean findNode(int[] vals){
        return findNode(root, vals, 0);
    }
    public KDNode nearestNeighbor(int[] vals){
        return nearestNeighbor(root, vals, null, Double.MAX_VALUE, 0);
    }

    //NODE INSERTION
    private KDNode insertNode(KDNode node, int[] vals, int depth){
        if(node == null){
            return new KDNode(vals);
        }

        int curDim = depth % KDNode.DIMENSIONS;

        if(vals[curDim] <= node.getVal()[curDim]){
            node.setLeft(insertNode(node.getLeft(), vals, depth + 1));
        }else{
            node.setRight(insertNode(node.getRight(), vals, depth + 1));
        }

        return node;
    }

    //NODE DELETION
    private KDNode deleteNode(KDNode node, int[] vals, int depth){
        if(node == null){
            return null;
        }

        int curDim = depth % KDNode.DIMENSIONS;

        if(isEquals(node.getVal(), vals)){
            if(node.getLeft() == null && node.getRight() == null){
                return null;
            }
            if(node.getLeft() == null){
                int[] min = findMin(node.getRight(), depth + 1, curDim);
                node.setVals(min);

                node.setRight(deleteNode(node.getRight(), min, depth + 1));
            }else{
                int[] max = findMax(node.getLeft(), depth + 1, curDim);
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
        return node;
    }

    //NODE DETECTION
    private boolean findNode(KDNode node, int[] vals, int depth){
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
    private KDNode nearestNeighbor(KDNode node, int[] vals, KDNode bestNode, double bestDist, int depth){        
        if(node == null){
            return bestNode;
        }

        double curDist = eucDistance(node.getVal(), vals);

        // System.out.println(Arrays.toString(node.getVal()) + " " + bestDist + " " + curDist + " " + Arrays.toString(vals));

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

        if(Math.abs(node.getVal()[curDim] - vals[curDim]) < bestDist){
            bestNode = nearestNeighbor(branchTwo, vals, bestNode, bestDist, depth + 1);
        }
        return bestNode;
    }
    
    //RANGE QUERIES

    //HELPER FUNCTIONS
    private boolean isEquals(int[] a, int[] b){
        assert a.length == b.length;
        
        for (int i = 0; i < a.length; i++) {
            if(a[i] != b[i]){
                return false;
            }
        }
        return true;
    }
    private double eucDistance(int[] a, int[] b){
        assert a.length == b.length;

        double dist = 0;
        for (int i = 0; i < a.length; i++) {
            dist += (a[i] - b[i]) * (a[i] - b[i]);
        }
        dist = Math.sqrt(dist);
        return dist;
    }
    private List<int[]> collectPoints(){
        ArrayList<int[]> cur = new ArrayList<>();
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
    private KDNode rebalanceTree(List<int[]> points, int depth){
        if(points.isEmpty()){
            return null;
        }

        int medianIdx = points.size()/2;

        int[] medianPoint = quickSelect(points, medianIdx, depth);
        KDNode median = new KDNode(medianPoint);
        int curDim = depth % KDNode.DIMENSIONS;

        List<int[]> leftPoints = new ArrayList<>();
        List<int[]> rightPoints = new ArrayList<>();
        
        for(int[] e : points){
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

        return median;
    }
    private int[] quickSelect(List<int[]> points, int k, int depth){
        if(points.size() == 1){
            return points.get(0);
        }

        int[] pivot = points.get(points.size()/2);
        int curDim = depth % KDNode.DIMENSIONS;
        List<int[]> lowPoints = new ArrayList<>();
        List<int[]> highPoints = new ArrayList<>();
        List<int[]> equalPoints = new ArrayList<>();

        for(int[] e : points){
            if(e[curDim] < pivot[curDim]){
                lowPoints.add(e);
            }else if(e[curDim] == pivot[curDim]){
                equalPoints.add(e);
            }else{
                highPoints.add(e);
            }
        }
        if(k < lowPoints.size()){
            return quickSelect(lowPoints, k, depth + 1);
        }else if(k < lowPoints.size() + equalPoints.size()){
            return pivot;
        }else{
            return quickSelect(highPoints, k - lowPoints.size() - equalPoints.size(), depth);
        }
    }
    private int[] findMax(KDNode node, int depth, int axis){
        int curDim = depth % KDNode.DIMENSIONS;

        if(node == null){
            return null;
        }
        if(curDim == axis){
            if(node.getRight() == null){
                return null;
            }else{
                return findMax(node.getRight(), depth + 1, axis);
            }
        }else{
            int[] leftMax = findMax(node.getLeft(), depth + 1, axis);
            int[] rightMax = findMax(node.getRight(), depth + 1, axis);
            int[] curMax = node.getVal();

            return maxPoint(maxPoint(leftMax, rightMax, axis), curMax, axis);
        }
    }
    private int[] findMin(KDNode node, int depth, int axis){
        int curDim = depth % KDNode.DIMENSIONS;

        if(node == null){
            return null;
        }
        if(curDim == axis){
            if(node.getLeft() == null){
                return null;
            }else{
                return findMax(node.getLeft(), depth + 1, axis);
            }
        }else{
            int[] leftMax = findMin(node.getLeft(), depth + 1, axis);
            int[] rightMax = findMin(node.getRight(), depth + 1, axis);
            int[] curMax = node.getVal();

            return minPoint(minPoint(leftMax, rightMax, axis), curMax, axis);
        }
    }
    private int[] maxPoint(int[] a, int[] b, int axis){
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
    private int[] minPoint(int[] a, int[] b, int axis){
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
        if(op % KDNode.DIMENSIONS == 0){
            List<int[]> points = collectPoints();
            root = rebalanceTree(points, 0);
        }
    }
    
    //TESTING PURPOSES
    public void getTree(){
        Stack<KDNode> cur = new Stack<>();
            cur.add(root);
        while(!cur.isEmpty()){
            KDNode node = cur.pop();
            if(node == null){
                System.out.println(node);
                continue;
            }

            System.out.println(Arrays.toString(node.getVal()));
            cur.add(node.getRight());
            cur.add(node.getLeft());
        }
    }
}