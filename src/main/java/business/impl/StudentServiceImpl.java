package business.impl;

import business.IStudentService;
import dao.IStudentDAO;
import dao.impl.StudentDAOImpl;
import model.Student;
import utils.PasswordUtil;

import java.util.Optional;

public class StudentServiceImpl implements IStudentService {
    private final IStudentDAO studentDAO;

    public StudentServiceImpl() {
        this.studentDAO = new StudentDAOImpl();
    }

    // ── login ────────────────────────────────────────────────────


    @Override
    public Optional<Student> login(String email, String password) {
        // Validate không để trống
        if (email == null || email.isBlank()) {
            System.err.println("[StudentService] Email không được để trống.");
            return Optional.empty();
        }
        if (password == null || password.isBlank()) {
            System.err.println("[StudentService] Mật khẩu không được để trống.");
            return Optional.empty();
        }

        Optional<Student> studentOpt = studentDAO.findByEmail(email);

        if (studentOpt.isEmpty()) {
            return Optional.empty();
        }

        Student student = studentOpt.get();

        if (!PasswordUtil.verify(password, student.getPassword())) {
            return Optional.empty();
        }

        System.out.println("[StudentService] Đăng nhập thành công: "
                + student.getEmail() + " | role=" + student.getRole());
        return Optional.of(student);
    }
}
