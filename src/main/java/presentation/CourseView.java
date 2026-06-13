package presentation;

import business.ICourseService;
import business.impl.CourseServiceImpl;
import model.Course;
import utils.ConsoleUtil;

import java.util.List;
import java.util.Optional;

public class CourseView {
    private final ICourseService courseService = new CourseServiceImpl();

    // ── Bảng cột ─────────────────────────────────────────────────
    private static final String[] HEADERS = {"ID", "Tên khóa học", "Thời lượng (h)", "Giảng viên", "Ngày tạo"};
    private static final int[]    WIDTHS  = {4, 30, 14, 22, 12};

    // ── Menu chính ────────────────────────────────────────────────

    public void show() {
        while (true) {
            String[] options = {
                    "Hiển thị danh sách khóa học",
                    "Thêm mới khóa học",
                    "Chỉnh sửa khóa học",
                    "Xóa khóa học",
                    "Tìm kiếm theo tên",
                    "Sắp xếp khóa học"
            };
            ConsoleUtil.printMenu("QUẢN LÝ KHÓA HỌC", options, "Quay lại menu Admin");

            int choice = ConsoleUtil.readInt("  Lựa chọn: ");
            System.out.println();

            switch (choice) {
                case 1 -> showList(courseService.getAllCourses());
                case 2 -> addCourse();
                case 3 -> editCourse();
                case 4 -> deleteCourse();
                case 5 -> searchCourse();
                case 6 -> sortCourse();
                case 0 -> { return; }
                default -> ConsoleUtil.printError("Lựa chọn không hợp lệ.");
            }

            ConsoleUtil.pressEnterToContinue();
        }
    }

    // ── 1. Hiển thị danh sách ─────────────────────────────────────

    private void showList(List<Course> courses) {
        ConsoleUtil.printTitle("DANH SÁCH KHÓA HỌC");
        String[][] rows = toRows(courses);
        ConsoleUtil.printTable(HEADERS, rows, WIDTHS);
        ConsoleUtil.printInfo("Tổng số: " + courses.size() + " khóa học.");
    }

    // ── 2. Thêm mới ───────────────────────────────────────────────

    private void addCourse() {
        ConsoleUtil.printTitle("THÊM MỚI KHÓA HỌC");

        String name       = ConsoleUtil.readLine("  Tên khóa học   : ");
        String duration   = ConsoleUtil.readLine("  Thời lượng (h) : ");
        String instructor = ConsoleUtil.readLine("  Giảng viên     : ");

        String error = courseService.addCourse(name, duration, instructor);
        if (error != null) {
            ConsoleUtil.printError(error);
        } else {
            ConsoleUtil.printSuccess("Thêm khóa học thành công!");
        }
    }

    // ── 3. Chỉnh sửa ──────────────────────────────────────────────

    private void editCourse() {
        ConsoleUtil.printTitle("CHỈNH SỬA KHÓA HỌC");
        showList(courseService.getAllCourses());

        int id = ConsoleUtil.readInt("  Nhập ID khóa học cần sửa (0 để hủy): ");
        if (id == 0) return;

        Optional<Course> opt = courseService.getCourseById(id);
        if (opt.isEmpty()) {
            ConsoleUtil.printError("Không tìm thấy khóa học với ID = " + id);
            return;
        }

        Course current = opt.get();
        ConsoleUtil.printInfo("Đang sửa: [" + current.getId() + "] " + current.getName());

        // Menu con chọn thuộc tính cần sửa
        while (true) {
            String[] subOptions = {
                    "Tên khóa học     (hiện tại: " + current.getName() + ")",
                    "Thời lượng (h)   (hiện tại: " + current.getDuration() + "h)",
                    "Giảng viên       (hiện tại: " + current.getInstructor() + ")"
            };
            ConsoleUtil.printMenu("CHỌN THUỘC TÍNH CẦN SỬA", subOptions, "Xong / Quay lại");

            int field = ConsoleUtil.readInt("  Lựa chọn: ");
            if (field == 0) break;

            String error = switch (field) {
                case 1 -> {
                    String val = ConsoleUtil.readLine("  Tên mới: ");
                    yield courseService.updateCourseName(id, val);
                }
                case 2 -> {
                    String val = ConsoleUtil.readLine("  Thời lượng mới (h): ");
                    yield courseService.updateCourseDuration(id, val);
                }
                case 3 -> {
                    String val = ConsoleUtil.readLine("  Giảng viên mới: ");
                    yield courseService.updateCourseInstructor(id, val);
                }
                default -> { ConsoleUtil.printError("Lựa chọn không hợp lệ."); yield "skip"; }
            };

            if (error == null) {
                ConsoleUtil.printSuccess("Cập nhật thành công!");
                // Reload để hiển thị giá trị hiện tại mới nhất
                current = courseService.getCourseById(id).orElse(current);
            } else if (!error.equals("skip")) {
                ConsoleUtil.printError(error);
            }
        }
    }

    // ── 4. Xóa ───────────────────────────────────────────────────

    private void deleteCourse() {
        ConsoleUtil.printTitle("XÓA KHÓA HỌC");
        showList(courseService.getAllCourses());

        int id = ConsoleUtil.readInt("  Nhập ID khóa học cần xóa (0 để hủy): ");
        if (id == 0) return;

        Optional<Course> opt = courseService.getCourseById(id);
        if (opt.isEmpty()) {
            ConsoleUtil.printError("Không tìm thấy khóa học với ID = " + id);
            return;
        }

        Course course = opt.get();
        ConsoleUtil.printWarning("Bạn có chắc muốn xóa khóa học: \"" + course.getName() + "\"?");
        String confirm = ConsoleUtil.readLine("  Xác nhận (Y/N): ");

        if (confirm.equalsIgnoreCase("Y")) {
            boolean deleted = courseService.deleteCourse(id);
            if (deleted) {
                ConsoleUtil.printSuccess("Đã xóa khóa học \"" + course.getName() + "\".");
            } else {
                ConsoleUtil.printError("Xóa thất bại. Vui lòng thử lại.");
            }
        } else {
            ConsoleUtil.printInfo("Đã hủy thao tác xóa.");
        }
    }

    // ── 5. Tìm kiếm ───────────────────────────────────────────────

    private void searchCourse() {
        ConsoleUtil.printTitle("TÌM KIẾM KHÓA HỌC THEO TÊN");
        String keyword = ConsoleUtil.readLine("  Nhập từ khóa: ");
        List<Course> results = courseService.searchCourseByName(keyword);

        if (results.isEmpty()) {
            ConsoleUtil.printWarning("Không tìm thấy khóa học nào với từ khóa \"" + keyword + "\".");
        } else {
            ConsoleUtil.printInfo("Tìm thấy " + results.size() + " kết quả cho \"" + keyword + "\":");
            ConsoleUtil.printTable(HEADERS, toRows(results), WIDTHS);
        }
    }

    // ── 6. Sắp xếp ────────────────────────────────────────────────

    private void sortCourse() {
        String[] subOptions = {
                "Theo tên — Tăng dần (A → Z)",
                "Theo tên — Giảm dần (Z → A)",
                "Theo ID  — Tăng dần",
                "Theo ID  — Giảm dần"
        };
        ConsoleUtil.printMenu("SẮP XẾP KHÓA HỌC", subOptions, "Quay lại");

        int choice = ConsoleUtil.readInt("  Lựa chọn: ");
        if (choice == 0) return;

        List<Course> sorted = switch (choice) {
            case 1 -> courseService.getSortedCourses("name", "ASC");
            case 2 -> courseService.getSortedCourses("name", "DESC");
            case 3 -> courseService.getSortedCourses("id", "ASC");
            case 4 -> courseService.getSortedCourses("id", "DESC");
            default -> { ConsoleUtil.printError("Lựa chọn không hợp lệ."); yield List.of(); }
        };

        if (!sorted.isEmpty()) showList(sorted);
    }

    // ── Helper: Course[] → String[][] ────────────────────────────

    private String[][] toRows(List<Course> courses) {
        return courses.stream().map(c -> new String[]{
                String.valueOf(c.getId()),
                c.getName(),
                c.getDuration() + " h",
                c.getInstructor(),
                c.getCreatedAt() != null ? c.getCreatedAt().toString() : ""
        }).toArray(String[][]::new);
    }
}
