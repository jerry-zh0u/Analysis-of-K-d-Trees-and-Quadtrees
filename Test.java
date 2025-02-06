import Structures.KDTree.KDNode;
import Structures.KDTree.KDTrees;
import Structures.Quadtree.QuadNode;
import Structures.Quadtree.QuadTrees;

import java.util.*;
import java.io.*;

public class Test {
    public static void main(String[] args) throws IOException{
        BufferedReader r = new BufferedReader(new FileReader("data.out"));
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("output.out")));

        QuadTrees quadTree = new QuadTrees();
        KDTrees kdTree = new KDTrees();

        double quadTreeTime = 0;
        double kdTreeTime = 0;
        
        for (int i = 0; i < RandomGenerator.ADDOPERATIONS; i++) {
            StringTokenizer st = new StringTokenizer(r.readLine());
            double[] points = new double[]{Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())};

            // double curTime = System.nanoTime();
            quadTree.insertNode(points);
            // quadTreeTime += System.nanoTime() - curTime;

            // curTime = System.nanoTime();
            kdTree.insertNode(points);
            // kdTreeTime += System.nanoTime() - curTime;

            // pw.println((i + 1) + " " + quadTreeTime + " " + kdTreeTime);
        }

        for (int i = 0; i < RandomGenerator.DELETIONOPERATIONS; i++) {
            StringTokenizer st = new StringTokenizer(r.readLine());
            double[] points = new double[]{Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())};

            // double curTime = System.nanoTime();
            quadTree.deleteNode(points);
            // quadTreeTime += System.nanoTime() - curTime;

            // curTime = System.nanoTime();
            kdTree.deleteNode(points);
            // kdTreeTime += System.nanoTime() - curTime;

            // pw.println((i + 1) + " " + quadTreeTime + " " + kdTreeTime);
        }

        for (int i = 0; i < RandomGenerator.NNADDOPERATIONS; i++) {
            StringTokenizer st = new StringTokenizer(r.readLine());
            double[] points = new double[]{Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())};

            // double curTime = System.nanoTime();
            quadTree.nearestNeighbor(points);
            // quadTreeTime += System.nanoTime() - curTime;

            // curTime = System.nanoTime();
            kdTree.nearestNeighbor(points);
            // kdTreeTime += System.nanoTime() - curTime;

            // pw.println((i + 1) + " " + quadTreeTime + " " + kdTreeTime);
        }

        for (int i = 0; i < RandomGenerator.RNGQOPERATIONS; i++) {
            StringTokenizer st = new StringTokenizer(r.readLine());
            double[][] query = new double[][]{{Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())}, {Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())}};

            double curTime = System.nanoTime();
            quadTree.rangeQuery(query);
            quadTreeTime += System.nanoTime() - curTime;

            curTime = System.nanoTime();
            kdTree.rangeQuery(query);
            kdTreeTime += System.nanoTime() - curTime;

            pw.println((i + 1) + " " + quadTreeTime + " " + kdTreeTime);
        }

        pw.close();
    }
}
