package presentation;

import business.IStudentService;
import business.impl.StudentServiceImpl;
import model.Student;
import utils.ConsoleUtil;

import java.util.List;
import java.util.Optional;

public class StudentManagementView {

    private final IStudentService studentService = new StudentServiceImpl();

    // ── Bảng cột ─────────────────────────────────────────────────
    private static final String[] HEADERS = {"ID", "Họ tên", "Ngày sinh", "Email", "Giới tính", "SĐT", "Ngày tạo"};
    private static final int[]    WIDTHS  = {4, 22, 12, 26, 8, 14, 12};

    // ── Menu chính ────────────────────────────────────────────────

    public void show() {
        while (true) {
            String[] options = {
                    "Hiển thị danh sách học viên",
                    "Thêm mới học viên",
                    "Chỉnh sửa học viên",
                    "Xóa học viên",
                    "Tìm kiếm (tên / email / id)",
                    "Sắp xếp học viên"
            };
            ConsoleUtil.printMenu("QUẢN LÝ HỌC VIÊN", options, "Quay lại menu Admin");

            int choice = ConsoleUtil.readInt("  Lựa chọn: ");
            System.out.println();

            switch (choice) {
                case 1 -> showList(studentService.getAllStudents());
                case 2 -> addStudent();
                case 3 -> editStudent();
                case 4 -> deleteStudent();
                case 5 -> searchStudent();
                case 6 -> sortStudent();
                case 0 -> { return; }
                default -> ConsoleUtil.printError("Lựa chọn không hợp lệ.");
            }

            ConsoleUtil.pressEnterToContinue();
        }
    }

    // ── 1. Hiển thị danh sách ─────────────────────────────────────

    private void showList(List<Student> students) {
        ConsoleUtil.printTitle("DANH SÁCH HỌC VIÊN");
        ConsoleUtil.printTable(HEADERS, toRows(students), WIDTHS);
        ConsoleUtil.printInfo("Tổng số: " + students.size() + " học viên.");
    }

    // ── 2. Thêm mới ───────────────────────────────────────────────

    private void addStudent() {
        ConsoleUtil.printTitle("THÊM MỚI HỌC VIÊN");

        String name     = ConsoleUtil.readLine("  Họ tên              : ");
        String dob      = ConsoleUtil.readLine("  Ngày sinh (yyyy-MM-dd): ");
        String email    = ConsoleUtil.readLine("  Email               : ");
        String sex      = ConsoleUtil.readLine("  Giới tính (Nam/Nữ)  : ");
        String phone    = ConsoleUtil.readLine("  Số điện thoại (có thể bỏ trống): ");
        String password = ConsoleUtil.readLine("  Mật khẩu            : ");

        String error = studentService.addStudent(name, dob, email, sex, phone, password);
        if (error != null) {
            ConsoleUtil.printError(error);
        } else {
            ConsoleUtil.printSuccess("Thêm học viên thành công!");
        }
    }

    // ── 3. Chỉnh sửa ──────────────────────────────────────────────

    private void editStudent() {
        ConsoleUtil.printTitle("CHỈNH SỬA HỌC VIÊN");
        showList(studentService.getAllStudents());

        int id = ConsoleUtil.readInt("  Nhập ID học viên cần sửa (0 để hủy): ");
        if (id == 0) return;

        Optional<Student> opt = studentService.getStudentById(id);
        if (opt.isEmpty()) {
            ConsoleUtil.printError("Không tìm thấy học viên với ID = " + id);
            return;
        }

        Student current = opt.get();
        ConsoleUtil.printInfo("Đang sửa: [" + current.getId() + "] " + current.getName());

        // Menu con chọn thuộc tính cần sửa
        while (true) {
            String[] subOptions = {
                    "Họ tên          (hiện tại: " + current.getName() + ")",
                    "Ngày sinh       (hiện tại: " + current.getDob() + ")",
                    "Email           (hiện tại: " + current.getEmail() + ")",
                    "Số điện thoại   (hiện tại: " + (current.getPhone() != null ? current.getPhone() : "—") + ")",
                    "Giới tính       (hiện tại: " + current.getSexDisplay() + ")"
            };
            ConsoleUtil.printMenu("CHỌN THUỘC TÍNH CẦN SỬA", subOptions, "Xong / Quay lại");

            int field = ConsoleUtil.readInt("  Lựa chọn: ");
            if (field == 0) break;

            String error = switch (field) {
                case 1 -> {
                    String val = ConsoleUtil.readLine("  Họ tên mới: ");
                    yield studentService.updateStudentName(id, val);
                }
                case 2 -> {
                    String val = ConsoleUtil.readLine("  Ngày sinh mới (yyyy-MM-dd): ");
                    yield studentService.updateStudentDob(id, val);
                }
                case 3 -> {
                    String val = ConsoleUtil.readLine("  Email mới: ");
                    yield studentService.updateStudentEmail(id, val);
                }
                case 4 -> {
                    String val = ConsoleUtil.readLine("  Số điện thoại mới (để trống để xóa): ");
                    yield studentService.updateStudentPhone(id, val);
                }
                case 5 -> {
                    String val = ConsoleUtil.readLine("  Giới tính mới (Nam/Nữ): ");
                    yield studentService.updateStudentSex(id, val);
                }
                default -> { ConsoleUtil.printError("Lựa chọn không hợp lệ."); yield "skip"; }
            };

            if (error == null) {
                ConsoleUtil.printSuccess("Cập nhật thành công!");
                current = studentService.getStudentById(id).orElse(current);
            } else if (!error.equals("skip")) {
                ConsoleUtil.printError(error);
            }
        }
    }

    // ── 4. Xóa ───────────────────────────────────────────────────

    private void deleteStudent() {
        ConsoleUtil.printTitle("XÓA HỌC VIÊN");
        showList(studentService.getAllStudents());

        int id = ConsoleUtil.readInt("  Nhập ID học viên cần xóa (0 để hủy): ");
        if (id == 0) return;

        Optional<Student> opt = studentService.getStudentById(id);
        if (opt.isEmpty()) {
            ConsoleUtil.printError("Không tìm thấy học viên với ID = " + id);
            return;
        }

        Student student = opt.get();
        ConsoleUtil.printWarning("Bạn có chắc muốn xóa học viên: \"" + student.getName() + "\" ("
                + student.getEmail() + ")?");
        ConsoleUtil.printWarning("Lưu ý: các đăng ký khóa học liên quan cũng sẽ bị xóa (ON DELETE CASCADE).");
        String confirm = ConsoleUtil.readLine("  Xác nhận (Y/N): ");

        if (confirm.equalsIgnoreCase("Y")) {
            boolean deleted = studentService.deleteStudent(id);
            if (deleted) {
                ConsoleUtil.printSuccess("Đã xóa học viên \"" + student.getName() + "\".");
            } else {
                ConsoleUtil.printError("Xóa thất bại. Vui lòng thử lại.");
            }
        } else {
            ConsoleUtil.printInfo("Đã hủy thao tác xóa.");
        }
    }

    // ── 5. Tìm kiếm ───────────────────────────────────────────────

    private void searchStudent() {
        ConsoleUtil.printTitle("TÌM KIẾM HỌC VIÊN (theo tên / email / id)");
        String keyword = ConsoleUtil.readLine("  Nhập từ khóa: ");
        List<Student> results = studentService.searchStudent(keyword);

        if (results.isEmpty()) {
            ConsoleUtil.printWarning("Không tìm thấy học viên nào với từ khóa \"" + keyword + "\".");
        } else {
            ConsoleUtil.printInfo("Tìm thấy " + results.size() + " kết quả cho \"" + keyword + "\":");
            ConsoleUtil.printTable(HEADERS, toRows(results), WIDTHS);
        }
    }

    // ── 6. Sắp xếp ────────────────────────────────────────────────

    private void sortStudent() {
        String[] subOptions = {
                "Theo tên — Tăng dần (A → Z)",
                "Theo tên — Giảm dần (Z → A)",
                "Theo ID  — Tăng dần",
                "Theo ID  — Giảm dần"
        };
        ConsoleUtil.printMenu("SẮP XẾP HỌC VIÊN", subOptions, "Quay lại");

        int choice = ConsoleUtil.readInt("  Lựa chọn: ");
        if (choice == 0) return;

        List<Student> sorted = switch (choice) {
            case 1 -> studentService.getSortedStudents("name", "ASC");
            case 2 -> studentService.getSortedStudents("name", "DESC");
            case 3 -> studentService.getSortedStudents("id", "ASC");
            case 4 -> studentService.getSortedStudents("id", "DESC");
            default -> { ConsoleUtil.printError("Lựa chọn không hợp lệ."); yield List.of(); }
        };

        if (!sorted.isEmpty()) showList(sorted);
    }

    // ── Helper: Student[] → String[][] ───────────────────────────

    private String[][] toRows(List<Student> students) {
        return students.stream().map(s -> new String[]{
                String.valueOf(s.getId()),
                s.getName(),
                s.getDob() != null ? s.getDob().toString() : "",
                s.getEmail(),
                s.getSexDisplay(),
                s.getPhone() != null ? s.getPhone() : "—",
                s.getCreatedAt() != null ? s.getCreatedAt().toString() : ""
        }).toArray(String[][]::new);
    }
}
