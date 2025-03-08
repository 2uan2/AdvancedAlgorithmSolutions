/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordNet {

    private HashMap<Integer, String[]> synsets;
    private HashMap<String, List<Integer>> nounToIds;
    private ArrayList<ArrayList<Integer>> adj;
    private Integer root = null;
    private Digraph G;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException();
        this.synsets = new HashMap<>();
        this.nounToIds = new HashMap<>();
        In synsetsIn = new In(synsets);
        while (synsetsIn.hasNextLine()) {
            String line = synsetsIn.readLine();
            Integer id = Integer.parseInt(line.trim().split(",")[0]);
            String synset = line.trim().split(",")[1];
            String[] nouns = synset.split(" ");
            // System.out.println(String.join(" ", words));
            this.synsets.put(id, nouns);
            for (String noun : nouns) {
                this.nounToIds.computeIfAbsent(noun, it -> new ArrayList<>()).add(id);
            }
        }
        this.adj = new ArrayList<>(this.synsets.size());
        for (int i = 0; i < this.synsets.size(); i++) {
            this.adj.add(new ArrayList<>()); // Initialize each slot with an empty ArrayList
        }
        In hypernymsIn = new In(hypernyms);
        while (hypernymsIn.hasNextLine()) {
            String line = hypernymsIn.readLine();
            String[] arr = line.trim().split(",");
            int synsetId = Integer.parseInt(arr[0]);
            if (arr.length == 1) {
                this.root = synsetId;
            }
            for (int i = 1; i < arr.length; i++) {
                this.adj.get(synsetId).add(Integer.parseInt(arr[i]));
            }
        }
        if (this.root == null) throw new IllegalArgumentException();
        G = new Digraph(this.synsets.size());
        for (int i = 0; i < adj.size(); i++) {
            for (int j : adj.get(i)) {
                G.addEdge(i, j);
            }
        }
        sap = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        ArrayList<String> nouns = new ArrayList<>();
        for (String[] arr : synsets.values()) {
            nouns.addAll(List.of(arr));
        }
        Set<String> set = new HashSet<>(nouns);
        nouns.clear();
        nouns.addAll(set);
        return nouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        // boolean isNoun = false;
        // for (String[] arr : this.synsets.values()) {
        //     if (Arrays.asList(arr).contains(word)) isNoun = true;
        // }
        // return isNoun;
        return nounToIds.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        // SAP sap = new SAP(G);
        List<Integer> v = findIndexFromNoun(nounA);
        List<Integer> w = findIndexFromNoun(nounB);
        return sap.length(v, w);
    }

    private List<Integer> findIndexFromNoun(String noun) {
        // ArrayList<Integer> indexes = new ArrayList<>();
        // for (int i = 0; i < synsets.size(); i++) {
        //     if (Arrays.asList(synsets.get(i)).contains(noun)) {
        //         indexes.add(i);
        //     }
        // }
        return nounToIds.get(noun);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        // SAP sap = new SAP(G);
        List<Integer> v = findIndexFromNoun(nounA);
        List<Integer> w = findIndexFromNoun(nounB);
        return String.join(" ", synsets.get(sap.ancestor(v, w)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // In in = new In("hypernyms.txt");
        // while (in.hasNextLine()) {
        //     String line = in.readLine();
        //     String[] arr = line.trim().split(",");
        //     if (arr.length == 1) {
        //         System.out.println(line);
        //     }
        // }
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        // SAP sap = new SAP(wn);
        // System.out.println(Arrays.toString(wn.synsets.get(wn.findIndexFromNoun("N n"))));
        // System.out.println(wn.isNoun("anamorphosis"));
        // // System.out.println(wn.isNoun("N n"));
        // System.out.println(wn.isNoun("Mary_Mallon"));
        System.out.println(wn.distance("sprinkling", "Mary_Mallon"));
    }
}
