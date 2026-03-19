import java.io.*;
import java.util.*;
import java.util.stream.*;

class Result {

    public static String countBalancedNumbers(List<Integer> p) {
        int n = p.size();
        int[] pos = new int[n + 1];
        for (int i = 0; i < n; i++) {
            pos[p.get(i)] = i;
        }

        char[] out = new char[n];
        int lo = n, hi = -1;

        for (int k = 1; k <= n; k++) {
            int pk = pos[k];
            if (pk < lo) lo = pk;
            if (pk > hi) hi = pk;
            out[k - 1] = (hi - lo + 1 == k) ? '1' : '0';
        }

        return new String(out);
    }
}

public class BalancedNumbers {

    public static void main(String[] args) {
        int passed = 0, total = 0;

        System.out.println("==============================================");
        System.out.println("  countBalancedNumbers — Edge-Case Tests");
        System.out.println("==============================================");

        // --- Section 1: Minimal / boundary sizes ---
        System.out.println("\n--- Section 1: Minimal & Boundary ---");

        passed += check(++total, list(1), "1",
                "n=1: trivially balanced");

        passed += check(++total, list(1, 2), "11",
                "n=2: identity perm, both balanced");

        passed += check(++total, list(2, 1), "11",
                "n=2: reversed, both balanced");

        // --- Section 2: Problem examples ---
        System.out.println("\n--- Section 2: Problem Examples ---");

        passed += check(++total, list(4, 1, 3, 2), "1011",
                "Example: k=2 not balanced");

        passed += check(++total, list(5, 3, 1, 2, 4), "11111",
                "Sample 0: all balanced");

        passed += check(++total, list(1, 4, 2, 3), "1001",
                "Sample 1: only k=1,4 balanced");

        // --- Section 3: Identity permutation (all balanced) ---
        System.out.println("\n--- Section 3: Identity Permutation ---");

        passed += check(++total, list(1, 2, 3, 4, 5), "11111",
                "Identity n=5: all balanced");

        passed += check(++total, list(1, 2, 3, 4, 5, 6, 7, 8), "11111111",
                "Identity n=8: all balanced");

        // --- Section 4: Reverse permutation ---
        System.out.println("\n--- Section 4: Reverse Permutation ---");

        passed += check(++total, list(5, 4, 3, 2, 1), "11111",
                "Reverse n=5: {1..k} always contiguous at tail");

        passed += check(++total, list(4, 3, 2, 1), "1111",
                "Reverse n=4: all balanced");

        passed += check(++total, list(3, 2, 1), "111",
                "Reverse n=3: all balanced");

        passed += check(++total, list(6, 5, 4, 3, 2, 1), "111111",
                "Reverse n=6: all balanced");

        // --- Section 5: Value 1 at edges ---
        System.out.println("\n--- Section 5: Value 1 at Edges ---");

        passed += check(++total, list(2, 3, 4, 5, 1), "10001",
                "Value 1 at end: k=1(pos=5) and k=5");

        passed += check(++total, list(1, 5, 4, 3, 2), "10001",
                "Value 1 at start: k=1 and k=5");

        // --- Section 6: Contiguous blocks ---
        System.out.println("\n--- Section 6: Contiguous Blocks ---");

        passed += check(++total, list(3, 1, 2, 4), "1111",
                "Block [1,2] at pos 2-3, all k contiguous");

        passed += check(++total, list(4, 2, 1, 3, 5), "11111",
                "All k form contiguous blocks");

        passed += check(++total, list(5, 2, 3, 1, 4), "10111",
                "k=2 not balanced (pos spread too wide)");

        passed += check(++total, list(2, 1, 4, 3, 6, 5), "110101",
                "Pairs swapped: k=1,2,4,6 balanced");

        // --- Section 7: Single swap from identity ---
        System.out.println("\n--- Section 7: Single Swap from Identity ---");

        passed += check(++total, list(2, 1, 3, 4, 5), "11111",
                "Swap(1,2): {1,2} adjacent so all k balanced");

        passed += check(++total, list(1, 3, 2, 4, 5), "10111",
                "Swap(2,3): k=2 not balanced");

        passed += check(++total, list(1, 2, 4, 3, 5), "11011",
                "Swap(3,4): k=3 not balanced");

        passed += check(++total, list(1, 2, 3, 5, 4), "11101",
                "Swap(4,5): k=4 not balanced");

        // --- Section 8: Only k=1 and k=n balanced ---
        System.out.println("\n--- Section 8: Sparse Balance ---");

        passed += check(++total, list(3, 5, 1, 4, 2, 6), "100011",
                "k=1,5,6 balanced");

        passed += check(++total, list(2, 4, 6, 1, 3, 5, 7), "1000011",
                "k=1,6,7 balanced");

        // --- Section 9: Large n identity (performance + correctness) ---
        System.out.println("\n--- Section 9: Performance ---");

        int bigN = 200000;
        List<Integer> bigIdentity = new ArrayList<>(bigN);
        for (int i = 1; i <= bigN; i++) bigIdentity.add(i);
        long t0 = System.currentTimeMillis();
        String bigRes = Result.countBalancedNumbers(bigIdentity);
        long elapsed = System.currentTimeMillis() - t0;
        total++;
        boolean bigOk = bigRes.length() == bigN && bigRes.chars().allMatch(c -> c == '1');
        if (bigOk && elapsed < 1000) {
            passed++;
            System.out.printf("  Test %2d: PASS  (n=%d, %dms)  — Identity: all 1s%n",
                    total, bigN, elapsed);
        } else {
            System.out.printf("  Test %2d: FAIL  (n=%d, %dms, correct=%b)  — Identity%n",
                    total, bigN, elapsed, bigOk);
        }

        List<Integer> bigReverse = new ArrayList<>(bigN);
        for (int i = bigN; i >= 1; i--) bigReverse.add(i);
        t0 = System.currentTimeMillis();
        bigRes = Result.countBalancedNumbers(bigReverse);
        elapsed = System.currentTimeMillis() - t0;
        total++;
        boolean revOk = bigRes.length() == bigN
                && bigRes.chars().allMatch(c -> c == '1');
        if (revOk && elapsed < 1000) {
            passed++;
            System.out.printf("  Test %2d: PASS  (n=%d, %dms)  — Reverse: all balanced%n",
                    total, bigN, elapsed);
        } else {
            System.out.printf("  Test %2d: FAIL  (n=%d, %dms, correct=%b)  — Reverse%n",
                    total, bigN, elapsed, revOk);
        }

        // Interleaved: [1,n,2,n-1,3,n-2,...] — worst-case range expansion
        List<Integer> interleaved = new ArrayList<>(bigN);
        int lo = 1, hi = bigN;
        for (int i = 0; i < bigN; i++) {
            interleaved.add(i % 2 == 0 ? lo++ : hi--);
        }
        t0 = System.currentTimeMillis();
        bigRes = Result.countBalancedNumbers(interleaved);
        elapsed = System.currentTimeMillis() - t0;
        total++;
        if (bigRes.length() == bigN && elapsed < 1000) {
            passed++;
            System.out.printf("  Test %2d: PASS  (n=%d, %dms)  — Interleaved perm%n",
                    total, bigN, elapsed);
        } else {
            System.out.printf("  Test %2d: FAIL  (n=%d, %dms)  — Interleaved perm%n",
                    total, bigN, elapsed);
        }

        // --- Section 10: Brute-force validation on small n ---
        System.out.println("\n--- Section 10: Brute-Force Cross-Validation ---");

        int bfPass = 0, bfTotal = 0;
        Random rng = new Random(12345);
        for (int t = 0; t < 500; t++) {
            int sz = 2 + rng.nextInt(9);
            List<Integer> perm = randomPerm(sz, rng);
            String dpAns = Result.countBalancedNumbers(perm);
            String bfAns = bruteForce(perm);
            bfTotal++;
            if (dpAns.equals(bfAns)) {
                bfPass++;
            } else {
                System.out.printf("  BF FAIL: p=%s  dp=%s  bf=%s%n", perm, dpAns, bfAns);
            }
        }
        total++;
        if (bfPass == bfTotal) {
            passed++;
            System.out.printf("  Test %2d: PASS  (%d/%d random perms matched brute-force)%n",
                    total, bfPass, bfTotal);
        } else {
            System.out.printf("  Test %2d: FAIL  (%d/%d random perms matched brute-force)%n",
                    total, bfPass, bfTotal);
        }

        // --- Section 11: Cyclic shifts ---
        System.out.println("\n--- Section 11: Cyclic Shifts ---");

        passed += check(++total, list(2, 3, 4, 5, 1), "10001",
                "Left-rotate identity by 1");

        passed += check(++total, list(3, 4, 5, 1, 2), "11001",
                "Left-rotate identity by 2");

        passed += check(++total, list(4, 5, 1, 2, 3), "11101",
                "Left-rotate identity by 3");

        passed += check(++total, list(5, 1, 2, 3, 4), "11111",
                "Left-rotate identity by 4");

        // --- Section 12: Derangements ---
        System.out.println("\n--- Section 12: Derangements ---");

        passed += check(++total, list(2, 1, 4, 3), "1101",
                "Pair-swap derangement n=4");

        passed += check(++total, list(2, 3, 1), "101",
                "Cyclic derangement n=3");

        passed += check(++total, list(3, 1, 2), "111",
                "Cyclic shift [3,1,2]: all k balanced");

        // ═══════════════════════════════════════════════════════════
        System.out.println("\n==============================================");
        System.out.printf("  TOTAL: %d / %d PASSED%n", passed, total);
        System.out.println("==============================================");
    }

    private static String bruteForce(List<Integer> p) {
        int n = p.size();
        char[] res = new char[n];
        for (int k = 1; k <= n; k++) {
            res[k - 1] = '0';
            outer:
            for (int l = 0; l <= n - k; l++) {
                int r = l + k - 1;
                boolean[] seen = new boolean[k + 1];
                boolean valid = true;
                for (int i = l; i <= r; i++) {
                    int v = p.get(i);
                    if (v < 1 || v > k) { valid = false; break; }
                    if (seen[v]) { valid = false; break; }
                    seen[v] = true;
                }
                if (valid) { res[k - 1] = '1'; break outer; }
            }
        }
        return new String(res);
    }

    private static List<Integer> randomPerm(int n, Random rng) {
        List<Integer> perm = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) perm.add(i);
        Collections.shuffle(perm, rng);
        return perm;
    }

    private static int check(int num, List<Integer> p, String expected, String desc) {
        String got = Result.countBalancedNumbers(p);
        String status = got.equals(expected) ? "PASS" : "FAIL";
        System.out.printf("  Test %2d: %s  (expected=%s, got=%s)  — %s%n",
                num, status, expected, got, desc);
        return got.equals(expected) ? 1 : 0;
    }

    @SafeVarargs
    private static List<Integer> list(Integer... vals) {
        return Arrays.asList(vals);
    }
}
