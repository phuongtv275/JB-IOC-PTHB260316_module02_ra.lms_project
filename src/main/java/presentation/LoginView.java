package presentation;

import business.IStudentService;
import business.impl.StudentServiceImpl;
import model.Student;
import utils.ConsoleUtil;

import java.util.Optional;

public class LoginView {
    private final IStudentService studentService = new StudentServiceImpl();

    /**
     * Hiển thị màn hình đăng nhập.
     * Vòng lặp đến khi đăng nhập thành công hoặc chọn thoát.
     *
     * @return Student đã đăng nhập, hoặc null nếu người dùng chọn thoát.
     */
    public Student show() {
        while (true) {
            ConsoleUtil.printTitle("HỆ THỐNG QUẢN LÝ KHÓA HỌC - LMS");
            System.out.println("  Vui lòng đăng nhập để tiếp tục.");
            System.out.println();

            String email    = ConsoleUtil.readLine("  Email    : ");
            String password = ConsoleUtil.readLine("  Mật khẩu : ");

            // Validate không để trống
            if (email.isBlank() || password.isBlank()) {
                ConsoleUtil.printError("Email và mật khẩu không được để trống. Vui lòng thử lại.");
                ConsoleUtil.pressEnterToContinue();
                continue;
            }

            Optional<Student> result = studentService.login(email, password);

            if (result.isPresent()) {
                Student student = result.get();
                ConsoleUtil.printSuccess("Chào mừng, " + student.getName()
                        + "! (" + student.getRole() + ")");
                ConsoleUtil.pressEnterToContinue();
                return student;
            } else {
                ConsoleUtil.printError("Email hoặc mật khẩu không đúng. Vui lòng thử lại.");
                ConsoleUtil.pressEnterToContinue();
            }
        }
    }
}
