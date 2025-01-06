package Structures;

public class KDNode{
    public static final int DIMENSIONS = 2;

    private KDNode left, right;
    private int[] val;

    //INIT
    public KDNode(int[] val_){
        val = new int[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            val[i] = val_[i];
        }
        
        left = null;
        right = null;
    }

    //SETTER FUNCTIONS
    public void setLeft(KDNode node){
        left = node;
    }
    public void setRight(KDNode node){
        right = node;
    }
    public void setVals(int[] val_){
        for (int i = 0; i < val_.length; i++) {
            val[i] = val_[i];
        }
    }

    //GETTER FUNCTIONS
    public int[] getVal(){
        return val;
    }
    public KDNode getLeft(){
        return left;
    }
    public KDNode getRight(){
        return right;
    }
}   