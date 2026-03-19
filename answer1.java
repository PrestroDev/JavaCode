import java.util.*;

public class Answer1 {

    public static int maxGameScore(List<Integer> cell) {
        int n = cell.size();
        if (n == 1) return 0;

        boolean[] sieve = new boolean[n];
        Arrays.fill(sieve, true);
        sieve[0] = sieve[1] = false;
        for (int i = 2; (long) i * i < n; i++) {
            if (sieve[i]) {
                for (int j = i * i; j < n; j += i)
                    sieve[j] = false;
            }
        }

        int cnt = 1;
        for (int i = 3; i < n; i++)
            if (sieve[i] && i % 10 == 3) cnt++;
        int[] jumps = new int[cnt];
        jumps[0] = 1;
        int idx = 1;
        for (int i = 3; i < n; i++)
            if (sieve[i] && i % 10 == 3) jumps[idx++] = i;

        long[] dp = new long[n];
        final long NEG_INF = Long.MIN_VALUE / 2;
        Arrays.fill(dp, NEG_INF);
        dp[0] = 0;

        for (int i = 1; i < n; i++) {
            long best = NEG_INF;
            for (int j = 0; j < jumps.length && jumps[j] <= i; j++) {
                long prev = dp[i - jumps[j]];
                if (prev > best) best = prev;
            }
            if (best != NEG_INF)
                dp[i] = best + cell.get(i);
        }

        return (int) dp[n - 1];
    }

    public static int maxGameScoreBrute(List<Integer> cell) {
        int n = cell.size();
        if (n == 1) return 0;

        boolean[] isComposite = new boolean[n];
        List<Integer> validJumps = new ArrayList<>();
        validJumps.add(1);
        for (int i = 2; i < n; i++) {
            if (!isComposite[i]) {
                if (i % 10 == 3) validJumps.add(i);
                for (long j = (long) i * i; j < n; j += i)
                    isComposite[(int) j] = true;
            }
        }

        long[] best = new long[n];
        Arrays.fill(best, Long.MIN_VALUE);
        best[0] = 0;
        for (int i = 0; i < n; i++) {
            if (best[i] == Long.MIN_VALUE) continue;
            for (int j : validJumps) {
                if (i + j < n) {
                    long score = best[i] + cell.get(i + j);
                    if (score > best[i + j]) best[i + j] = score;
                }
            }
        }
        return (int) best[n - 1];
    }

    public static void main(String[] args) {
        int passed = 0, total = 0;

        System.out.println("==============================================");
        System.out.println("  Answer1 — maxGameScore Comprehensive Tests");
        System.out.println("==============================================");

        System.out.println("\n--- Section 1: Boundary / Base Cases ---");
        passed += test(++total, list(0), 0, "n=1, cell[0]=0: stuck at start, score=0");
        passed += test(++total, list(42), 0, "n=1, cell[0]=42: cell[0] never scored");
        passed += test(++total, list(-999), 0, "n=1, cell[0]=-999: negative start still 0");
        passed += test(++total, list(0, 7), 7, "n=2: only jump+1, no prime<=1 ends in 3");
        passed += test(++total, list(0, -100), -100, "n=2: forced to visit negative cell");
        passed += test(++total, list(0, -5, 10), 5, "n=3: no prime<=2 ends in 3, must jump+1 twice");
        passed += test(++total, list(0, 0, 0), 0, "n=3: all zeros, score=0");

        System.out.println("\n--- Section 2: Prime 3 Activation (n>=4) ---");
        passed += test(++total, list(0, -100, -100, 50), 50, "n=4: jump3 0->3 skips two -100s");
        passed += test(++total, list(0, 100, 100, 100), 300, "n=4: jump+1 better, all positive (100+100+100)");
        passed += test(++total, list(0, 10, 10, 30), 50, "n=4: jump+1 all way (10+10+30=50) beats jump3 (30)");
        passed += test(++total, list(0, -1, -1, -1), -1, "n=4: jump3 skips to -1, better than -1-1-1=-3");
        passed += test(++total, list(0, 100, 100, -500), -300, "n=4: negative end unavoidable, 100+100-500=-300");

        System.out.println("\n--- Section 3: Original Problem Tests ---");
        passed += test(++total, list(0, -10, 100, -20), 70, "Original #1: -10+100-20=70 via jump+1 all");
        passed += test(++total, list(0, -100, -100, -1, 0, -1), -2, "Original #2: 0->3->4->5 = -1+0-1=-2");

        System.out.println("\n--- Section 4: Uniform Value Arrays ---");
        passed += test(++total, list(0, 0, 0, 0, 0), 0, "All zeros, n=5");
        passed += test(++total, list(0, 1, 2, 3, 4, 5), 15, "All positive: collect everything = 15");
        passed += test(++total, list(0, -5, -5, -5, -5, -5, -5, -5), -15, "All same negative -5: skip via prime3, 3 stops=-15");

        System.out.println("\n--- Section 5: Decreasing Negatives ---");
        passed += test(++total, list(0, -1, -2, -3, -4), -5, "Decreasing: 0->1->4 = -1-4=-5 via jump3");
        passed += test(++total, list(0, -1, -2, -3, -4, -5, -6, -7), -12, "Decreasing n=8: 0->1->4->7 = -1-4-7=-12");
        passed += test(++total, list(0, -10, -20, -30, -40, -50), -80, "Decreasing x10: 0->1->2->5(jump3) = -10-20-50=-80");

        System.out.println("\n--- Section 6: Strategic Skip Patterns ---");
        passed += test(++total, list(0, 1, -1000, 1, 100), 101, "Skip -1000: 0->1->4(jump3) = 1+100=101");
        passed += test(++total, list(0, -1, -1, 10, -1, -1, 10), 20, "Double jump3: 0->3->6 = 10+10=20");
        passed += test(++total, list(0, -50, 100, -50, 200), 200, "Mix: jump+1 all way gives -50+100-50+200=200");
        passed += test(++total, list(0, 100, -5000, -5000, 1), 101, "Positive then deep pit: 0->1->4(jump3) = 100+1=101");

        System.out.println("\n--- Section 7: Spikes at Prime-3 Multiples ---");
        passed += test(++total, list(0, -10, -10, 50, -10, -10, 50, -10, -10, 50), 150, "Spikes at 3,6,9: chain jump3 = 50+50+50=150");
        passed += test(++total, list(0, 500, 500, -10, 500, 500, -10, -10), 1970, "Positives at 1,2,4,5: jump+1 best = 1970");

        System.out.println("\n--- Section 8: Large Prime Single Jumps ---");
        passed += test(++total, buildArray(14, -1000, 13, 500), 500, "Prime 13: 0->13 direct jump");
        passed += test(++total, buildArray(24, -1000, 23, 999), 999, "Prime 23: 0->23 direct jump");
        passed += test(++total, buildArray(44, -1000, 43, 5000), 5000, "Prime 43: 0->43 direct jump");
        passed += test(++total, buildArray(54, -1000, 53, 7777), 7777, "Prime 53: 0->53 direct jump");
        passed += test(++total, buildArray(74, -100, 73, 50000), 50000, "Prime 73: 0->73 direct jump");
        passed += test(++total, buildArray(84, -100, 83, 77777), 77777, "Prime 83: 0->83 direct jump");

        System.out.println("\n--- Section 9: Chaining Different Primes ---");
        List<Integer> chain_3_13 = fill(17, -1000);
        chain_3_13.set(3, 10); chain_3_13.set(16, 200);
        passed += test(++total, chain_3_13, 210, "Chain prime3+prime13: 0->3->16 = 10+200=210");

        List<Integer> chain_13_3 = fill(17, -1000);
        chain_13_3.set(13, 10); chain_13_3.set(16, 200);
        passed += test(++total, chain_13_3, 210, "Chain prime13+prime3: 0->13->16 = 10+200=210");

        List<Integer> multiChain = fill(17, -1);
        multiChain.set(3, 5); multiChain.set(6, 5); multiChain.set(9, 5);
        multiChain.set(12, 5); multiChain.set(13, 5); multiChain.set(16, 100);
        passed += test(++total, multiChain, 125, "Chain: 0->3->6->9->12->13->16 = 5x5+100=125");

        List<Integer> chain_23_3_3 = fill(30, -1000);
        chain_23_3_3.set(23, 10); chain_23_3_3.set(26, 20); chain_23_3_3.set(29, 30);
        passed += test(++total, chain_23_3_3, 60, "Chain prime23+prime3+prime3: 0->23->26->29 = 60");

        List<Integer> chain_3_13_13 = fill(30, -1000);
        chain_3_13_13.set(3, 10); chain_3_13_13.set(16, 20); chain_3_13_13.set(29, 30);
        passed += test(++total, chain_3_13_13, 60, "Chain prime3+prime13+prime13: 0->3->16->29 = 60");

        System.out.println("\n--- Section 10: Non-Prime Exclusion ---");
        List<Integer> no9 = fill(10, -100);
        no9.set(9, 5000);
        passed += test(++total, no9, 4800, "9=3^2: NOT prime, chain 0->3->6->9 = 4800");

        List<Integer> no33 = fill(34, -1);
        no33.set(33, 9999);
        passed += test(++total, no33, 9995, "33=3x11: NOT prime, need 5 jumps, score=9995");

        List<Integer> no63 = fill(64, -1);
        no63.set(63, 9999);
        passed += test(++total, no63, 9995, "63=7x9: NOT prime, need 5 jumps, score=9995");

        List<Integer> no93 = fill(94, -1);
        no93.set(93, 9999);
        passed += test(++total, no93, 9995, "93=3x31: NOT prime, need 5 jumps, score=9995");

        System.out.println("\n--- Section 11: Prime Availability Boundary ---");
        List<Integer> n13 = fill(13, -1);
        n13.set(12, 100);
        passed += test(++total, n13, 97, "n=13: prime13 NOT usable, chain prime3: 97");

        List<Integer> n14 = fill(14, -1000);
        n14.set(13, 100);
        passed += test(++total, n14, 100, "n=14: prime13 usable, 0->13 direct=100");

        List<Integer> n23 = fill(23, -1);
        n23.set(22, 100);
        passed += test(++total, n23, 97, "n=23: prime23 NOT usable, best chain gives 97");

        List<Integer> n24 = fill(24, -1000);
        n24.set(23, 100);
        passed += test(++total, n24, 100, "n=24: prime23 usable, 0->23 direct=100");

        System.out.println("\n--- Section 12: Alternating / Wave Patterns ---");
        passed += test(++total, list(0, 10, -5, 10, -5, 10, -5), 15, "Alternating +10/-5: prime3 helps");

        List<Integer> wave = fill(13, -999);
        wave.set(3, 100); wave.set(6, 100); wave.set(9, 100); wave.set(12, 100);
        passed += test(++total, wave, 400, "Wave: highs every 3 positions, jump3 chain = 400");

        List<Integer> waveOff = fill(11, -999);
        waveOff.set(1, 100); waveOff.set(4, 100); waveOff.set(7, 100); waveOff.set(10, 100);
        passed += test(++total, waveOff, 400, "Wave offset+1: 0->1->4->7->10 = 400");

        List<Integer> waveOff2 = fill(12, -999);
        waveOff2.set(2, 100); waveOff2.set(5, 100); waveOff2.set(8, 100); waveOff2.set(11, 100);
        passed += test(++total, waveOff2, -599, "Wave offset+2: forced to visit one -999, score=-599");

        System.out.println("\n--- Section 13: Competing Paths ---");
        List<Integer> compete1 = fill(17, -1);
        compete1.set(3, 100); compete1.set(13, -500); compete1.set(16, 50);
        passed += test(++total, compete1, 150, "Competing: 0->3->16(prime13)=150 beats -450");

        List<Integer> compete2 = fill(17, -1);
        compete2.set(3, -500); compete2.set(13, 100); compete2.set(16, 50);
        passed += test(++total, compete2, 150, "Competing: 0->13->16(prime3)=150 beats -450");

        List<Integer> multiPath = fill(17, -1);
        multiPath.set(3, 50); multiPath.set(13, 50); multiPath.set(16, 50);
        passed += test(++total, multiPath, 147, "Multi-path: prime3 chain builds dp[13]=97, then dp[16]=147");

        System.out.println("\n--- Section 14: Large Value / Overflow ---");
        passed += test(++total, list(0, 1000000000, 0, 1000000000), 2000000000, "Large positive: 1e9+0+1e9=2e9");
        passed += test(++total, list(0, -1000000000, -1000000000, -1000000000), -1000000000, "Large negative: jump3 = -1e9");
        passed += test(++total, list(0, 1000000000, -1000000000, -1000000000, 1000000000), 2000000000, "Mixed large: 0->1->4(jump3)=2e9");

        System.out.println("\n--- Section 15: Complex Multi-Step Paths ---");
        passed += test(++total, list(0, 10, -100, 10, -100, 10, -100, 10, -100, 10), -80, "Zigzag: skip negatives via prime3, score=-80");

        List<Integer> increasing3 = fill(16, -1000);
        increasing3.set(3, 10); increasing3.set(6, 20); increasing3.set(9, 30);
        increasing3.set(12, 40); increasing3.set(15, 50);
        passed += test(++total, increasing3, 150, "Increasing at 3-multiples: 10+20+30+40+50=150");

        passed += test(++total, list(0, -1, -1, -1, -1, -1, 1000), 999, "Tunnel to payoff: 0->3->6 = -1+1000=999");

        System.out.println("\n--- Section 16: Brute-Force Cross-Validation ---");
        int brutePass = 0, bruteTotal = 0;
        int[][] bruteTests = {
            {0, 5, -3, 2, -1, 4, -2},
            {0, -7, 3, -1, 8, -4, 2, -6, 5},
            {0, 1, -1, 1, -1, 1, -1, 1},
            {0, -3, -3, -3, 1, 1, 1, -3, -3, -3, 5},
            {0, 100, -200, 300, -400, 500},
            {0, -1, 2, -3, 4, -5, 6, -7, 8, -9, 10},
            {0, 0, 0, 0, -1, 0, 0, 0, 1},
            {0, 50, -100, 50, -100, 50, -100, 50},
            {0, -5, -5, 10, -5, -5, 10, -5, -5, 10, -5, -5, 10, 100},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14},
        };
        for (int[] tc : bruteTests) {
            bruteTotal++;
            List<Integer> cells = new ArrayList<>();
            for (int v : tc) cells.add(v);
            int dpResult = maxGameScore(cells);
            int bfResult = maxGameScoreBrute(cells);
            if (dpResult == bfResult) {
                brutePass++;
            } else {
                System.out.printf("  BRUTE FAIL: cells=%s, dp=%d, brute=%d%n", cells, dpResult, bfResult);
            }
        }
        total++;
        if (brutePass == bruteTotal) {
            passed++;
            System.out.printf("  Test %2d: PASS  (all %d brute-force checks matched)  — DP vs brute-force on 10 small arrays%n", total, bruteTotal);
        } else {
            System.out.printf("  Test %2d: FAIL  (%d/%d brute-force checks matched)  — DP vs brute-force mismatch!%n", total, brutePass, bruteTotal);
        }

        int brutePass2 = 0, bruteTotal2 = 0;
        Random rng = new Random(42);
        for (int t = 0; t < 20; t++) {
            bruteTotal2++;
            int sz = 15 + rng.nextInt(16);
            List<Integer> cells = new ArrayList<>();
            cells.add(0);
            for (int j = 1; j < sz; j++) cells.add(rng.nextInt(201) - 100);
            int dpResult = maxGameScore(cells);
            int bfResult = maxGameScoreBrute(cells);
            if (dpResult == bfResult) {
                brutePass2++;
            } else {
                System.out.printf("  BRUTE FAIL: cells=%s, dp=%d, brute=%d%n", cells, dpResult, bfResult);
            }
        }
        total++;
        if (brutePass2 == bruteTotal2) {
            passed++;
            System.out.printf("  Test %2d: PASS  (all %d random brute-force checks matched)  — DP vs brute-force on 20 random arrays%n", total, bruteTotal2);
        } else {
            System.out.printf("  Test %2d: FAIL  (%d/%d random brute-force checks matched)  — DP vs brute-force mismatch!%n", total, brutePass2, bruteTotal2);
        }

        System.out.println("\n--- Section 17: Performance Sanity ---");
        List<Integer> bigArr = new ArrayList<>(100000);
        bigArr.add(0);
        for (int i = 1; i < 100000; i++) bigArr.add(i % 7 == 0 ? 100 : -1);
        long start = System.currentTimeMillis();
        int bigResult = maxGameScore(bigArr);
        long elapsed = System.currentTimeMillis() - start;
        total++;
        if (elapsed < 2000) {
            passed++;
            System.out.printf("  Test %2d: PASS  (n=100000 completed in %dms, score=%d)  — Performance OK%n", total, elapsed, bigResult);
        } else {
            System.out.printf("  Test %2d: FAIL  (n=100000 took %dms, too slow!)  — Performance issue%n", total, elapsed);
        }

        System.out.println("\n==============================================");
        System.out.printf("  TOTAL: %d / %d PASSED%n", passed, total);
        System.out.println("==============================================");
    }

    private static int test(int num, List<Integer> cell, int expected, String desc) {
        int got = maxGameScore(cell);
        String status = (got == expected) ? "PASS" : "FAIL";
        System.out.printf("  Test %2d: %s  (expected=%d, got=%d)  — %s%n", num, status, expected, got, desc);
        return (got == expected) ? 1 : 0;
    }

    private static List<Integer> list(Integer... vals) {
        return Arrays.asList(vals);
    }

    private static List<Integer> buildArray(int n, int fillVal, int targetIdx, int targetVal) {
        List<Integer> arr = new ArrayList<>(Collections.nCopies(n, fillVal));
        arr.set(0, 0);
        arr.set(targetIdx, targetVal);
        return arr;
    }

    private static List<Integer> fill(int n, int fillVal) {
        List<Integer> arr = new ArrayList<>(Collections.nCopies(n, fillVal));
        arr.set(0, 0);
        return arr;
    }
}
