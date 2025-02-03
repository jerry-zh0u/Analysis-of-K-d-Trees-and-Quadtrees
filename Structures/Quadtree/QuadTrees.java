package Structures.Quadtree;

public class QuadTrees {
    private static final double MAX_BOUND_X = 100;
    private static final double MAX_BOUND_Y = 100;
    private static final int[] regionXIdx = {0, 2, 2, 0};
    private static final int[] regionYIdx = {1, 1, 3, 3};

    public QuadNode root;

    //QUADTREE OPERATIONS
    public void insertNode(double[] vals){
        if(root == null){
            root = new QuadNode(vals, MAX_BOUND_X, MAX_BOUND_Y);
            root.addVal(vals);
            return;
        }

        insertNode(root, vals);
    }

    //QUADTREE OPERATIONS
    private void insertNode(QuadNode node, double[] vals){
        if(!inBounds(node.getRect(), vals)){
            return;
        }

        if(node.getVal().size() < QuadNode.CAPACITY){
            node.addVal(vals);
            return;
        }

        double[] rect = node.getRect();
        double xMid = (rect[0] + rect[2])/2;
        double yMid = (rect[1] + rect[3])/2;

        if(!node.getDivide()){
            node.divide();
            for (double[] e : node.getVal()) {
                add(xMid, yMid, e, rect, node);
            }
        }
        add(xMid, yMid, vals, rect, node);
    }

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
}
