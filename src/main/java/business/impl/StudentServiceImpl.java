package business.impl;

import business.IStudentService;
import dao.IStudentDAO;
import dao.impl.StudentDAOImpl;
import enums.Role;
import model.Student;
import utils.PasswordUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class StudentServiceImpl implements IStudentService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final DateTimeFormatter DOB_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        Optional<Student> studentOpt = studentDAO.findUserByEmail(email);

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

    // ── getAllStudents ────────────────────────────────────────────

    @Override
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    // ── getStudentById ────────────────────────────────────────────

    @Override
    public Optional<Student> getStudentById(int id) {
        return studentDAO.findById(id);
    }

    // ── addStudent ────────────────────────────────────────────────

    @Override
    public String addStudent(String name, String dobStr, String email, String sexStr,
                             String phone, String password) {
        if (name == null || name.isBlank())
            return "Họ tên không được để trống.";
        if (dobStr == null || dobStr.isBlank())
            return "Ngày sinh không được để trống.";
        if (email == null || email.isBlank())
            return "Email không được để trống.";
        if (sexStr == null || sexStr.isBlank())
            return "Giới tính không được để trống.";
        if (password == null || password.isBlank())
            return "Mật khẩu không được để trống.";

        LocalDate dob;
        try {
            dob = LocalDate.parse(dobStr.trim(), DOB_FORMAT);
        } catch (DateTimeParseException e) {
            return "Ngày sinh không hợp lệ. Định dạng: yyyy-MM-dd (vd: 2000-01-31).";
        }
        if (dob.isAfter(LocalDate.now()))
            return "Ngày sinh không được lớn hơn ngày hiện tại.";

        if (!EMAIL_PATTERN.matcher(email.trim()).matches())
            return "Email không đúng định dạng.";
        if (studentDAO.existsByEmail(email.trim(), -1))
            return "Email \"" + email.trim() + "\" đã được sử dụng.";

        Boolean sex = parseSex(sexStr);
        if (sex == null)
            return "Giới tính không hợp lệ. Vui lòng nhập \"Nam\" hoặc \"Nữ\".";

        Student student = new Student();
        student.setName(name.trim());
        student.setDob(dob);
        student.setEmail(email.trim());
        student.setSex(sex);
        student.setPhone((phone == null || phone.isBlank()) ? null : phone.trim());
        student.setRole(Role.STUDENT);
        student.setPassword(PasswordUtil.hash(password));

        studentDAO.save(student);
        return null;
    }

    // ── updateStudentName ─────────────────────────────────────────

    @Override
    public String updateStudentName(int id, String newName) {
        if (newName == null || newName.isBlank())
            return "Họ tên không được để trống.";

        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) return "Không tìm thấy học viên với id=" + id;

        Student student = opt.get();
        student.setName(newName.trim());
        studentDAO.update(student);
        return null;
    }

    // ── updateStudentDob ──────────────────────────────────────────

    @Override
    public String updateStudentDob(int id, String newDobStr) {
        if (newDobStr == null || newDobStr.isBlank())
            return "Ngày sinh không được để trống.";

        LocalDate dob;
        try {
            dob = LocalDate.parse(newDobStr.trim(), DOB_FORMAT);
        } catch (DateTimeParseException e) {
            return "Ngày sinh không hợp lệ. Định dạng: yyyy-MM-dd (vd: 2000-01-31).";
        }
        if (dob.isAfter(LocalDate.now()))
            return "Ngày sinh không được lớn hơn ngày hiện tại.";

        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) return "Không tìm thấy học viên với id=" + id;

        Student student = opt.get();
        student.setDob(dob);
        studentDAO.update(student);
        return null;
    }

    // ── updateStudentEmail ────────────────────────────────────────

    @Override
    public String updateStudentEmail(int id, String newEmail) {
        if (newEmail == null || newEmail.isBlank())
            return "Email không được để trống.";
        if (!EMAIL_PATTERN.matcher(newEmail.trim()).matches())
            return "Email không đúng định dạng.";
        if (studentDAO.existsByEmail(newEmail.trim(), id))
            return "Email \"" + newEmail.trim() + "\" đã được sử dụng bởi tài khoản khác.";

        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) return "Không tìm thấy học viên với id=" + id;

        Student student = opt.get();
        student.setEmail(newEmail.trim());
        studentDAO.update(student);
        return null;
    }

    // ── updateStudentPhone ────────────────────────────────────────

    @Override
    public String updateStudentPhone(int id, String newPhone) {
        // Phone được phép NULL theo schema — cho phép để trống để xóa số điện thoại
        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) return "Không tìm thấy học viên với id=" + id;

        Student student = opt.get();
        student.setPhone((newPhone == null || newPhone.isBlank()) ? null : newPhone.trim());
        studentDAO.update(student);
        return null;
    }

    // ── updateStudentSex ──────────────────────────────────────────

    @Override
    public String updateStudentSex(int id, String newSexStr) {
        Boolean sex = parseSex(newSexStr);
        if (sex == null)
            return "Giới tính không hợp lệ. Vui lòng nhập \"Nam\" hoặc \"Nữ\".";

        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) return "Không tìm thấy học viên với id=" + id;

        Student student = opt.get();
        student.setSex(sex);
        studentDAO.update(student);
        return null;
    }

    // ── deleteStudent ─────────────────────────────────────────────

    @Override
    public boolean deleteStudent(int id) {
        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) return false;
        studentDAO.deleteById(id);
        return true;
    }

    // ── searchStudent ─────────────────────────────────────────────

    @Override
    public List<Student> searchStudent(String keyword) {
        if (keyword == null || keyword.isBlank()) return studentDAO.findAll();
        return studentDAO.search(keyword.trim());
    }

    // ── getSortedStudents ─────────────────────────────────────────

    @Override
    public List<Student> getSortedStudents(String field, String direction) {
        return studentDAO.findAllSorted(field, direction);
    }
    // ── changePassword ────────────────────────────────────────────

    @Override
    public String changePassword(int studentId, String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword == null || oldPassword.isBlank())
            return "Mật khẩu cũ không được để trống.";
        if (newPassword == null || newPassword.isBlank())
            return "Mật khẩu mới không được để trống.";
        if (confirmPassword == null || confirmPassword.isBlank())
            return "Xác nhận mật khẩu không được để trống.";

        Optional<Student> studentOpt = studentDAO.findById(studentId);
        if (studentOpt.isEmpty())
            return "Không tìm thấy tài khoản.";

        Student student = studentOpt.get();

        if (!PasswordUtil.verify(oldPassword, student.getPassword()))
            return "Mật khẩu cũ không đúng.";

        if (!newPassword.equals(confirmPassword))
            return "Mật khẩu mới và xác nhận mật khẩu không khớp.";

        if (newPassword.equals(oldPassword))
            return "Mật khẩu mới phải khác mật khẩu cũ.";

        if (newPassword.length() < 6)
            return "Mật khẩu mới phải có ít nhất 6 ký tự.";

        studentDAO.updatePassword(studentId, PasswordUtil.hash(newPassword));
        return null;
    }

    // ── Helper ────────────────────────────────────────────────────

    private Boolean parseSex(String sexStr) {
        if (sexStr == null) return null;
        String s = sexStr.trim().toLowerCase();
        if (s.equals("nam") || s.equals("nữ") || s.equals("nu")) {
            return s.equals("nam");
        }
        return null;
    }
}
