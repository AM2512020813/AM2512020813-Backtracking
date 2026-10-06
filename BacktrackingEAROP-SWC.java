import java.util.*;

public class BacktrackingEAROP {

    static int[][] costMatrix = {
        {0, 15, 25, 35},
        {15, 0, 30, 28},
        {25, 30, 0, 20},
        {35, 28, 20, 0}
    };

    static String[] locations = {
        "Hospital",
        "Emergency Location B",
        "Emergency Location C",
        "Emergency Location D"
    };

    // ============================================
    // Backtracking Route Optimization
    // ============================================

    // Best complete route found so far (shared by all recursive calls)
    private static int bestCost;
    private static String bestRoute;

    public static String backtrackingEAROP(int[][] dist) {
        // Data validation before running the algorithm
        String error = validateMatrix(dist);
        if (error != null) {
            return "Backtracking Ambulance Route: INVALID INPUT - " + error;
        }

        int n = dist.length;
        boolean[] visited = new boolean[n];
        visited[0] = true;                       // ambulance starts at the hospital (index 0)
        bestCost = Integer.MAX_VALUE;
        bestRoute = "";

        StringBuilder path = new StringBuilder(nameOf(0));
        earopBacktracking(0, dist, visited, n, 1, 0, path);

        return "Backtracking Ambulance Route: " + bestRoute
                + " | Total Cost: " + bestCost;
    }

    // Backtracking Helper Method
    // pos   = current location, count = locations visited so far (hospital included)
    // cost  = travel cost accumulated so far, path = route built so far
    // Returns the cheapest total cost (including return to hospital) found below this call.
    private static int earopBacktracking(
            int pos,
            int[][] dist,
            boolean[] visited,
            int n,
            int count,
            int cost,
            StringBuilder path) {

        // PRUNE: this partial route is already as expensive as the best full route,
        // so no way of finishing it can be better. Abandon this branch.
        if (cost >= bestCost) {
            return Integer.MAX_VALUE;
        }

        // BASE CASE: every location visited, so return to the hospital
        if (count == n) {
            int total = cost + dist[pos][0];
            if (total < bestCost) {
                bestCost = total;
                bestRoute = path + " -> " + nameOf(0);
            }
            return total;
        }

        int best = Integer.MAX_VALUE;

        for (int next = 1; next < n; next++) {
            if (!visited[next]) {
                // CHOOSE: go to the next location
                visited[next] = true;
                int lengthBefore = path.length();
                path.append(" -> ").append(nameOf(next));

                // EXPLORE: recurse with the updated cost
                int result = earopBacktracking(next, dist, visited, n,
                        count + 1, cost + dist[pos][next], path);
                best = Math.min(best, result);

                // UN-CHOOSE (backtrack): undo the choice so the next option can be tried
                path.setLength(lengthBefore);
                visited[next] = false;
            }
        }
        return best;
    }

    // Data validation: square matrix, at least 2 locations, no negative costs,
    // zero on the diagonal.
    private static String validateMatrix(int[][] dist) {
        if (dist == null || dist.length < 2) {
            return "cost matrix must contain at least 2 locations";
        }
        int n = dist.length;
        for (int i = 0; i < n; i++) {
            if (dist[i] == null || dist[i].length != n) {
                return "cost matrix must be square (row " + i + " has the wrong length)";
            }
            for (int j = 0; j < n; j++) {
                if (dist[i][j] < 0) {
                    return "negative cost at [" + i + "][" + j + "]";
                }
                if (i == j && dist[i][j] != 0) {
                    return "cost from a location to itself must be 0 (at [" + i + "][" + j + "])";
                }
            }
        }
        return null; // valid
    }

    // Safe location name (falls back if the names array is shorter than the matrix)
    private static String nameOf(int i) {
        return (i < locations.length) ? locations[i] : "Location " + i;
    }

    public static void main(String[] args) {
        System.out.println(backtrackingEAROP(costMatrix));

        // Validation tests
        System.out.println(backtrackingEAROP(new int[][]{{0, 5}, {5}}));
        System.out.println(backtrackingEAROP(new int[][]{{0, -5}, {-5, 0}}));
        System.out.println(backtrackingEAROP(new int[][]{{0}}));
        System.out.println(backtrackingEAROP(new int[][]{{3, 5}, {5, 0}}));
    }
}
