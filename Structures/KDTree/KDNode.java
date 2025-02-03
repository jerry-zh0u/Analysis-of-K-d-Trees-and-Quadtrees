package Structures.KDTree;

public class KDNode{
    public static final int DIMENSIONS = 2;

    private KDNode left, right;
    private double[] val;
    private int amt;

    //INIT
    public KDNode(double[] val_){
        val = new double[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            val[i] = val_[i];
        }
        
        left = null;
        right = null;
        amt = 1;
    }

    //SETTER FUNCTIONS
    public void setLeft(KDNode node){
        left = node;
    }
    public void setRight(KDNode node){
        right = node;
    }
    public void setVals(double[] val_){
        for (int i = 0; i < val_.length; i++) {
            val[i] = val_[i];
        }
    }
    public void setAmt(int val_){
        amt = val_;
    }

    //GETTER FUNCTIONS
    public double[] getVal(){
        return val;
    }
    public KDNode getLeft(){
        return left;
    }
    public KDNode getRight(){
        return right;
    }
    public int getAmt(){
        return amt;
    }
}   