import java.util.*;

public class Solution {

    /*
     * PROBLEM 1: getMaxSubarrayLen
     *
     * For each index, pick team_a[i] or team_b[i] so the chosen sequence is
     * non-decreasing. Find the maximum length contiguous subarray where this
     * is achievable.
     *
     * Approach: At each position i, track lenA (longest valid window ending
     * here choosing a[i]) and lenB (choosing b[i]). Extend from previous
     * choices if the non-decreasing constraint holds; otherwise reset to 1.
     *
     * Time:  O(n) — single pass
     * Space: O(1) — only 4 variables carried forward
     */
    public static int getMaxSubarrayLen(List<Integer> team_a, List<Integer> team_b) {
        int n = team_a.size();
        if (n == 0) return 0;

        int maxLen = 1;
        int lenA = 1, lenB = 1;

        for (int i = 1; i < n; i++) {
            int a = team_a.get(i), b = team_b.get(i);
            int pa = team_a.get(i - 1), pb = team_b.get(i - 1);

            // Can we extend a window to position i by choosing a[i]?
            int newLenA = 1;
            if (a >= pa) newLenA = Math.max(newLenA, lenA + 1);
            if (a >= pb) newLenA = Math.max(newLenA, lenB + 1);

            // Can we extend a window to position i by choosing b[i]?
            int newLenB = 1;
            if (b >= pa) newLenB = Math.max(newLenB, lenA + 1);
            if (b >= pb) newLenB = Math.max(newLenB, lenB + 1);

            lenA = newLenA;
            lenB = newLenB;
            maxLen = Math.max(maxLen, Math.max(lenA, lenB));
        }

        return maxLen;
    }

    /*
     * PROBLEM 2: countBalancedNumbers
     *
     * k is "balanced" if values {1..k} occupy a contiguous block in p.
     * Equivalently, max(pos[1..k]) - min(pos[1..k]) + 1 == k.
     *
     * Approach: Record each value's position. Sweep k from 1 to n,
     * incrementally maintaining the running min and max positions.
     *
     * Time:  O(n)
     * Space: O(n) — position array + result string
     */
    public static String countBalancedNumbers(List<Integer> p) {
        int n = p.size();
        int[] pos = new int[n + 1];
        for (int i = 0; i < n; i++) {
            pos[p.get(i)] = i;
        }

        StringBuilder sb = new StringBuilder(n);
        int minPos = n, maxPos = -1;

        for (int k = 1; k <= n; k++) {
            minPos = Math.min(minPos, pos[k]);
            maxPos = Math.max(maxPos, pos[k]);
            // {1..k} spans exactly k indices iff the block is contiguous
            sb.append((maxPos - minPos + 1 == k) ? '1' : '0');
        }

        return sb.toString();
    }

    /*
     * PROBLEM 3: maxGameScore
     *
     * Start at index 0 (not scored). Each step: jump +1 or +3.
     * You always end at index n-1 (from any i < n-1, i+1 is always in bounds).
     *
     * Approach:
     *   dp[i] = best score when arriving at cell i
     *   dp[0] = 0  (starting cell, not counted)
     *   dp[i] = cell[i] + max(dp[i-1], dp[i-3])   (i-3 only if i >= 3)
     *
     * Time:  O(n)
     * Space: O(n)
     */
    public static int maxGameScore(List<Integer> cell) {
        int n = cell.size();
        if (n <= 1) return 0;

        int[] dp = new int[n];
        dp[0] = 0;

        for (int i = 1; i < n; i++) {
            dp[i] = dp[i - 1] + cell.get(i);
            if (i >= 3) {
                dp[i] = Math.max(dp[i], dp[i - 3] + cell.get(i));
            }
        }

        return dp[n - 1];
    }

    /*
     * PROBLEM 4: findMinimumLengthSubarray
     *
     * Classic sliding window: find the shortest subarray containing at
     * least k distinct integers.
     *
     * Approach: Expand right pointer, maintaining a frequency map.
     * Once distinct count >= k, shrink from the left to minimize length.
     *
     * Time:  O(n) — each element enters/leaves the window at most once
     * Space: O(n) — frequency map
     */
    public static int findMinimumLengthSubarray(List<Integer> arr, int k) {
        int n = arr.size();
        if (k <= 0) return 0;

        Map<Integer, Integer> freq = new HashMap<>();
        int distinct = 0, minLen = Integer.MAX_VALUE;

        for (int l = 0, r = 0; r < n; r++) {
            int val = arr.get(r);
            freq.merge(val, 1, Integer::sum);
            if (freq.get(val) == 1) distinct++;

            // Shrink window while it still satisfies the distinct requirement
            while (distinct >= k) {
                minLen = Math.min(minLen, r - l + 1);
                int lVal = arr.get(l);
                freq.merge(lVal, -1, Integer::sum);
                if (freq.get(lVal) == 0) distinct--;
                l++;
            }
        }

        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }

    // ======================== TEST RUNNER ========================

    public static void main(String[] args) {

        System.out.println("=== Problem 1: getMaxSubarrayLen ===");
        test1(Arrays.asList(5, 2, 4, 1), Arrays.asList(3, 6, 2, 2), 3, 1);
        test1(Arrays.asList(2, 7, 3), Arrays.asList(4, 2, 6), 3, 2);
        test1(Arrays.asList(9, 7), Arrays.asList(10, 8), 1, 3);

        System.out.println("\n=== Problem 2: countBalancedNumbers ===");
        test2(Arrays.asList(4, 1, 3, 2), "1011", 1);
        test2(Arrays.asList(5, 3, 1, 2, 4), "11111", 2);
        test2(Arrays.asList(1, 4, 2, 3), "1001", 3);

        System.out.println("\n=== Problem 3: maxGameScore ===");
        test3(Arrays.asList(0, -10, 100, -20), 70, 1);
        test3(Arrays.asList(0, -100, -100, -1, 0, -1), -2, 2);

        System.out.println("\n=== Problem 4: findMinimumLengthSubarray ===");
        test4(Arrays.asList(2, 2, 1, 1, 3), 3, 4, 1);
        test4(Arrays.asList(3, 2, 3, 3, 1, 3), 3, 4, 2);
        test4(Arrays.asList(1, 2, 2, 1, 2), 4, -1, 3);
    }

    private static void test1(List<Integer> a, List<Integer> b, int expected, int num) {
        int got = getMaxSubarrayLen(a, b);
        System.out.printf("  Test %d: %s (expected=%d, got=%d)%n",
                num, got == expected ? "PASS" : "FAIL", expected, got);
    }

    private static void test2(List<Integer> p, String expected, int num) {
        String got = countBalancedNumbers(p);
        System.out.printf("  Test %d: %s (expected=%s, got=%s)%n",
                num, got.equals(expected) ? "PASS" : "FAIL", expected, got);
    }

    private static void test3(List<Integer> cell, int expected, int num) {
        int got = maxGameScore(cell);
        System.out.printf("  Test %d: %s (expected=%d, got=%d)%n",
                num, got == expected ? "PASS" : "FAIL", expected, got);
    }

    private static void test4(List<Integer> arr, int k, int expected, int num) {
        int got = findMinimumLengthSubarray(arr, k);
        System.out.printf("  Test %d: %s (expected=%d, got=%d)%n",
                num, got == expected ? "PASS" : "FAIL", expected, got);
    }
}
