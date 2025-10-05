package com.lingoutil.workcopilot.component;

import java.util.ArrayList;
import java.util.List;

public class FuzzyMatcher {

    public static class Interval {
        private int lowerBound;
        private int upperBound;

        public Interval(int lowerBound, int upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public int getLowerBound() {
            return lowerBound;
        }

        public void setLowerBound(int lowerBound) {
            this.lowerBound = lowerBound;
        }

        public int getUpperBound() {
            return upperBound;
        }

        public void setUpperBound(int upperBound) {
            this.upperBound = upperBound;
        }
    }

    public static boolean match(String content, String target) {
        return content.toLowerCase().contains(target.toLowerCase());
    }

    public static List<Interval> getMatchIntervals(String content, String target) {
        List<Interval> intervals = new ArrayList<>();
        String contentLowerCase = content.toLowerCase();
        String targetLowerCase = target.toLowerCase();
        int startIndex = 0;

        // 循环找到所有的匹配
        while (startIndex < contentLowerCase.length()) {
            int lowerBound = contentLowerCase.indexOf(targetLowerCase, startIndex);
            if (lowerBound == -1) {
                break; // 没有更多匹配，结束循环
            }
            int upperBound = lowerBound + target.length() - 1;
            intervals.add(new Interval(lowerBound, upperBound));
            startIndex = lowerBound + 1; // 从下一个字符开始继续查找
        }
        return intervals;
    }

}
