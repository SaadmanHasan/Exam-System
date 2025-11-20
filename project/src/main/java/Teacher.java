public class Teacher {
    public static int [] getQuizStatistics(String courseCode, int num) {
        int[] stats = {0, 0, 0, 0, 0};
        if (courseCode == "COMP3111" && num == 1) {
            int[] temp = {52, 48, 100, 20, 10};
            stats = temp;
            return stats;
        }

        return stats;
    }
}
