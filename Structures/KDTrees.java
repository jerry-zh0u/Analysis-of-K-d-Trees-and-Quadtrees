package Structures;

import java.util.*;
import java.io.*;

public class KDTrees{  
    private KDNode root;  
    private int op = 0;

    public void insertNode(int[] vals){
        KDNode node = insertNode(root, vals, 0);
        if(root == null){
            root = node;
        }
        op++;

        if(op % KDNode.DIMENSIONS == 0){
            List<int[]> points = collectPoints(root);
            root = rebalanceTree(points, 0);
        }
    }
    public boolean findNode(int[] vals){
        return findNode(root, vals, 0);
    }
    public KDNode nearestNeighbor(int[] vals){
        return nearestNeighbor(root, vals, null, Double.MAX_VALUE, 0);
    }

    //NODE INSERTION
    private KDNode insertNode(KDNode root, int[] vals, int depth){
        if(root == null){
            return new KDNode(vals);
        }

        int curDim = depth % KDNode.DIMENSIONS;

        if(vals[curDim] <= root.getVal()[curDim]){
            root.setLeft(insertNode(root.getLeft(), vals, depth + 1));
        }else{
            root.setRight(insertNode(root.getRight(), vals, depth + 1));
        }

        return root;
    }

    //NODE DELETION

    //NODE DETECTION
    private boolean findNode(KDNode root, int[] vals, int depth){
        if(root == null){
            return false;
        }
        if(isEquals(root.getVal(), vals)){
            return true;
        }

        int curDim = depth % KDNode.DIMENSIONS;

        if(vals[curDim] <= root.getVal()[curDim]){
            return findNode(root.getLeft(), vals, depth + 1);
        }else{
            return findNode(root.getRight(), vals, depth + 1);
        }
    }

    //NEAREST NEIGHBOR
    private KDNode nearestNeighbor(KDNode root, int[] vals, KDNode bestNode, double bestDist, int depth){        
        if(root == null){
            return bestNode;
        }

        double curDist = eucDistance(root.getVal(), vals);

        // System.out.println(Arrays.toString(root.getVal()) + " " + bestDist + " " + curDist + " " + Arrays.toString(vals));

        if(bestNode == null || curDist < bestDist){
            bestNode = root;
            bestDist = curDist;
        }

        int curDim = depth % KDNode.DIMENSIONS;
        KDNode branchOne = null; 
        KDNode branchTwo = null;

        if(vals[curDim] <= root.getVal()[curDim]){
            branchOne = root.getLeft();
            branchTwo = root.getRight();
        }else{
            branchOne = root.getRight();
            branchTwo = root.getLeft();
        }

        bestNode = nearestNeighbor(branchOne, vals, bestNode, bestDist, depth + 1);

        if(Math.abs(root.getVal()[curDim] - vals[curDim]) < bestDist){
            bestNode = nearestNeighbor(branchTwo, vals, bestNode, bestDist, depth + 1);
        }
        return bestNode;
    }
    //RANGE QUERIES

    //SPATIAL PARTITIONING

    //PATHFINDING

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
    private List<int[]> collectPoints(KDNode root){
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

        int[] pivot = points.get((points.size() - 1)/2);
        List<int[]> lowPoints = new ArrayList<>();
        List<int[]> highPoints = new ArrayList<>();
        List<int[]> equalPoints = new ArrayList<>();

        for(int[] e : points){
            if(e[depth] < pivot[depth]){
                lowPoints.add(e);
            }else if(e[depth] == pivot[depth]){
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