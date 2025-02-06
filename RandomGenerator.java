import java.util.*;

import Structures.KDTree.KDNode;

import java.io.*;

public class RandomGenerator {
    public static final int MAX = 1000;
    public static final int ADDOPERATIONS = 1000;
    public static final int DELETIONOPERATIONS = 1000;
    public static final int NNADDOPERATIONS = 1000;
    public static final int RNGQOPERATIONS = 1000;

    public static void main(String[] args) throws IOException{
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("data.out")));

        Random gen = new Random();

        StringBuilder addition = new StringBuilder();
        StringBuilder deletion  = new StringBuilder();
        StringBuilder nearestNeighbor = new StringBuilder();
        StringBuilder rangeQueries = new StringBuilder();

        for (int i = 0; i < ADDOPERATIONS; i++) {
            addition.append(-MAX/2 + gen.nextInt(MAX + 1)).append(" ").append(-MAX/2 + gen.nextInt(MAX + 1)).append('\n');
        }
        for (int i = 0; i < DELETIONOPERATIONS; i++) {
            deletion.append(-MAX/2 + gen.nextInt(MAX + 1)).append(" ").append(-MAX/2 + gen.nextInt(MAX + 1)).append(" ==").append('\n');
        }
        for (int i = 0; i < NNADDOPERATIONS; i++) {
            nearestNeighbor.append(-MAX/2 + gen.nextInt(MAX + 1)).append(" ").append(-MAX/2 + gen.nextInt(MAX + 1)).append('\n');
        }
        for (int i = 0; i < RNGQOPERATIONS; i++) {
            for (int j = 0; j < KDNode.DIMENSIONS; j++) {
                int a = -MAX/2 + gen.nextInt(MAX + 1); int b = -MAX/2 + gen.nextInt(MAX + 1);
                rangeQueries.append(Math.min(a, b)).append(" ").append(Math.max(a, b)).append(" ");
            }
            rangeQueries.deleteCharAt(rangeQueries.length() - 1);
            rangeQueries.append('\n');
        }
        addition.deleteCharAt(addition.length() - 1);
        deletion.deleteCharAt(deletion.length() - 1);
        nearestNeighbor.deleteCharAt(nearestNeighbor.length() - 1);
        rangeQueries.deleteCharAt(rangeQueries.length() - 1);

        pw.println(addition);
        pw.println(deletion);
        pw.println(nearestNeighbor);
        pw.println(rangeQueries);

        pw.close();
    }
}
