package com.skillgapguide.sgg.Service;

import java.util.Arrays;

/* --------------------------------------------------------------------------
 *  Minimal Hungarian Algorithm (for square cost matrix)
 * This implementation is kept
 *  simple and works for modest matrix sizes (< 100).
 * --------------------------------------------------------------------------
 */
class HungarianAlgorithm {
    private final double[][] cost;
    private final int n;
    private final double[] u, v;
    private final int[] p, way;

    public HungarianAlgorithm(double[][] cost) {
        this.cost = cost;
        this.n = cost.length;
        this.u = new double[n + 1];
        this.v = new double[n + 1];
        this.p = new int[n + 1];
        this.way = new int[n + 1];
    }

    /**
     * Executes Hungarian and returns assignment array of length n.
     * assignment[i] = j means row i assigned to column j (0‑based), or -1.
     */
    public int[] execute() {
        // 1‑based implementation (classic), rows -> jobs, cols -> workers
        for (int i = 1; i <= n; i++) {
            p[0] = i;
            double[] minv = new double[n + 1];
            Arrays.fill(minv, Double.POSITIVE_INFINITY);
            boolean[] used = new boolean[n + 1];
            int j0 = 0;
            do {
                used[j0] = true;
                int i0 = p[j0];
                double delta = Double.POSITIVE_INFINITY;
                int j1 = 0;
                for (int j = 1; j <= n; j++) {
                    if (used[j]) continue;
                    double cur = cost[i0 - 1][j - 1] - u[i0] - v[j];
                    if (cur < minv[j]) {
                        minv[j] = cur;
                        way[j] = j0;
                    }
                    if (minv[j] < delta) {
                        delta = minv[j];
                        j1 = j;
                    }
                }
                for (int j = 0; j <= n; j++) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                }
                j0 = j1;
            } while (p[j0] != 0);

            // Augmenting
            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }

        int[] assignment = new int[n];
        Arrays.fill(assignment, -1);
        for (int j = 1; j <= n; j++) {
            if (p[j] != 0 && p[j] - 1 < n && j - 1 < n) {
                assignment[p[j] - 1] = j - 1; // row -> column
            }
        }
        return assignment;
    }
}
