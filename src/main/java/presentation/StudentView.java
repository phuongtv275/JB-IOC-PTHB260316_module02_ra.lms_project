package presentation;

import model.Student;
import utils.ConsoleUtil;

public class StudentView {
    private final Student student;

    public StudentView(Student student) {
        this.student = student;
    }

    public void show() {
        while (true) {
            String[] options = {
                    "Xem danh sách khóa học",
                    "Đăng ký khóa học",
                    "Xem khóa học đã đăng ký",
                    "Hủy đăng ký",
                    "Đổi mật khẩu"
            };
            ConsoleUtil.printMenu("MENU HỌC VIÊN — " + student.getName(), options, "Đăng xuất");

            int choice = ConsoleUtil.readInt("  Lựa chọn: ");

            switch (choice) {
                case 1, 2, 3, 4, 5 ->
                        ConsoleUtil.printWarning("Chức năng này sẽ được triển khai ở buổi 4.");
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
