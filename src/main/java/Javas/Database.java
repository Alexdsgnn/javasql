package Javas;

import java.sql.Connection;
import java.sql.Date; // Важливо для роботи з датами в SQL
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/userdb";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Підключено до бази даних!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Помилка підключення до бази даних!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Відключено від бази даних!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 1. Створюємо складнішу таблицю employees
    public static void createEmployeesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS employees ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "first_name VARCHAR(50) NOT NULL, " // NOT NULL означає, що поле обов'язкове
                + "last_name VARCHAR(50) NOT NULL, "
                + "email VARCHAR(100) UNIQUE, "       // UNIQUE не дозволить додати два однакових email
                + "department VARCHAR(50), "
                + "salary DECIMAL(10, 2), "           // DECIMAL - найкращий тип для грошей (10 цифр всього, 2 після коми)
                + "hire_date DATE"                    // Тип для дати
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблиця 'employees' готова!");
        } catch (SQLException e) {
            System.out.println("Помилка при створенні таблиці!");
            e.printStackTrace();
        }
    }

    // 2. Метод для додавання працівника з усіма параметрами
    public static void insertEmployee(String firstName, String lastName, String email, String department, double salary, String hireDateStr) {
        String sql = "INSERT INTO employees (first_name, last_name, email, department, salary, hire_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, department);
            pstmt.setDouble(5, salary);
            pstmt.setDate(6, Date.valueOf(hireDateStr)); // Перетворюємо рядок формату YYYY-MM-DD у SQL Date

            pstmt.executeUpdate();
            System.out.println("Працівника " + firstName + " " + lastName + " успішно додано!");
        } catch (SQLException e) {
            System.out.println("Помилка при додаванні працівника (можливо, такий email вже існує)!");
            e.printStackTrace(); // Виведе деталі помилки
        }
    }

    public static void main(String[] args) {
        connect();
        createEmployeesTable();

        // 3. Додаємо кількох працівників
        // Зверніть увагу: дата передається у форматі "Рік-Місяць-День" (YYYY-MM-DD)
        insertEmployee("Олена", "Коваленко", "olena.k@company.com", "IT", 3500.50, "2023-01-15");
        insertEmployee("Максим", "Шевченко", "maksym.sh@company.com", "HR", 2100.00, "2023-05-20");
        insertEmployee("Анна", "Бойко", "anna.b@company.com", "Marketing", 2800.75, "2024-02-10");

        // Якщо ви спробуєте розкоментувати рядок нижче, SQL видасть помилку, бо email "olena.k@company.com" вже зайнятий
        // insertEmployee("Ігор", "Петренко", "olena.k@company.com", "IT", 3000.00, "2024-03-01");

        disconnect();
    }
}