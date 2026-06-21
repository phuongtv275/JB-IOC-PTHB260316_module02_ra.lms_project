package presentation;

import business.IEnrollmentService;
import business.impl.EnrollmentServiceImpl;
import utils.ConsoleUtil;

import java.util.List;
import java.util.Map;

public class StatisticsView {

    private final IEnrollmentService enrollmentService = new EnrollmentServiceImpl();

    public void show() {
        while (true) {
            String[] options = {
                    "Tổng số khóa học và học viên",
                    "Số học viên đăng ký theo từng khóa học",
                    "Top 5 khóa học đông học viên nhất",
                    "Khóa học có trên 10 học viên đăng ký"
            };
            ConsoleUtil.printMenu("THỐNG KÊ", options, "Quay lại menu Admin");

            int choice = ConsoleUtil.readInt("  Lựa chọn: ");
            System.out.println();

            switch (choice) {
                case 1 -> showOverallStats();
                case 2 -> showStudentCountPerCourse();
                case 3 -> showTop5Courses();
                case 4 -> showCoursesAboveThreshold();
                case 0 -> { return; }
                default -> ConsoleUtil.printError("Lựa chọn không hợp lệ.");
            }

            ConsoleUtil.pressEnterToContinue();
        }
    }

    // ── 1. Tổng số khóa học và học viên ─────────────────────────────

    private void showOverallStats() {
        ConsoleUtil.printTitle("TỔNG QUAN HỆ THỐNG");
        int[] stats = enrollmentService.getOverallStats();

        String[] headers = {"Chỉ số", "Số lượng"};
        int[] widths = {30, 12};
        String[][] rows = {
                {"Tổng số khóa học", String.valueOf(stats[0])},
                {"Tổng số học viên", String.valueOf(stats[1])}
        };
        ConsoleUtil.printTable(headers, rows, widths);
    }

    // ── 2. Số học viên theo từng khóa học ───────────────────────────

    private void showStudentCountPerCourse() {
        ConsoleUtil.printTitle("SỐ HỌC VIÊN THEO TỪNG KHÓA HỌC");

        Map<String, Long> data = enrollmentService.getStudentCountPerCourse();
        String[] headers = {"Khóa học", "Số học viên đăng ký"};
        int[] widths = {60, 20};

        String[][] rows = data.entrySet().stream()
                .map(e -> new String[]{e.getKey(), String.valueOf(e.getValue())})
                .toArray(String[][]::new);

        ConsoleUtil.printTable(headers, rows, widths);
    }

    // ── 3. Top 5 khóa học đông học viên nhất ────────────────────────

    private void showTop5Courses() {
        ConsoleUtil.printTitle("TOP 5 KHÓA HỌC ĐÔNG HỌC VIÊN NHẤT");

        List<Object[]> top5 = enrollmentService.getTop5CoursesByEnrollment();
        String[] headers = {"Hạng", "ID", "Tên khóa học", "Số học viên"};
        int[] widths = {5, 4, 45, 12};

        String[][] rows = new String[top5.size()][];
        for (int i = 0; i < top5.size(); i++) {
            Object[] row = top5.get(i);
            rows[i] = new String[]{
                    String.valueOf(i + 1),
                    String.valueOf(row[0]),
                    String.valueOf(row[1]),
                    String.valueOf(row[2])
            };
        }
        ConsoleUtil.printTable(headers, rows, widths);
    }

    // ── 4. Khóa học có trên N học viên ──────────────────────────────

    private void showCoursesAboveThreshold() {
        ConsoleUtil.printTitle("KHÓA HỌC CÓ TRÊN 10 HỌC VIÊN ĐĂNG KÝ");

        List<Object[]> data = enrollmentService.getCoursesAboveThreshold(10);
        String[] headers = {"ID", "Tên khóa học", "Số học viên"};
        int[] widths = {4, 45, 12};

        String[][] rows = data.stream()
                .map(row -> new String[]{
                        String.valueOf(row[0]),
                        String.valueOf(row[1]),
                        String.valueOf(row[2])
                })
                .toArray(String[][]::new);

        ConsoleUtil.printTable(headers, rows, widths);
        if (data.isEmpty()) {
            ConsoleUtil.printInfo("Hiện chưa có khóa học nào vượt mốc 10 học viên đăng ký.");
        }
    }
}