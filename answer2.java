import java.io.*;
import java.util.*;

class Result {

    public static List<Integer> getUnexpiredTokens(int timeToLive, List<String> queries) {
        HashMap<String, Integer> tokenExpiry = new HashMap<>();
        TreeMap<Integer, Integer> expiryBuckets = new TreeMap<>();
        int activeCount = 0;
        List<Integer> results = new ArrayList<>();

        for (String query : queries) {
            int firstSpace = query.indexOf(' ');
            String action = query.substring(0, firstSpace);

            if (action.charAt(0) == 'g') {
                int secondSpace = query.indexOf(' ', firstSpace + 1);
                String tokenId = query.substring(firstSpace + 1, secondSpace);
                int currentTime = parseIntFast(query, secondSpace + 1);
                int expiry = currentTime + timeToLive;

                tokenExpiry.put(tokenId, expiry);
                expiryBuckets.merge(expiry, 1, Integer::sum);
                activeCount++;

            } else if (action.charAt(0) == 'r') {
                int secondSpace = query.indexOf(' ', firstSpace + 1);
                String tokenId = query.substring(firstSpace + 1, secondSpace);
                int currentTime = parseIntFast(query, secondSpace + 1);

                Integer oldExpiry = tokenExpiry.get(tokenId);
                if (oldExpiry != null && oldExpiry > currentTime) {
                    int newExpiry = currentTime + timeToLive;

                    int cnt = expiryBuckets.get(oldExpiry);
                    if (cnt == 1) {
                        expiryBuckets.remove(oldExpiry);
                    } else {
                        expiryBuckets.put(oldExpiry, cnt - 1);
                    }

                    tokenExpiry.put(tokenId, newExpiry);
                    expiryBuckets.merge(newExpiry, 1, Integer::sum);
                }

            } else {
                int currentTime = parseIntFast(query, firstSpace + 1);

                while (!expiryBuckets.isEmpty() && expiryBuckets.firstKey() <= currentTime) {
                    activeCount -= expiryBuckets.pollFirstEntry().getValue();
                }

                results.add(activeCount);
            }
        }

        return results;
    }

    private static int parseIntFast(String s, int start) {
        int val = 0;
        for (int i = start; i < s.length(); i++) {
            val = val * 10 + (s.charAt(i) - '0');
        }
        return val;
    }
}

public class UnexpiredTokens {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int timeToLive = Integer.parseInt(br.readLine().trim());
        int q = Integer.parseInt(br.readLine().trim());

        List<String> queries = new ArrayList<>(q);
        for (int i = 0; i < q; i++) {
            queries.add(br.readLine().trim());
        }

        List<Integer> result = Result.getUnexpiredTokens(timeToLive, queries);

        StringBuilder sb = new StringBuilder();
        for (int r : result) {
            sb.append(r).append('\n');
        }
        System.out.print(sb);
    }
}
