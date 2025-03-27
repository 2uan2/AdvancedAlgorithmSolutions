/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseballElimination {
    private int teamCount;
    private List<String> teams;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] gameAgainst;

    private class Pair<T, U> {
        private T team1;
        private U team2;

        Pair(T v, U w) {
            this.team1 = v;
            this.team2 = w;
        }

        @Override
        public String toString() {
            return "(" + team1 + ", " + team2 + ")";
        }

    }

    public BaseballElimination(String filename) {
        In in = new In(filename);
        if (in.hasNextLine()) teamCount = Integer.parseInt(in.readLine().strip());
        teams = new ArrayList<String>(teamCount);
        wins = new int[teamCount];
        losses = new int[teamCount];
        remaining = new int[teamCount];
        gameAgainst = new int[teamCount][teamCount];
        int counter = 0;
        while (in.hasNextLine()) {
            String line = in.readLine().strip();
            String[] stats = line.split("\\s+");
            teams.add(stats[0]);
            wins[counter] = Integer.parseInt(stats[1]);
            losses[counter] = Integer.parseInt(stats[2]);
            remaining[counter] = Integer.parseInt(stats[3]);
            for (int i = 4; i < stats.length; i++) {
                gameAgainst[counter][i - 4] = Integer.parseInt(stats[i]);
            }
            counter++;
        }
    }// create a baseball division from given filename in format specified below

    public int numberOfTeams() {
        return teamCount;
    }

    public Iterable<String> teams() {
        return teams;
    }

    public int wins(String team) {
        if (team == null) throw new IllegalArgumentException("please provide team");
        return wins[teams.indexOf(team)];
    }

    public int losses(String team) {
        if (team == null) throw new IllegalArgumentException("please provide team");
        return losses[teams.indexOf(team)];

    }

    public int remaining(String team) {
        if (team == null) throw new IllegalArgumentException("please provide team");
        return remaining[teams.indexOf(team)];
    }

    public int against(String team1, String team2) {
        if (team1 == null || team2 == null) throw new IllegalArgumentException("ffs");
        return gameAgainst[teams.indexOf(team1)][teams.indexOf(team2)];
    }

    public boolean isEliminated(String team) {
        if (team == null) throw new IllegalArgumentException("stop");
        int teamIndex = teams.indexOf(team);

        // trivially eliminated
        for (int i = 0; i < teamCount; i++)
            if (wins[teamIndex] + remaining[teamIndex] < wins[i]) return true;
        FlowNetwork fn = makeFlowNetwork(team);
        // printFn(fn);
        int vertexCount = 2 + teamCount - 1 + ((teamCount - 1 - 1) * (teamCount - 1)) / 2;

        // non trivially eliminated
        FordFulkerson ff = new FordFulkerson(fn, 0, vertexCount - 1);
        double maxflow = ff.value();
        double otherTeamGames = 0;
        for (FlowEdge edge : fn.adj(0)) otherTeamGames += edge.capacity();

        return otherTeamGames > maxflow;
    }

    private FlowNetwork makeFlowNetwork(String team) {
        int teamIndex = teams.indexOf(team);
        // System.out.println("checking out team " + team);


        int otherTeamCount = teamCount - 1;
        int combination = ((otherTeamCount - 1) * otherTeamCount) / 2;
        int vertexCount = 2 + otherTeamCount + combination;
        // fn consist of 2 source sink vertex, n vertex for all other teams besides team, (n+1) * n / 2 for all combination of other teams
        FlowNetwork fn = new FlowNetwork(vertexCount);

        // list of other team
        ArrayList<Integer> teamVertices = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++)
            if (!Objects.equals(teams.get(i), team)) teamVertices.add(i);
        List<Pair<Integer, Integer>> gameVertices = getGameVertices(teamVertices);

        // adding all edges from source to game vertices
        // adding all edges from game vertices to team vertices
        for (int i = 1; i < combination + 1; i++) {
            Integer team1 = gameVertices.get(i - 1).team1;
            Integer team2 = gameVertices.get(i - 1).team2;
            int gameCount = gameAgainst[team1][team2];
            fn.addEdge(new FlowEdge(0, i, gameCount));
            int team1Vertex = combination + 1 + teamVertices.indexOf(team1);
            int team2Vertex = combination + 1 + teamVertices.indexOf(team2);
            fn.addEdge(new FlowEdge(i, team1Vertex, Double.POSITIVE_INFINITY));
            fn.addEdge(new FlowEdge(i, team2Vertex, Double.POSITIVE_INFINITY));
        }

        // add edges from team vertices to sink
        for (int i = 0; i < otherTeamCount; i++) {
            int otherTeam = teamVertices.get(i);
            int sinkIndex = vertexCount - 1;
            int otherTeamIndex = i + combination + 1;
            int capacity = wins[teamIndex] + remaining[teamIndex] - wins[otherTeam];
            FlowEdge edgeToSink = new FlowEdge(otherTeamIndex, sinkIndex, capacity);
            fn.addEdge(edgeToSink);
        }
        return fn;
    }

    private static void printFn(FlowNetwork fn) {
        ArrayList<FlowEdge> visited = new ArrayList<>();
        for (int i = 0; i < fn.V(); i++) {
            for (FlowEdge j : fn.adj(i)) {
                if (!visited.contains(j)) {
                    // System.out.println(j);
                    visited.add(j);
                }
            }
        }
    }

    // function that takes an array of teams and returns all matches between the teams
    private List<Pair<Integer, Integer>> getGameVertices(ArrayList<Integer> teams) {
        ArrayList<Pair<Integer, Integer>> matches = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                Pair<Integer, Integer> match = new Pair<>(teams.get(i), teams.get(j));
                matches.add(match);
            }
        }
        return matches;
    }

    public Iterable<String> certificateOfElimination(String team) {
        if (team == null) throw new IllegalArgumentException();
        int vertexCount = 2 + teamCount - 1 + ((teamCount - 1 - 1) * (teamCount - 1)) / 2;
        FlowNetwork fn = new FlowNetwork(vertexCount);
        FordFulkerson ff = new FordFulkerson(fn, 0, vertexCount - 1);
        int otherTeamCount = teamCount - 1;
        int combination = ((otherTeamCount - 1) * otherTeamCount) / 2;

        ArrayList<String> certificate = new ArrayList<>();
        ArrayList<Integer> teamVertices = new ArrayList<>();

        for (int i = 0; i < teams.size(); i++)
            if (!Objects.equals(teams.get(i), team)) teamVertices.add(i);
        System.out.println("team v is " + teamVertices);
        for (int i = 1 + combination; i < fn.V(); i++) {
            System.out.println(i);
            // System.out.println(ff.inCut(i));
            if (ff.inCut(i)) {
                certificate.add(teams.get(teamVertices.get(i - combination - 1)));
            }
        }

        if (certificate.size() == 0) return null;
        return certificate;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination("teams4.txt");
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                if (division.certificateOfElimination(team) != null) {
                    StdOut.print(team + " is eliminated by the subset R = { ");
                    for (String t : division.certificateOfElimination(team)) {
                        StdOut.print(t + " ");
                    }
                    StdOut.println("}");
                }
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}