import Structures.KDTree.KDNode;
import Structures.KDTree.KDTrees;
import Structures.Quadtree.QuadNode;
import Structures.Quadtree.QuadTrees;

import java.util.*;
import java.io.*;

public class Test {
    public static void main(String[] args) {
        QuadTrees tree = new QuadTrees();

        tree.insertNode(new double[]{0, 0});
        tree.insertNode(new double[]{1, 1});
        tree.insertNode(new double[]{-1, 1});
        tree.insertNode(new double[]{1, -1});

        // System.out.println(tree.root.getVal().size());
        System.out.println(tree.root.getDivide());

        // tree.insertNode(new double[]{-1, -1});

        System.out.println(tree.root.getDivide() + " " + Arrays.toString(tree.root.getRect()));

        // for(QuadNode e : tree.root.getChildren()){
        //     System.out.println("==" + e + " " + Arrays.toString(e.getRect()));
        //     for(double[] e1 : e.getVal()){
        //         System.out.print(Arrays.toString(e1));
        //     }
        //     System.out.println();
        // }

        // System.out.println(Arrays.toString(tree.root.getChildren()[0].getRect()));
        // System.out.println(Arrays.toString(tree.root.getChildren()[1].getVal()));
        // System.out.println(Arrays.toString(tree.root.getChildren()[2].getVal()));
        // System.out.println(Arrays.toString(tree.root.getChildren()[3].getVal()));

        // tree.insertNode(new double[]{-2, 2});

        // System.out.println(Arrays.toString(tree.root.getChildren()[0].getChildren()));
        // KDTrees tree = new KDTrees();

        // tree.insertNode(new double[]{0, 0});

        // System.out.println(tree.root);

        // tree.deleteNode(new double[]{0, 0});

        // System.out.println(tree.root);
    }
}
