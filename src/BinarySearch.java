public class BinarySearch {

    /*
      连续的一段
      前一段可以，后一段不行，则可使用二分
     */

    // 可信 1
    // 二分
    /**
     * 现给出展厅列表，下标0，1...代表0号，1号...展厅，数组的值为展厅中来参观的人数，总参观人数为 nums[0] + nums[1] + ... + nums[n - 1]
     * <br/>但本次参观最多容纳人数为cnt，如果总共来参观的人数大于cnt，我们需要对人数进行限流，给每个展厅进入人数设置一个上限
     * <br/>每个展厅最多容纳上限个人，总人数不可超过cnt
     * <p/>
     * e.g.
     * <br/>{1, 4, 2, 5, 5, 1, 6}, 13 -> 2
     * <br/>{1, 1, 1, 1, 1, 1, 25}, 13 -> 7
     * <p/>
     * @param nums 展厅列表数组
     * @param cnt 本次参观最多容纳人数为cnt
     * @return 展厅进入人数上限的最大值
     */
    public int manageTourists(int[] nums, int cnt) {
        long sum = 0;
        for (int t : nums) {
            sum += t;
        }
        if (sum <= cnt) {
            return -1;
        }
        // 限制每个展厅的人数 limit，使"实际到达"的总人数不超过cnt
        int l = 1;
        int r = cnt + 1;
        while (l < r) {
            int m = (l + r) >> 1;
            if (isCapable2(nums, m, cnt)) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return l - 1;
    }

    public boolean isCapable2(int[] nums, long limit, long cnt) {
        long sum = 0;
        for (int n : nums) {
            long t = Math.min(n, limit);
            sum += t;
            if (sum > cnt) {
                return false;
            }
        }
        return true;
    }

    // 74
    public boolean searchMatrix(int[][] matrix, int target) {
        int rowIndex = binarySearchFirstColumn(matrix, target);
        if (rowIndex < 0) {
            return false;
        }
        return binarySearchRow(matrix[rowIndex], target);
    }

    // 二分查找到比target小的数字
    public int binarySearchFirstColumn(int[][] matrix, int target) {
        int low = -1, high = matrix.length - 1;
        while (low < high) {
            int mid = (high - low + 1) / 2 + low;
            if (matrix[mid][0] <= target) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }

    // 二分查找，查找存不存在
    public boolean binarySearchRow(int[] row, int target) {
        int low = 0, high = row.length - 1;
        while (low <= high) {
            int mid = (high - low) / 2 + low;
            if (row[mid] == target) {  // 找到一个点，可以明确是否是想要的值
                return true;
            } else if (row[mid] > target) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return false;
    }

    // 278. First Bad Version
    /*
     * 区间二分
     * 1. 写 l < r，条件 r = m, l = m + 1
     * 2. 考虑会不会真正结果在l左或r右
     */
    // g b b
    public int firstBadVersion(int n) {
        int l = 1;
        int r = n;
        while (l < r) {
            long t = (long) r + l >> 1;  // 需要这样写
            // 不能这样写 long t = (r + l) >> 1; 这样是将 (r + l) >> 1 的结果转换成long
            int m = (int) t;
            if (isBadVersion(m)) r = m;
            else l = m + 1;
        }
        return l;  // 此处，return l 或 r 都可以，因为 l == r
    }

    public boolean isBadVersion(int version) {
        short s = 1;
        s = (short) (s + 1);
        byte b = 10;
        return true;
    }

    /*
     * 周赛 1898. 可移除字符的最大数目
     * 区间二分
     *
     * s = "abcbddddd", p = "abcd", removable = [3,1,0]
     * 移除哪几个后，p仍是s子序列
     *
     */
    public int maximumRemovals(String s, String p, int[] removable) {
        char[] pc = p.toCharArray();
        int l = 0;
        // 特别注意，递归结果，（第一个坏的地方），可能超过数组右边界，所以这里设 r = removable.length;
        int r = removable.length;
        while (l < r) {
            int m = (l + r) / 2;
            char[] sc = s.toCharArray();
            for (int i = 0; i <= m; i++) {
                sc[removable[i]] = '.';
            }
            // 可行，l = m + 1
            if (isSubSeq(sc, pc)) {
                l = m + 1;
            } else r = m;
        }

        return l;
    }

    public boolean isSubSeq(char[] sc, char[] pc) {
        int j = 0;
        for (int i = 0; i < sc.length; i++) {
            // 优化1
            if (sc.length - i < pc.length - j) return false;
            if (sc[i] == pc[j]) {
                j++;
                if (j == pc.length) return true;
            }
        }
        return false;
    }

    // 1011. 在 D 天内送达包裹的能力
    // 区间二分，寻找最接近的；找到一个点后，并不能知道是不是结果，需要二分逼近
    // 二分查找 与 区间二分
    public int shipWithinDays(int[] weights, int D) {
        int max = 0;
        int sum = 0;
        for (int i : weights) {
            sum += i;
            max = Math.max(i, max);
        }
        // 船的载重肯定在 平均重量 和 最大重量 之间
        int l = max;
        int r = sum;
        while (l < r) {
            int m = l + (r - l) / 2;  // 二分查找，先确定 m
            if (isCapable(weights, m, D)) {
                // 考虑刚好命中结果的场景，应当走这里
                // 我们目的是找到符合中的最小的，所以符合的话 r = m，r 永远符合要求
                r = m;
            } else {
                l = m + 1;
            }
        }
        return r;
    }

    // 如果船载重为capacity，能否在D天内装完
    public boolean isCapable(int[] weights, int capacity, int D) {
        int sum = 0;
        int round = 1;
        for (int i : weights) {
            // 写在上面的话，如果sum + 最后一个 i > capacity 了，就会失误的判断为可行
            if (sum + i > capacity) {
                round++;
                sum = i;
                if (round > D) {  // 已经开始了下一轮
                    return false;
                }
                continue;
            }
            sum += i;
        }
        return true;
    }
}