import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DPSubSeq {

    // 115. 不同的子序列
    // 动态规划
    // 给定一个字符串 s 和一个字符串 t ，计算在 s 的子序列中 t 出现的个数
    public int numDistinct(String s, String t) {
        // 定义数组后，数组默认值全0
        /*
        状态转移方程：
        如果t[i] == s[j]
        dp[i][j] = dp[i][j-1] (不用s[i]和t[j]) + dp[i-1][j-1] (用s[i]和t[j])

        如果不
        dp[i][j] = dp[i][j-1]

            a b a b g b a g
            1 1 1 1 1 1 1 1
        b 0 0 1 1 2 2 3 3 3
        a 0 0 0 1 1 1 1 4 4
        g 0 0 0 0 0 1 1 1 5

         */
        int[][] dp = new int[t.length() + 1][s.length() + 1];
        for (int i = 0; i < s.length() + 1; i++) {  // 第i列
            dp[0][i] = 1;
        }
        for (int i = 1; i < t.length() + 1; i++) {  // 第i行
            for (int j = 1; j < s.length() + 1; j++) {  // 第j列
                if (s.charAt(j - 1) == t.charAt(i - 1)) {
                    // s="babgbag", t="bag"
                    // 最后一项g与g对应上时，ba在g之前的匹配（使用g） + bag在g之前的匹配（不使用此g）
                    dp[i][j] = dp[i - 1][j - 1] + dp[i][j - 1];
                } else {
                    dp[i][j] = dp[i][j - 1];
                }
            }
        }
        return dp[t.length()][s.length()];
    }

    // 446. 等差数列划分 II - 子序列
    // 返回为等差数列的子序列(从原序列去除任意个字符，字符顺序不变)个数
    // 区间 dp
    public int numberOfArithmeticSlices(int[] nums) {
        int n = nums.length;
        int ans = 0;
        List<Map<Long, Integer>> dp = new ArrayList<>();  // dp.get(i).get(diff) 表示为以 nums[i] 结尾，公差为 diff 的子序列的数量
        for (int i = 0; i < n; i++) {
            Map<Long, Integer> mp = new HashMap<>();
            dp.add(mp);
            for (int j = 0; j < i; j++) {
                long diff = nums[i] * 1L - nums[j];
                int t = dp.get(j).getOrDefault(diff, 0);
                ans += t;
                int t1 = mp.getOrDefault(diff, 0);  // 此处直接用 mp, 不使用dp.get(i), 减少对map的访问，可轻微提速
                mp.put(diff, t1 + t + 1);  // 此处也一样
            }
        }
        return ans;
    }

    // 5833. 统计特殊子序列的数目
    // 给出nums求满足 0..1..2.. 的子序列数目 x..代表一个或多个x
    // 动态规划
    public int countSpecialSubsequences(int[] nums) {
        long mod = (long) (1e9 + 7);
        long a = 0;  // n位之前满足的 0.. 数目
        long b = 0;  // 0..1..
        long c = 0;  // 0..1..2..
        for (int n : nums) {
            if (n == 0) {
                a = (2 * a + 1) % mod;
            }
            if (n == 1) {
                b = (2 * b + a) % mod;
            }
            if (n == 2) {
                c = (2 * c + b) % mod;
            }
        }
        return (int) c;
    }
}