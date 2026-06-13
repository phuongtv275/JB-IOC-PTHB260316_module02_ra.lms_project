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
                case 1 -> ConsoleUtil.printWarning("Chức năng Quản lý khóa học — sẽ triển khai ở buổi 2.");
                case 2 -> ConsoleUtil.printWarning("Chức năng Quản lý học viên — sẽ triển khai ở buổi 3.");
                case 3 -> ConsoleUtil.printWarning("Chức năng Quản lý đăng ký — sẽ triển khai ở buổi 4.");
                case 4 -> ConsoleUtil.printWarning("Chức năng Thống kê — sẽ triển khai ở buổi 4.");
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
