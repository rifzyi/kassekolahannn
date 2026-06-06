package koneksi;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Koneksi instance;
    private static Connection connection;
    private final Properties config = new Properties();

    private Koneksi() {
        config.setProperty("db.host", "localhost");
        config.setProperty("db.port", "3306");
        config.setProperty("db.name", "kas_sekolah");
        config.setProperty("db.user", "root");
        config.setProperty("db.password", "");
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) config.load(in);
        } catch (IOException ex) {
            System.err.println("Gagal membaca config.properties: " + ex.getMessage());
        }
    }

    public static synchronized Koneksi getInstance() {
        if (instance == null) instance = new Koneksi();
        return instance;
    }

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) connection = getInstance().open();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Koneksi database gagal: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    private Connection open() throws SQLException {
        String host = config.getProperty("db.host", "localhost");
        String port = config.getProperty("db.port", "3306");
        String name = config.getProperty("db.name", "kas_sekolah");
        String user = config.getProperty("db.user", "root");
        String pass = config.getProperty("db.password", "");
        String url = config.getProperty("db.url", "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta").trim();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver tidak ditemukan. Tambahkan mysql-connector-j ke Libraries.", "Driver Error", JOptionPane.ERROR_MESSAGE);
        }
        return DriverManager.getConnection(url, user, pass);
    }
}
