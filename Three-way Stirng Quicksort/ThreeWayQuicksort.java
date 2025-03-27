/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

public class ThreeWayQuicksort {
    public static void sort(String[] a) {
        sort(a, 0, a.length - 1, 0);
    }

    private static void sort(String[] a, int lo, int hi, int d) {
        if (lo >= hi) return;
        int lt = lo;
        int gt = hi;
        int v = charAt(a[lo], d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(a[i], d);
            if (t < v) {
                String tmp = a[i];
                a[i] = a[lt];
                a[lt] = tmp;
                lt++;
                i++;
            }
            else if (t > v) {
                String tmp = a[gt];
                a[gt] = a[i];
                a[i] = tmp;
                gt--;
                // i++;
            }
            else i++;
        }
        sort(a, lo, lt - 1, d);
        if (v > 0) sort(a, lt, gt, d + 1);
        sort(a, gt + 1, hi, d);
    }

    private static int charAt(String a, int i) {
        if (i < a.length()) return a.charAt(i);
        return -1;
    }

    public static void main(String[] args) {
        In in = new In("dickens.txt");
        String str = in.readAll();
        str = str.replaceAll("[!?,]", "");
        String[] words = str.split("\\s+");
        String[] words2 = str.split("\\s+");

        long startTime = System.nanoTime();
        // Arrays.sort(words);
        sort(words2);
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println("durations is " + duration);
        System.out.println("Execution time: " + (duration / 1_000_000) + " milliseconds");
        // System.out.println(Arrays.toString(words));
        // System.out.println(Arrays.toString(words2));
    }
}
