/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class SeamCarver {

    private class Node implements Comparable<Node> {
        double weight;
        double totalWeight;
        int x = -1;
        int y = -1;
        ArrayList<Node> nodes;
        Node from;

        Node(double weight, int x, int y) {
            this.weight = weight;
            this.totalWeight = Double.POSITIVE_INFINITY;
            this.x = x;
            this.y = y;
            nodes = new ArrayList<>();
            this.from = null;
        }

        public int compareTo(Node o) {
            if ((int) this.totalWeight - (int) o.totalWeight != 0) {
                return (int) (this.totalWeight - o.totalWeight);
            }
            else {
                return this.x - o.x;
            }
        }
    }

    private Picture picture;
    private boolean transposed = false;
    private boolean isRemovingHorizontal = false;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= picture.width() || y < 0 || y >= picture.height())
            throw new IllegalArgumentException("x or y out of bound");
        if (x == 0 || x == picture.width() - 1 || y == 0 || y == picture.height() - 1) {
            return 1000.0;
        }
        Color left = picture.get(x - 1, y);
        Color right = picture.get(x + 1, y);
        Color top = picture.get(x, y - 1);
        Color bottom = picture.get(x, y + 1);
        double RX = left.getRed() - right.getRed();
        double GX = left.getGreen() - right.getGreen();
        double BX = left.getBlue() - right.getBlue();
        double RY = top.getRed() - bottom.getRed();
        double GY = top.getGreen() - bottom.getGreen();
        double BY = top.getBlue() - bottom.getBlue();
        double deltaXSqr = RX * RX + GX * GX + BX * BX;
        double deltaYSqr = RY * RY + GY * GY + BY * BY;
        return Math.sqrt(deltaXSqr + deltaYSqr);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Picture tempPic = picture;
        picture = transposed();
        int[] seams = findVerticalSeam();
        picture = tempPic;
        return seams;
    }

    private Picture transposed() {
        Picture transposedPicture = new Picture(picture.height(), picture.width());
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                transposedPicture.setRGB(y, x, picture.getRGB(x, y));
            }
        }
        return transposedPicture;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        Node[][] graph = createVGraph();
        Node root = new Node(0, -1, -1);
        root.totalWeight = 0;
        root.nodes.addAll(Arrays.asList(graph[0]));

        Node bottomNode = runDijkstra(root);

        // if (bottomNode != null) {
        //     System.out.println("Total energy: " + bottomNode.totalWeight);
        // }

        int[] seam = new int[picture.height()];
        while (bottomNode != null) {
            if (bottomNode.x != -1) {
                seam[bottomNode.y] = bottomNode.x;
            }
            bottomNode = bottomNode.from;
        }
        return seam;
    }

    private Node runDijkstra(Node root) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(root);

        int queueCount = 0;
        int queuePoll = 0;
        List<Node> visited = new ArrayList<>();
        Node bottomNode = null;
        while (!queue.isEmpty()) {
            queuePoll += 1;
            Node smallest = queue.poll();
            if (visited.contains(smallest)) {
                continue;
            }
            if (smallest == null) {
                break;
            }
            visited.add(smallest);
            if (smallest.nodes.isEmpty()) {
                bottomNode = smallest;
                break;
            }
            for (Node node : smallest.nodes) {
                queue.add(node);
                queueCount += 1;
                if (node.totalWeight > node.weight + smallest.totalWeight) {
                    node.totalWeight = node.weight + smallest.totalWeight;
                    node.from = smallest;
                }
            }
        }
        // System.out.println(queuePoll);
        // System.out.println(queueCount);

        return bottomNode;
    }

    private Node[][] createVGraph() {
        Node[][] nodes = new Node[picture.height()][picture.width()];
        for (int y = picture.height() - 1; y >= 0; y--) {
            for (int x = 0; x < picture.width(); x++) {
                Node n = new Node(energy(x, y), x, y);
                nodes[y][x] = n;
                if (y != picture.height() - 1) {
                    nodes[y][x] = n;
                    n.nodes.add(nodes[y + 1][x]);
                    if (x != 0) {
                        n.nodes.add(nodes[y + 1][x - 1]);
                    }
                    if (x != picture.width() - 1) {
                        n.nodes.add(nodes[y + 1][x + 1]);
                    }
                }
            }
        }
        return nodes;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        // if (seam == null) throw new IllegalArgumentException();
        // if (seam.length != picture.width()) throw new IllegalArgumentException();
        // if (picture.height() <= 1) throw new IllegalArgumentException();
        // for (int i = 0; i < seam.length; i++) {
        //     if (seam[i] < 0 || seam[i] >= picture.height()) {
        //         throw new IllegalArgumentException();
        //     }
        //     // dont check on last loop
        //     if (i != seam.length - 1) {
        //         if (Math.abs(seam[i] - seam[i + 1]) > 1) {
        //             throw new IllegalArgumentException();
        //         }
        //     }
        // }
        // Picture newPic = new Picture(picture.width(), picture.height() - 1);
        // for (int x = 0; x < picture.width(); x++) {
        //     for (int y = 0; y < picture.height() - 1; y++) {
        //         if (y < seam[x]) {
        //             // picture.set(x, y, new Color(255, 255, 255, 0));
        //             newPic.set(x, y, picture.get(x, y));
        //         }
        //         else {
        //             newPic.set(x, y, picture.get(x, y + 1));
        //         }
        //     }
        // }
        // picture = newPic;
        // if (!transposed) {
        picture = transposed();
        // transposed = !transposed;
        // }
        // isRemovingHorizontal = true;
        removeVerticalSeam(seam);
        // isRemovingHorizontal = false;
        picture = transposed();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != picture.height()) throw new IllegalArgumentException();
        if (picture.width() <= 1) throw new IllegalArgumentException();
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= picture.width()) {
                throw new IllegalArgumentException();
            }
            // dont check on last loop
            if (i != seam.length - 1) {
                if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                    throw new IllegalArgumentException();
                }
            }
        }
        Picture newPic = new Picture(picture.width() - 1, picture.height());
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width() - 1; x++) {
                if (x < seam[y]) {
                    // picture.set(x, y, new Color(255, 255, 255, 0));
                    newPic.setRGB(x, y, picture.getRGB(x, y));
                }
                else {
                    newPic.setRGB(x, y, picture.getRGB(x + 1, y));
                }
            }
        }
        picture = newPic;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture("chameleon.png");
        // Picture picture = SCUtility.randomPicture(500, 500);
        // picture.show();
        SeamCarver sc = new SeamCarver(picture);
        // sc.picture();
        // long startTime = System.currentTimeMillis();
        // System.out.println(Arrays.toString(sc.findVerticalSeam()));
        // long endTime = System.currentTimeMillis();
        //
        // System.out.println("time to execute " + (endTime - startTime) / 1000.0 + "s");
        System.out.println(Arrays.toString(sc.findHorizontalSeam()));
        double[][] m = SCUtility.toEnergyMatrix(sc);
        double horizontalEnergy = 0;
        double verticalEnergy = 0;
        for (int y = 0; y < m[0].length; y++) {
            for (int x = 0; x < m.length; x++) {
                if (y == sc.findHorizontalSeam()[x]) {
                    horizontalEnergy += m[x][y];
                    // System.out.print("*");
                }
                if (x == sc.findVerticalSeam()[y]) {
                    verticalEnergy += m[x][y];
                }
                // if (m[x][y] != 1000) {
                //     System.out.print(" " + String.format("%.2f", m[x][y]));
                // }
                // else {
                //     System.out.print(" " + m[x][y]);
                // }
            }
            // System.out.println("/n");
        }
        System.out.println("Vertical energy: " + verticalEnergy);
        System.out.println("Horizontal energy: " + horizontalEnergy);
        // int[] seam = sc.findHorizontalSeam();
        // for (int i = 0; i < 5; i++) {
        //     // sc.removeHorizontalSeam(sc.findHorizontalSeam());
        //     sc.removeVerticalSeam(sc.findVerticalSeam());
        // }
        // System.out.println();

        // SCUtility.seamOverlay(picture, false, sc.findVerticalSeam()).show();
        // sc.removeVerticalSeam(sc.findVerticalSeam());
        // sc.removeHorizontalSeam(sc.findHorizontalSeam());

        // double[][] energyMatrix = SCUtility.toEnergyMatrix(sc);
        // for (int y = 0; y < energyMatrix.length; y++) {
        //     for (int x = 0; x < energyMatrix[0].length; x++) {
        //         if (energyMatrix[y][x] != 1000) {
        //             System.out.print(String.format("%.2f", energyMatrix[y][x]) + " ");
        //         }
        //         else {
        //             System.out.print(energyMatrix[y][x] + " ");
        //         }
        //     }
        //     System.out.println("/n");
        // }
    }

}