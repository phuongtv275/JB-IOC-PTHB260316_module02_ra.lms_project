import enums.Role;
import model.Student;
import presentation.AdminView;
import presentation.LoginView;
import presentation.StudentView;
import utils.ConsoleUtil;
import utils.DBUtil;

public class Main {
    static void main(String[] args) {
        System.out.println(ConsoleUtil.CYAN + ConsoleUtil.BOLD);
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║     LMS - Learning Management System         ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println(ConsoleUtil.RESET);

        DBUtil.getConnection();
        LoginView loginView = new LoginView();

        while (true) {
            // Đăng nhập — trả null khi người dùng chọn thoát hẳn
            Student loggedIn = loginView.show();

            if (loggedIn == null) {
                break;
            }

            // Phân luồng theo role
            if (loggedIn.getRole() == Role.ADMIN) {
                new AdminView(loggedIn).show();
            } else {
                new StudentView(loggedIn).show();
            }
            // Sau khi đăng xuất → quay lại màn hình đăng nhập
        }
    }
}
