/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SAP {
    private Digraph G;
    // private int[][] ancestor;
    // private int[][] length;
    private BreadthFirstDirectedPaths bfsV;
    private BreadthFirstDirectedPaths bfsW;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        this.G = new Digraph(G);
        // ancestor = new int[G.V()][G.V()];
        // length = new int[G.V()][G.V()];
        // for (int i = 0; i < G.V(); i++) {
        //     for (int j = 0; j < G.V(); j++) {
        //         ancestor[i][j] = -1;
        //         length[i][j] = -1;
        //     }
        // }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        // if (length[v][w] != -1) return length[v][w];
        // bfs(iterV, iterW);
        // return length[v][w];
        Iterable<Integer> iterV = Collections.singletonList(v);
        Iterable<Integer> iterW = Collections.singletonList(w);
        checkCornerCases(iterV, iterW);
        bfsV = new BreadthFirstDirectedPaths(G, v);
        bfsW = new BreadthFirstDirectedPaths(G, w);
        int shortestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                if (shortestDistance > bfsV.distTo(i) + bfsW.distTo(i))
                    shortestDistance = bfsV.distTo(i) + bfsW.distTo(i);
            }
        }
        return shortestDistance == Integer.MAX_VALUE ? -1 : shortestDistance;
    }

    // private void bfs(Iterable<Integer> v, Iterable<Integer> w) {
    //     // initialization
    //     boolean[] marked = new boolean[G.V()];
    //     int edgeToA[] = new int[G.V()];
    //     int edgeToB[] = new int[G.V()];
    //     int distToA[] = new int[G.V()];
    //     int distToB[] = new int[G.V()];
    //     for (int i = 0; i < G.V(); i++) {
    //         distToA[i] = distToB[i] = Integer.MAX_VALUE; // initial distance is INF
    //         edgeToA[i] = edgeToB[i] = -1; // no edge to anywhere yet
    //     }
    //     Queue<Integer> queue = new Queue<>();
    //     for (int i : v) {
    //         queue.enqueue(i);
    //         marked[i] = true;
    //         edgeToA[i] = i;
    //         distToA[i] = 0;
    //     }
    //     for (int i : w) {
    //         queue.enqueue(i);
    //         marked[i] = true;
    //         edgeToB[i] = i;
    //         distToB[i] = 0;
    //     }
    //
    //     int loopCount = 0;
    //     outerloop:
    //     while (!queue.isEmpty()) {
    //         loopCount++;
    //         if (loopCount == G.V()) break;
    //         // boolean allChecked = true;
    //         // for (boolean b : marked) if (!b) allChecked = false;
    //         // if (allChecked) break;
    //         int vertex = queue.dequeue();
    //         if (edgeToA[vertex] != -1 && edgeToB[vertex] != -1) {
    //             ancestor[vertex][vertex] = ancestor[vertex][vertex] = vertex;
    //             length[vertex][vertex] = length[vertex][vertex] = 0;
    //             // break;
    //         }
    //         for (int adjVertex : G.adj(vertex)) {
    //             queue.enqueue(adjVertex);
    //             marked[vertex] = true;
    //             // if (marked[adjVertex]) {
    //             //     // if same subset then continue
    //             //     if ((edgeToA[vertex] != -1 && edgeToA[adjVertex] != -1) || (
    //             //             edgeToB[vertex] != -1 && edgeToB[adjVertex] != -1)) continue;
    //             // }
    //             // edgeToA not -1 means vertex belongs to A
    //             if (edgeToA[vertex] != -1 && edgeToA[adjVertex] != adjVertex) {
    //                 edgeToA[adjVertex] = vertex;
    //                 distToA[adjVertex] = distToA[vertex] + 1;
    //             }
    //             else if (edgeToB[vertex] != -1 && edgeToB[adjVertex] != adjVertex) {
    //                 edgeToB[adjVertex] = vertex;
    //                 distToB[adjVertex] = distToB[vertex] + 1;
    //             }
    //             if (edgeToA[adjVertex] != -1 && edgeToB[adjVertex] != -1) {
    //                 // found a common ancestor, and probably the shortest already
    //                 int startA = findStartVertex(edgeToA, adjVertex);
    //                 int startB = findStartVertex(edgeToB, adjVertex);
    //                 if (length[startA][startB] == -1
    //                         || length[startA][startB] > distToB[adjVertex] + distToA[adjVertex]) {
    //                     ancestor[startA][startB] = ancestor[startB][startA] = adjVertex;
    //                     length[startA][startB] = length[startB][startA] = distToB[adjVertex]
    //                             + distToA[adjVertex];
    //                     // break outerloop;
    //                 }
    //             }
    //             // cases if the start and end is the same
    //         }
    //     }
    // }

    // private int findStartVertex(int[] edgeTo, int s) {
    //     int cur = s;
    //     while (cur != edgeTo[cur]) {
    //         cur = edgeTo[cur];
    //     }
    //     return cur;
    // }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Iterable<Integer> iterV = Collections.singletonList(v);
        Iterable<Integer> iterW = Collections.singletonList(w);
        checkCornerCases(iterV, iterW);
        bfsV = new BreadthFirstDirectedPaths(G, v);
        bfsW = new BreadthFirstDirectedPaths(G, w);
        int shortestDistance = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i))
                if (shortestDistance > bfsV.distTo(i) + bfsW.distTo(i)) {
                    shortestDistance = bfsV.distTo(i) + bfsW.distTo(i);
                    ancestor = i;
                }
        }
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkCornerCases(v, w);
        bfsV = new BreadthFirstDirectedPaths(G, v);
        bfsW = new BreadthFirstDirectedPaths(G, w);
        int shortestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                if (shortestDistance > bfsV.distTo(i) + bfsW.distTo(i))
                    shortestDistance = bfsV.distTo(i) + bfsW.distTo(i);
            }
        }
        return shortestDistance == Integer.MAX_VALUE ? -1 : shortestDistance;
        // int smallestDist = Integer.MAX_VALUE;
        // boolean exploredAll = true;
        // for (int i : v) {
        //     for (int j : w) {
        //         // if length[i][j] then there are still vertexs unexplored, so might not have found shortest distance yet
        //         if (length[i][j] == -1) exploredAll = false;
        //         if (length[i][j] < smallestDist && length[i][j] != -1) smallestDist = length[i][j];
        //     }
        // }
        // // return cache here, but if still unexplored in set then might still be smaller distance
        // if (exploredAll) {
        //     // if (smallestDist != -1 && smallestDist != Integer.MAX_VALUE) {
        //     System.out.println("returning from cache...");
        //     return smallestDist;
        //     // }
        // }
        // bfs(v, w);
        // int maxValue = Integer.MAX_VALUE;
        // for (int i : v) {
        //     for (int j : w) {
        //         if (length[i][j] < maxValue && length[i][j] != -1) maxValue = length[i][j];
        //     }
        // }
        // return maxValue;
        // return 1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkCornerCases(v, w);
        // bfs(v, w);
        // int shortestDist = Integer.MAX_VALUE;
        // int ancestor = -1;
        // for (int i : v) {
        //     for (int j : w) {
        //         if (length[i][j] < shortestDist && length[i][j] != -1) {
        //             ancestor = this.ancestor[i][j];
        //             shortestDist = length[i][j];
        //         }
        //     }
        // }

        bfsV = new BreadthFirstDirectedPaths(G, v);
        bfsW = new BreadthFirstDirectedPaths(G, w);
        int shortestDistance = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i))
                if (shortestDistance > bfsV.distTo(i) + bfsW.distTo(i)) {
                    shortestDistance = bfsV.distTo(i) + bfsW.distTo(i);
                    ancestor = i;
                }
        }
        return ancestor;
    }

    private void checkCornerCases(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("argument can't be null");
        if (v.spliterator().estimateSize() < 1 || w.spliterator().estimateSize() < 1) {
            throw new IllegalArgumentException("zero vertices");
        }
        for (Integer i : v) {
            if (i == null) throw new IllegalArgumentException("element can't be null");
            if (i < 0 || i >= G.V())
                throw new IllegalArgumentException("argument outside range");
        }
        for (Integer i : w) {
            if (i == null) throw new IllegalArgumentException("element can't be null");
            if (i < 0 || i >= G.V()) throw new IllegalArgumentException("argument outside range");
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v1 = StdIn.readInt();
            int v2 = StdIn.readInt();
            int w1 = StdIn.readInt();
            int w2 = StdIn.readInt();
            // int length = sap.length(v1, w1);
            int length = sap.length(
                    new ArrayList<Integer>(List.of(new Integer[] { v1, v2 })),
                    new ArrayList<Integer>(List.of(new Integer[] { w1, w2 })));
            int ancestor = sap.ancestor(
                    new ArrayList<Integer>(List.of(new Integer[] { v1, v2 })),
                    new ArrayList<Integer>(List.of(new Integer[] { w1, w2 })));

            // int ancestor = sap.ancestor(v1, w1);
            // StdOut.printf("length = %d\n", length);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }

    }
}
