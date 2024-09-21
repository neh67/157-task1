package servelet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        String errorMessage = validateInput(username, password, email);

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } else {
            try {
                // JDBC connection setup
            	Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/UserRegistrationDB", "root", "root");

                // Check if the username is unique
                PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    request.setAttribute("errorMessage", "Username already exists.");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                } else {
                    // Insert user into the database
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
                    stmt.setString(1, username);
                    stmt.setString(2, password); // You can hash the password for security
                    stmt.setString(3, email);
                    stmt.executeUpdate();

                    response.sendRedirect("success.jsp");
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "An error occurred. Please try again.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
        }
    }

    private String validateInput(String username, String password, String email) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
            return "All fields are required.";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (!email.contains("@")) {
            return "Invalid email format.";
        }
        return null;
    }
}
