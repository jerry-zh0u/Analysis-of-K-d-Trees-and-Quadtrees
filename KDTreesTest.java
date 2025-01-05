import Structures.KDNode;
import Structures.KDTrees;

import java.util.*;
import java.io.*;

public class KDTreesTest {
    public static void main(String[] args) {
        KDTrees test = new KDTrees();

        Stack<KDNode> dfs = new Stack<>();
        ArrayList<int[]> cur = new ArrayList<>();
        cur.add(new int[]{0, 1});
        cur.add(new int[]{2, 3});
        cur.add(new int[]{4, 5});
        cur.add(new int[]{3, 2});
        cur.add(new int[]{5, 4});

        KDNode root = new KDNode(cur.get(0));

        for(int[] e : cur){
            test.insertNode(e);
        }

        dfs.add(root);
        while(!dfs.isEmpty()){
            KDNode temp = dfs.pop();
            if(temp == null){
                // System.out.println(temp);
                continue;
            }
            // System.out.println(Arrays.toString(temp.getVal()));
            dfs.add(temp.getRight());
            dfs.add(temp.getLeft());
        }
        System.out.println(test.findNode(new int[]{2, 3}));
        System.out.println(Arrays.toString(test.nearestNeighbor(new int[]{1, 2}).getVal()));
    }
}
