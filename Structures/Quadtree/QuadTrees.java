package Structures.Quadtree;

import java.util.ArrayList;

public class QuadTrees {
    private static final double MAX_BOUND_X = 100;
    private static final double MAX_BOUND_Y = 100;

    //ADDED FOR CONVIENCE: USED TO DISTINGUISH BETWEEN THE DIFFERENT SIDES OF THE RECTANGLE FOR THE SUBDIVISION
    private static final int[] regionXIdx = {0, 2, 2, 0};
    private static final int[] regionYIdx = {1, 1, 3, 3};

    public QuadNode root;
    private int total = 0;

    //QUADTREE OPERATIONS
    public void insertNode(double[] vals){
        if(root == null){
            root = new QuadNode(vals, MAX_BOUND_X, MAX_BOUND_Y);
            root.addVal(vals);
            return;
        }

        insertNode(root, vals);
    }
    public void deleteNode(double[] vals){
        if(root == null || !contains(root, vals)){
            return;
        }
        deleteNode(root, null, vals);
    }
    public double[] nearestNeighbor(double[] vals){
        return nearestNeighbor(root, vals, null, Double.MAX_VALUE);
    }

    //QUADTREE OPERATIONS
    private void insertNode(QuadNode node, double[] vals){
        if(!inBounds(node.getRect(), vals)){
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
    private QuadNode deleteNode(QuadNode node, QuadNode par, double[] vals){
        if(node == null){
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
            if(e == null || e.getVal().size() > 0){
                sum += e.getAmt();
            }
        }  
        node.setAmt(sum);
        return node;
    }
    private double[] nearestNeighbor(QuadNode node, double[] vals, double[] bestNode, double bestDist){
        if(node == null){
            return bestNode;
        }

        double[] curBest = minEucDist(node.getVal(), vals);
        double curDis = eucDist(curBest, vals);

        if(bestNode == null || curDis < bestDist){
            bestNode = curBest;
            bestDist = curDis;
        }

        if(!node.getDivide()){
            return bestNode;
        }

        double[] rect = node.getRect();
        double xMid = (rect[0] + rect[2])/2; double yMid = (rect[1] + rect[3])/2;
        int idx = findRegion(vals, xMid, yMid);

        bestNode = nearestNeighbor(node.getChildren()[idx], vals, bestNode, bestDist);

        if(idx == 0){
            if(Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[1], vals, bestNode, bestDist);
            }
            if(Math.abs(yMid - vals[1]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[3], vals, bestNode, bestDist);
            }
            if(Math.abs(yMid - vals[1]) < bestDist || Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[2], vals, bestNode, bestDist);
            }
        }else if(idx == 1){
            if(Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[0], vals, bestNode, bestDist);
            }
            if(Math.abs(yMid - vals[1]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[2], vals, bestNode, bestDist);
            }
            if(Math.abs(yMid - vals[1]) < bestDist || Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[3], vals, bestNode, bestDist);
            }
        }else if(idx == 2){
            if(Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[3], vals, bestNode, bestDist);
            }
            if(Math.abs(yMid - vals[1]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[1], vals, bestNode, bestDist);
            }
            if(Math.abs(yMid - vals[1]) < bestDist || Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[0], vals, bestNode, bestDist);
            }
        }else{
            if(Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[2], vals, bestNode, bestDist);
            }
            if(Math.abs(yMid - vals[1]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[0], vals, bestNode, bestDist);
            }
            if(Math.abs(yMid - vals[1]) < bestDist || Math.abs(xMid - vals[0]) < bestDist){
                bestNode = nearestNeighbor(node.getChildren()[1], vals, bestNode, bestDist);
            }
        }
        return bestNode;
    }
    // private int rangeQuery(){

    // }

    //HELPER FUNCTIONS
    private boolean inBounds(double[] rect, double[] vals){
        return rect[0] <= vals[0] && vals[0] <= rect[2] && rect[3] <= vals[1] && vals[1] <= rect[1];
    }

    private void add(double xMid, double yMid, double[] vals, double[] rect, QuadNode node){
        int idx = findRegion(vals, xMid, yMid);

        QuadNode next = node.getChildren()[idx];
        if(next == null){
            double[] center = new double[]{(rect[regionXIdx[idx]] + xMid)/2, (rect[regionYIdx[idx]] + yMid)/2};
            node.setChildren(new QuadNode(center, node.getLength()/2, node.getHeight()/2), idx);
        }
        insertNode(node.getChildren()[idx], vals);
    }

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

    private int contains(ArrayList<double[]> arr, double[] vals){
        for (int i = 0; i < arr.size(); i++) {
            if(arr.get(i)[0] == vals[0] && arr.get(i)[1] == vals[1]){
                return i;
            }
        }
        return -1;
    }
    private boolean contains(QuadNode node, double[] vals){
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
    private double[] getMid(QuadNode node){
        double[] rect = node.getRect();
        double xMid = (rect[0] + rect[2])/2;
        double yMid = (rect[1] + rect[3])/2;
        return new double[]{xMid, yMid};
    }
    private double[] minEucDist(ArrayList<double[]> points, double[] val){
        double minDist = Double.MAX_VALUE;
        int idx = 0;
        for(int i = 0; i < points.size(); i++){
            double curDist = eucDist(points.get(i), val);
            if(curDist < minDist){
                minDist = curDist;
                idx = i;
            }
        }
        return points.get(idx);
    }
    private double eucDist(double[] p1, double[] p2){
        double dist = 0;
        for (int i = 0; i < p1.length; i++) {
            dist += (p1[i] - p2[i]) * (p1[i] - p2[i]);
        }
        dist = Math.sqrt(dist);
        return dist;
    }
}