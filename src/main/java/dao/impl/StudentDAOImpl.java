package dao.impl;

import dao.IStudentDAO;
import enums.Role;
import model.Student;
import utils.DBUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class StudentDAOImpl implements IStudentDAO {

    @Override
    public Optional<Student> findByEmail(String email) {
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, created_at " +
                "FROM student WHERE email = ?";

        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Lỗi findByEmail: " + e.getMessage());
        }

        return Optional.empty();
    }

    // ── Row mapper ────────────────────────────────────────────────

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setDob(rs.getDate("dob").toLocalDate());
        s.setEmail(rs.getString("email"));
        s.setSex(rs.getBoolean("sex"));
        s.setPhone(rs.getString("phone"));
        s.setRole(Role.valueOf(rs.getString("role")));
        s.setPassword(rs.getString("password"));
        s.setCreatedAt(rs.getDate("created_at").toLocalDate());
        return s;
    }
}
