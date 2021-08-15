import java.util.*;

public class DP {

    /**
     * 经典动态规划
     * <p>
     * 最长递增子序列 LIS
     * 最长公共子序列 LCS
     * 打家劫舍
     * 背包问题
     * 回文字串
     */

    // 516. 最长回文子序列
    // 区间dp
    // 给定一个字符串，求 最长的，回文的，子序列(从原序列去除任意个字符，字符顺序不变)的长度
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] f = new int[n][n];
        // f[j][i] 为下标j到i之间的子串中的最长回文数
        // 状态转移方程
        // f[j][i] = f[j + 1][i - 1] + 2             —— s.charAt(i) == s.charAt(j)
        // f[j][i] = max(f[j + 1][i], f[j][i - 1])   —— s.charAt(i) != s.charAt(j)

        // i从后往前，j从i往后
        for (int i = n - 1; i >= 0; i--) {
            f[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    f[i][j] = f[i + 1][j - 1] + 2;  // j = i + 1 时候达到"无人区"，0 + 2 = 2
                } else {
                    f[i][j] = Math.max(f[i + 1][j], f[i][j - 1]);
                }
            }
        }
        // 或者j从前往后，i从j - 1往前
        return f[0][n - 1];
    }

    // 1959. K 次调整数组大小浪费的最小总空间
    // 区间dp
    //
    // 题目等价于：
    // 给定数组 nums 以及整数 k
    // 需要把数组完整地分成 k+1 段连续的子数组
    // 每一段的权值是"这一段的最大值乘以这一段的长度"
    // 求最小化总权值(减去原值之和)
    public int minSpaceWastedKResizing(int[] nums, int k) {
        int n = nums.length;
        int sum = 0;
        int[][] g = new int[n][n];  // g[i][j] 表示i到j区间内，这一段的最大值乘以这一段的长度
        for (int i = 0; i < n; i++) {
            int m = 0;
            for (int j = i; j < n; j++) {
                m = Math.max(m, nums[j]);
                g[i][j] = m * (j + 1 - i);
            }
            sum += nums[i];
        }

        int INF = 0x3f3f3f3f;  // 适用于，需要很大的数，但还没到顶，可以往上加的情景
        int[][] dp = new int[n][k + 2];  // dp[i][j] 表示将 nums[0..i] 分成 j 段的最小总权值
        // dp[i][j] =
        // min(
        //      g[0][i],
        //      dp[0][j - 1]+g[1][i],
        //      dp[1][j - 1]+g[2][i],
        //      ... ,
        //      dp[i - 1][j - 1]+g[i][i]
        // )
        for (int i = 0; i < n; i++) Arrays.fill(dp[i], INF);

        for (int i = 0; i < n; i++) {
            for (int j = 1; j <= k + 1; j++) {
                for (int l = 0; l <= i; l++) {
                    if (l == 0) {
                        dp[i][j] = Math.min(dp[i][j], g[l][i]);
                    } else {
                        dp[i][j] = Math.min(dp[i][j], dp[l - 1][j - 1] + g[l][i]);
                    }
                }
            }
        }
        return dp[n - 1][k + 1] - sum;
    }

    // 446. 等差数列划分 II - 子序列
    // 返回为等差数列的子序列(从原序列去除任意个字符，字符顺序不变)个数
    // 序列 dp
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

    // 1937. 扣分后的最大得分
    public long maxPoints1(int[][] points) {
        int m = points.length;
        int n = points[0].length;
        long[] dp = new long[n];  // dp[] 存储到当前行的结果
        for (int i = 0; i < m; i++) {
            long[] buff = new long[n];  // buff 表示下一行可以从上一行收获的"最大增益"
            // 求增益，左扫描dp一遍，右扫描dp一遍，太秀了
            long lmax = 0;
            for (int j = 0; j < n; j++) {
                lmax = Math.max(lmax - 1, dp[j]);
                buff[j] = lmax;
            }
            long rmax = 0;
            for (int j = n - 1; j >= 0; j--) {
                rmax = Math.max(rmax - 1, dp[j]);
                buff[j] = Math.max(buff[j], rmax);
            }
            // 更新dp[]，上一行 -> 当前行
            for (int j = 0; j < n; j++) {
                dp[j] = buff[j] + points[i][j];
            }
        }
        long ans = 0;
        for (int j = 0; j < n; j++) {
            ans = Math.max(ans, dp[j]);
        }
        return ans;
    }

    // 朴素dp —— TLE
    public long maxPoints0(int[][] points) {
        int m = points.length;
        int n = points[0].length;
        // dp[i][j]表示，若选取第i行第j列，所得到的最大结果是多少
        // dp[i + 1][j] = dp[i][x] - Math.abs(j - x) —— x 取 0 - n，取最大结果
        long[][] dp = new long[m][n];
        for (int i = 0; i < n; i++) {
            dp[0][i] = points[0][i];
        }
        for (int i = 1; i < m; i++) {
            for (int j = 0; j < n; j++) {
                long max = Long.MIN_VALUE;
                for (int k = 0; k < n; k++) {
                    long t = dp[i - 1][k] - Math.abs(j - k);
                    max = Math.max(max, t);
                }
                dp[i][j] = max + points[i][j];
            }
        }
        long ans = Long.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            ans = Math.max(ans, dp[m - 1][i]);
        }
        return ans;
    }

    // 记忆化搜索（TLE）
    public long maxPoints(int[][] points) {
        int m = points.length;
        int n = points[0].length;
        maxPointsDfs(points, m, n, 0, -1, 0);
        return ans1937;
    }

    long ans1937 = 0;

    // 记忆map
    Map<String, Long> map = new HashMap<>();

    // 记忆化搜索
    public void maxPointsDfs(int[][] points, int m, int n, int height, int index, long sum) {
        if (height == m) {
            ans1937 = Math.max(ans1937, sum);
            return;
        }
        int[] row = points[height];
        for (int i = 0; i < n; i++) {
            long subtract = index == -1 ? 0 : Math.abs(i - index);
            long t = sum + row[i] - subtract;

            // 记忆化
            String key = height + "_" + i;
            if (map.getOrDefault(key, Long.MIN_VALUE) >= t) {
                continue;
            }
            map.put(key, t);

            maxPointsDfs(points, m, n, height + 1, i, t);
        }
    }


    // 1035. 不相交的线
    // 动态规划，与 1143. 最长公共子序列 雷同 —— LCS问题
    public int maxUncrossedLines(int[] nums1, int[] nums2) {
        int m = nums1.length, n = nums2.length;
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            int num1 = nums1[i - 1];
            for (int j = 1; j <= n; j++) {
                int num2 = nums2[j - 1];
                if (num1 == num2) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }


    // 300. 最长递增子序列 —— LIS问题

    // 300. 最长递增子序列
    // 有两种解法，一种 n方 的朴素解法，一种像如下这样 nlogn 解法
    public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] arr = new int[n];  // 用做状态传递的数组，以这种方式 巧妙的记录了之前的状态
        int ans = -1;
        int len = 0;
        for (int x : nums) {
            int t = Utils.lowerBound(arr, 0, len, x);
            arr[t] = x;
            ans = Math.max(ans, t + 1);
            if (t == len) {
                len++;
            }
        }
        return ans;
    }

    // 5841. 找出到每个位置为止最长的有效障碍赛跑路线
    // 求解最长不递减子序列长度，对数组中的每一项
    // 必须 nlogn 解法，否则超时
    public int[] longestObstacleCourseAtEachPosition1(int[] obstacles) {
        int n = obstacles.length;
        int[] arr = new int[n];  // 用做状态传递的数组
        int[] res = new int[n];  // 存储结果
        int len = 0, idx = 0;
        for (int x : obstacles) {
            int ins = Utils.upperBound(arr, 0, len, x);
            arr[ins] = x;
            res[idx] = ins + 1;
            if (ins == len) {
                len++;
            }
            idx++;
        }

        return res;
    }

    // 普通dp方法，复杂度 n^2（笨比）
    public int[] longestObstacleCourseAtEachPosition(int[] obstacles) {
        int n = obstacles.length;
        int[] mp = new int[10000008];
        int[] ans = new int[n];

        for (int i = 0; i < n; i++) {
            int t = obstacles[i];
            // t1 为小于等于 t 的第一个数
            int t2 = getMaxIndex(mp, obstacles, t, i);
            mp[t] = mp[t2] + 1;
            ans[i] = mp[t];
        }
        return ans;
    }

    public int getMaxIndex(int[] mp, int[] obstacles, int t1, int i) {
        int maxIndex = 0;
        int max = -1;
        // 从i往下找，obstacles[i]小于等于t1合理，寻找ans中最大的，返回其值
        while (i >= 0) {
            int now = obstacles[i];
            if (now > t1) {
                i--;
                continue;
            }
            if (mp[now] > max) {
                max = mp[now];
                maxIndex = now;
            }
            i--;
        }
        return maxIndex;
    }

    // 354. 俄罗斯套娃信封问题
    // 动态规划，寻找最长递增子序列
    public static int maxEnvelopes(int[][] envelopes) {
        if (envelopes.length == 0) {
            return 0;
        }
        int n = envelopes.length;
        // 二维数组排序，从小到大，用前减后
        Arrays.sort(envelopes, (o1, o2) -> {
            if (o1[0] != o2[0]) {
                return o1[0] - o2[0];
            } else {
                return o2[1] - o1[1];
            }
        });
        int[] f = new int[n];
        int ans = 1;
        for (int i = 0; i < n; i++) {
            f[i] = 1;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (envelopes[i][1] > envelopes[j][1]) {
                    f[i] = Math.max(f[i], f[j] + 1);
                    ans = Math.max(f[i], ans);
                }
            }
        }
        return ans;
    }


    /*
     * 1143. 最长公共子序列 —— LCS问题
     * 经典动态规划
     * dp[i][j] =
     * {
     *     dp[i - 1][j - 1] + 1;  （当text1.charAt(i) == text2.charAt(j)时）
     *     Math.max(dp[i - 1][j], dp[i][j - 1]);  （当text1.charAt(i) != text2.charAt(j)时）
     * }
     */
    public static int longestCommonSubsequence(String text1, String text2) {
        int length1 = text1.length();
        int length2 = text2.length();
        int[][] dp = new int[length1 + 1][length2 + 1];
        for (int i = 1; i < length1 + 1; i++) {
            char c1 = text1.charAt(i - 1);
            for (int j = 1; j < length2 + 1; j++) {
                char c2 = text2.charAt(j - 1);
                if (c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[length1][length2];
    }


    // 132. 分割回文串 II
    public int minCut(String s) {
        int n = s.length();
        boolean[][] g = new boolean[n][n];
        for (int i = 0; i < n; ++i) {
            Arrays.fill(g[i], true);
        }
        for (int i = n - 1; i >= 0; --i) {
            for (int j = i + 1; j < n; ++j) {
                g[i][j] = s.charAt(i) == s.charAt(j) && g[i + 1][j - 1];
            }
        }

        int[] f = new int[n];
        Arrays.fill(f, Integer.MAX_VALUE);
        // 动态规划
        // aabbc
        for (int i = 0; i < n; i++) {
            if (g[0][i]) {
                f[i] = 0;  // 切0刀
            } else {
                for (int j = 0; j < i; j++) {
                    if (g[j + 1][i]) {
                        f[i] = Math.min(f[i], f[j] + 1);
                    }
                }
            }
        }
        return f[n - 1];
    }


    // 132. 分割回文串 II
    static boolean[][] f;
    static List<List<String>> ret = new ArrayList<>();
    static List<String> ans132 = new ArrayList<>();
    static int n132;

    public List<List<String>> partition(String s) {
        n132 = s.length();
        f = new boolean[n132][n132];
        for (int i = 0; i < n132; ++i) {
            Arrays.fill(f[i], true);  // Arrays.fill
        }
        // 动态规划求解回文字串
        // f[][]中存储的true，[i, j]为回文字串，false为非回文字串
        for (int i = n132 - 1; i >= 0; --i) {
            for (int j = i + 1; j < n132; ++j) {
                f[i][j] = (s.charAt(i) == s.charAt(j)) && f[i + 1][j - 1];
            }
        }

        dfs(s, 0);
        return ret;
    }

    // aabbc
    // 找出所有回文组合，存储到ret中
    public void dfs(String s, int i) {
        if (i == n132) {
            ret.add(new ArrayList<String>(ans132));
            return;
        }
        for (int j = i; j < n132; ++j) {
            if (f[i][j]) {
                ans132.add(s.substring(i, j + 1));  // substring截取[i, j)
                dfs(s, j + 1);
                ans132.remove(ans132.size() - 1);
            }
        }
    }


    // LCP 19. 秋叶收藏集
    /*
    动态规划：通过增加维度，来全面规划问题。
    某一个状态的值，往往是问题的解
    都是有状态的
    某一个状态，可以参考前面的状态，叫做“状态转移”
     */
    public int minimumOperations(String leaves) {
        int n = leaves.length();
        int[][] f = new int[n][3];
        // 第一个元素一定为0
        f[0][0] = leaves.charAt(0) == 'y' ? 1 : 0;
        f[0][1] = f[0][2] = f[1][2] = Integer.MAX_VALUE;

        /*
        f[i][j]表示：从头开始规整第i个元素至j状态时，所需要调整的“最少”叶子数
        状态
        j = 0 处于初级r状态
        j = 1 处于中间y状态
        j = 2 处于后段r状态
        状态转移方程：
        f[i][0] = f[i-1][0] + 调整r
        f[i][1] = min( f[i-1][0], f[i-1][1] ) + 调整y
        f[i][2] = min( f[i-1][1], f[i-1][2] ) + 调整r

            r r r y y y r y r r y
        [0] 0 0 0 1 2 3 3 4 4 x x
        [1] x 1 1 0 0 0 1 1 2 3 x
        [2] x x 1 2 1 1 0 1 1 1 2

         */
        for (int i = 1; i < n; ++i) {
            int isRed = leaves.charAt(i) == 'r' ? 1 : 0;
            int isYellow = leaves.charAt(i) == 'y' ? 1 : 0;
            f[i][0] = f[i - 1][0] + isYellow;
            f[i][1] = Math.min(f[i - 1][0], f[i - 1][1]) + isRed;
            if (i >= 2) {
                f[i][2] = Math.min(f[i - 1][1], f[i - 1][2]) + isYellow;
            }
        }
        return f[n - 1][2];
    }

    // 139. 单词拆分
    // 给定一个非空字符串 s 和一个包含非空单词的列表 wordDict，判定 s 是否可以被空格拆分为一个或多个在字典中出现的单词，单词可重复使用
    // 动态规划
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>();  // wordDict允许复用，所以用Set去重
        int maxWordLength = 0;
        for (String str : wordDict) {
            wordSet.add(str);
            if (str.length() > maxWordLength) {
                maxWordLength = str.length();
            }
        }
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 0; i <= s.length(); i++) {
            for (int j = (Math.max(i - maxWordLength, 0)); j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[s.length()];
    }

    // BFS
    public static boolean wordBreakBfs(String s, List<String> wordDict) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);

        int slength = s.length();
        boolean[] visited = new boolean[slength + 1];

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int start = queue.poll();
                for (String word : wordDict) {
                    int nextStart = start + word.length();
                    if (nextStart > slength || visited[nextStart]) {
                        continue;
                    }
                    if (s.indexOf(word, start) == start) {
                        if (nextStart == slength) {
                            return true;
                        }
                        queue.add(nextStart);
                        visited[nextStart] = true;
                    }
                }
            }
        }
        return false;
    }

    // DFS
    public boolean wordBreakDfs(String s, List<String> wordDict) {
        boolean[] visited = new boolean[s.length() + 1];
        return dfsWordBreak(s, 0, wordDict, visited);
    }

    private boolean dfsWordBreak(String s, int start, List<String> wordDict, boolean[] visited) {
        for (String word : wordDict) {
            int nextStart = start + word.length();
            if (nextStart > s.length() || visited[nextStart]) {
                continue;
            }
            if (s.indexOf(word, start) == start) {  // index of returns the index within this string of the first occurrence of the specified substring, starting at the specified index.
                if (nextStart == s.length() || dfsWordBreak(s, nextStart, wordDict, visited)) {
                    return true;
                }
                visited[nextStart] = true;
            }
        }
        return false;
    }

    // 115. 不同的子序列
    // 动态规划
    public int numDistinct(String s, String t) {
        // 定义数组后，数组默认值全0
        /*
        状态转移方程：
        如果t[i] == s[j]

        dp[i][j] = dp[i][j-1] + dp[i-1][j-1]

        如果不
        dp[i][j] = dp[i][j-1]

            a b a b g b a g
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

    // 1269. 停在原地的方案数
    // 动态规划
    int mod = (int) 1e9 + 7;

    public int numWays(int steps, int len) {
        int max = Math.min(steps / 2, len - 1);
        // 定义 f[i][j] 代表当前剩余操作数为 i，所在位置为 j 的所有方案数
        int[][] f = new int[steps + 1][max + 1];
        f[steps][0] = 1;
        for (int i = steps - 1; i >= 0; i--) {
            int edge = Math.min(i, max);
            for (int j = 0; j <= edge; j++) {
                // 通过"留在原地"得到
                f[i][j] = (f[i][j] + f[i + 1][j]) % mod;
                // 从j - 1右移得到
                if (j - 1 >= 0) f[i][j] = (f[i][j] + f[i + 1][j - 1]) % mod;
                // 从j + 1左移得到
                if (j + 1 <= max) f[i][j] = (f[i][j] + f[i + 1][j + 1]) % mod;
            }
        }
        return f[0][0];
    }

    // 377. 组合总和 Ⅳ
    // 动态规划
    public int combinationSum4(int[] nums, int target) {
        // dfs会超时
        // 使用dp数组，dp[i]代表组合数为i时使用nums中的数能组成的组合数的个数
        // 别怪我写的这么完整
        // dp[i]=dp[i-nums[0]]+dp[i-nums[1]]+dp[i=nums[2]]+...
        // 举个例子比如nums=[1,3,4],target=7;
        // dp[7]=dp[6]+dp[4]+dp[3]
        // 其实就是说7的组合数可以由三部分组成，1和dp[6]，3和dp[4],4和dp[3];
        int[] dp = new int[target + 1];
        // 是为了算上自己的情况，比如dp[1]可以由dp[0]和1这个数的这种情况组成。
        dp[0] = 1;
        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (i >= num) {
                    dp[i] += dp[i - num];
                }
            }
        }
        return dp[target];
    }

    // 368. 最大整除子集
    // 动态规划
    public static List<Integer> largestDivisibleSubset(int[] nums) {
        Arrays.sort(nums);
        int[] dp = new int[nums.length];
        int[] g = new int[nums.length];
        dp[0] = 0;
        g[0] = -1;
        for (int i = 0; i < nums.length; i++) {
            int num = 0;
            int past = -1;
            for (int j = 0; j < i; j++) {
                if (nums[i] % nums[j] == 0) {
                    if (dp[j] > num) {
                        num = dp[j];
                        past = j;
                    }
                }
            }
            dp[i] = num + 1;
            g[i] = past;
        }
        int max = 0;
        int index = 0;
        for (int i = 0; i < dp.length; i++) {
            if (dp[i] > max) {
                max = dp[i];
                index = i;
            }
        }
        List<Integer> ans = new ArrayList<>();
        while (index > -1) {
            ans.add(nums[index]);
            index = g[index];
        }
        return ans;
    }

    // 213. 打家劫舍 Ⅱ
    public int rob(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        if (nums.length == 2) {
            return Math.max(nums[0], nums[1]);
        }
        int[] nums1 = new int[nums.length - 1];
        int[] nums2 = new int[nums.length - 1];
        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i != nums.length - 1) nums1[index1++] = nums[i];
            if (i != 0) nums2[index2++] = nums[i];
        }
        nums = nums1;
        if (nums.length == 1) {
            return nums[0];
        }
        if (nums.length == 2) {
            return Math.max(nums[0], nums[1]);
        }
        int[] res = new int[nums.length];
        res[0] = nums[0];
        res[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < nums.length; i++) {
            res[i] = Math.max(res[i - 1], res[i - 2] + nums[i]);
        }
        int ans1 = res[res.length - 1];
        nums = nums2;
        if (nums.length == 1) {
            return nums[0];
        }
        if (nums.length == 2) {
            return Math.max(nums[0], nums[1]);
        }
        res = new int[nums.length];
        res[0] = nums[0];
        res[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < nums.length; i++) {
            res[i] = Math.max(res[i - 1], res[i - 2] + nums[i]);
        }
        int ans2 = res[res.length - 1];
        return Math.max(ans1, ans2);
    }

    // 264. 丑数Ⅱ
    // 动态规划，三指针
    // 丑数 就是只包含质因数 2、3 和/或 5 的正整数
    public static int nthUglyNumber2(int n) {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        int index_2 = 0;
        int index_3 = 0;
        int index_5 = 0;
        long min = 1;
        for (int i = 1; i < n; i++) {
            long min_2_3 = Math.min(2 * list.get(index_2), 3 * list.get(index_3));
            min = Math.min(min_2_3, 5 * list.get(index_5));
            list.add(i, min);
            if (min == 2 * list.get(index_2)) index_2++;
            if (min == 3 * list.get(index_3)) index_3++;  // 此处不使用 else if，可达到去重效果
            if (min == 5 * list.get(index_5)) index_5++;
        }
        return (int) min;
    }

    // 91. 解码方法
    // 动态规划
    public static int numDecodings(String s) {
        int n = s.length();
        /*
         * 涉及到前两个状态的动态规划，可以添加哨兵，来简化逻辑
         * ' ' - '0' = -150
         */
        s = " " + s;
        char[] cs = s.toCharArray();  // String.toCharArray() 转换String到字符数组
        int[] f = new int[n + 1];
        f[0] = 1;
        for (int i = 1; i <= n; i++) {
            // a : 代表「当前位置」单独形成 item
            // b : 代表「当前位置」与「前一位置」共同形成 item
            int a = cs[i] - '0';
            int b = (cs[i - 1] - '0') * 10 + (cs[i] - '0');
            // 如果 a 属于有效值，那么 f[i] 可以由 f[i - 1] 转移过来
            if (1 <= a && a <= 9) f[i] = f[i - 1];
            // 如果 b 属于有效值，那么 f[i] 可以由 f[i - 2] 或者 f[i - 1] & f[i - 2] 转移过来
            if (10 <= b && b <= 26) f[i] += f[i - 2];
        }
        return f[n];
    }

    /**
     * 背包问题
     */

    /*
     * 1449. 数位成本和为目标值的最大数字
     * 动态规划：完全背包问题
     */
    public String largestNumber3(int[] cost, int t) {
        // f[j]表示当和为j时，需要多少个数字
        int[] f = new int[t + 1];
        // 这块需要填MIN_VALUE，因为会偶尔 +1 +1
        Arrays.fill(f, Integer.MIN_VALUE);
        f[0] = 0;
        for (int i = 0; i < 9; i++) {
            // 一个一个数字过
            int u = cost[i];
            for (int j = u; j <= t; j++) {
                // 由于f[j]和f[j - u]都参考的是上一个i的状态，所以这里必须倒序
                // —— 错，因为这是完全背包问题，可以重用，所以正序

                // 如 cost = [6,3,2], target = 12, u = 2
                // j = 4 时用 u 可以 在j = 2 基础上 + 1
                // j = 6 时用 u 也可以在j = 4 基础上继续用 u 来 +1，总共 +2
                // j = 8 ...
                f[j] = Math.max(f[j], f[j - u] + 1);
            }
        }
        if (f[t] < 0) return "0";
        String ans = "";
        int j = t;
        for (int i = 9; i > 0; i--) {
            // 判断，第i位可以作为ans中的下一位吗？由于只能 99887 或 99865 递减，所以不用发愁i又来了
            int u = cost[i - 1];
            while (j >= u && f[j] == f[j - u] + 1) {
                ans += i;
                j = j - u;
            }
        }
        return ans;
    }

    /*
     * 416. 分割等和子集
     * 动态规划：01背包问题
     */
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int i : nums) {
            sum += i;
        }
        if ((sum & 1) == 1) {
            return false;
        }
        int count = sum >> 1;
        int n = nums.length;
        boolean[] f = new boolean[count + 1];
        f[0] = true;
        for (int num : nums) {
            // 每一个从后往前
            for (int j = count; j >= num; j--) {
                f[j] = f[j] || f[j - num];
                if (f[count]) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * 279. 完全平方数
     * 动态规划，完全背包问题
     *
     */
    public int numSquares(int n) {
        int[] f = new int[n + 1];
        Arrays.fill(f, 0x3f3f3f3f);
        f[0] = 0;
        for (int i = 1; i * i <= n; i++) {
            int k = i * i;
            for (int x = k; x <= n; x++) {
                f[x] = Math.min(f[x], f[x - k] + 1);
            }
        }
        return f[n];
    }

    /*
     * 动态规划，01背包问题
     */
    public int lastStoneWeightII(int[] ss) {
        int n = ss.length;
        int sum = 0;
        for (int i : ss) sum += i;
        int t = sum / 2;
        int[] f = new int[t + 1];
        for (int i = 1; i <= n; i++) {
            int x = ss[i - 1];
            for (int j = t; j >= x; j--) {
                f[j] = Math.max(f[j], f[j - x] + x);
            }
        }
        return Math.abs(sum - f[t] - f[t]);
    }

    // 5815. 扣分后的最大得分
    // 双重dp + 状态压缩成一维
    public long maxPoints2(int[][] points) {
        int m = points.length;
        int n = points[0].length;
        long[] dp = new long[n];
        for (int i = 0; i < m; i++) {
            long[] cur = new long[n];
            long lmax = 0;
            for (int j = 0; j < n; j++) {
                lmax = Math.max(lmax - 1, dp[j]);
                cur[j] = lmax;
            }
            long rmax = 0;
            for (int j = n - 1; j >= 0; j--) {
                rmax = Math.max(rmax - 1, dp[j]);
                cur[j] = Math.max(cur[j], rmax);
            }
            for (int j = 0; j < n; j++) {
                dp[j] = cur[j] + points[i][j];
            }
        }
        long ans = 0;
        for (int j = 0; j < n; j++) {
            ans = Math.max(ans, dp[j]);
        }
        return ans;
    }
}
