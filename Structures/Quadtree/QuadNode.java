package Structures.Quadtree;

import java.util.ArrayList;

public class QuadNode {
    public static final int DIMENSIONS = 2;
    public static final int CAPACITY = 4;

    private ArrayList<double[]> val = new ArrayList<>();
    private double[] rect;
    private double length, height;
    private boolean divide;
    private QuadNode[] children;
    private int amt;
        
    public QuadNode(double[] center_, double length_, double height_){        
        rect = new double[]{
            center_[0] - length_/2,
            center_[1] + height_/2,
            center_[0] + length_/2,
            center_[1] - height_/2};

        length = length_;
        height = height_;

        divide = false;

        children = new QuadNode[4];

        // amt = 1;
    }
    
    public void setChildren(QuadNode node, int idx){
        children[idx] = node;
    }
    public void addVal(double[] val_){
        val.add(val_);
    }
    public void setDivideTrue(){
        divide = true;
    }
    public void setDivideFalse(){
        divide = true;
    }
    public void clearVal(){
        val = new ArrayList<>();
    }
    public void setAmt(int x){
        amt = x;
    }

    public ArrayList<double[]> getVal(){
        return val;
    }
    public double[] getRect(){
        return rect;
    }
    public QuadNode[] getChildren(){
        return children;
    }
    public double getLength(){
        return length;
    }
    public double getHeight(){
        return height;
    }
    public boolean getDivide(){
        return divide;
    }
    public int getAmt(){
        return amt;
    }
}
