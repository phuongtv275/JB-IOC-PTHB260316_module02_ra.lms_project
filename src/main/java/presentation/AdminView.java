package presentation;

import model.Student;
import utils.ConsoleUtil;

public class AdminView {
    private final Student admin;

    public AdminView(Student admin) {
        this.admin = admin;
    }

    public void show() {
        while (true) {
            String[] options = {
                    "Quản lý khóa học",
                    "Quản lý học viên",
                    "Quản lý đăng ký khóa học",
                    "Thống kê"
            };
            ConsoleUtil.printMenu("MENU ADMIN — " + admin.getName(), options, "Đăng xuất");

            int choice = ConsoleUtil.readInt("  Lựa chọn: ");

            switch (choice) {
                case 1 -> new CourseView().show();
                case 2 -> new StudentManagementView().show();
                case 3 -> new EnrollmentManagementView().show();
                case 4 -> new StatisticsView().show();
                case 0 -> {
                    ConsoleUtil.printInfo("Đã đăng xuất.");
                    return;
                }
                default -> ConsoleUtil.printError("Lựa chọn không hợp lệ. Vui lòng thử lại.");
            }

            ConsoleUtil.pressEnterToContinue();
        }
    }
}
