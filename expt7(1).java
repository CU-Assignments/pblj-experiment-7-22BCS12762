import java.sql.*;
import java.util.Scanner;

public class ProductCRUDApp {
    private static final String URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false); // Enable transaction management

            while (true) {
                System.out.println("\n--- Product CRUD Application ---");
                System.out.println("1. Create Product");
                System.out.println("2. Read Products");
                System.out.println("3. Update Product");
                System.out.println("4. Delete Product");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createProduct(conn, scanner);
                        break;
                    case 2:
                        readProducts(conn);
                        break;
                    case 3:
                        updateProduct(conn, scanner);
                        break;
                    case 4:
                        deleteProduct(conn, scanner);
                        break;
                    case 5:
                        conn.close();
                        System.out.println("Exiting application.");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createProduct(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter Product Name: ");
            String name = scanner.next();
            System.out.print("Enter Price: ");
            double price = scanner.nextDouble();
            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();

            String sql = "INSERT INTO Product (ProductName, Price, Quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, quantity);
                pstmt.executeUpdate();
                conn.commit();
                System.out.println("Product created successfully.");
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private static void readProducts(Connection conn) {
        String sql = "SELECT * FROM Product";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Product List ---");
            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("ProductID") +
                    ", Name: " + rs.getString("ProductName") +
                    ", Price: " + rs.getDouble("Price") +
                    ", Quantity: " + rs.getInt("Quantity")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateProduct(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter Product ID to update: ");
            int id = scanner.nextInt();
            System.out.print("Enter new Product Name: ");
            String name = scanner.next();
            System.out.print("Enter new Price: ");
            double price = scanner.nextDouble();
            System.out.print("Enter new Quantity: ");
            int quantity = scanner.nextInt();

            String sql = "UPDATE Product SET ProductName=?, Price=?, Quantity=? WHERE ProductID=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, quantity);
                pstmt.setInt(4, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Product updated successfully.");
                } else {
                    System.out.println("Product not found.");
                }
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private static void deleteProduct(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter Product ID to delete: ");
            int id = scanner.nextInt();

            String sql = "DELETE FROM Product WHERE ProductID=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    System.out.println("Product deleted successfully.");
                } else {
                    System.out.println("Product not found.");
                }
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
