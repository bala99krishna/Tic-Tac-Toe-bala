import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.sql.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NewPal extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	String name1;
	String play = "0";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewPal frame = new NewPal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NewPal() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Enter Player Name");
		lblNewLabel.setFont(new Font("Algerian", Font.ITALIC, 13));
		lblNewLabel.setBounds(46, 93, 159, 34);
		contentPane.add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(234, 95, 142, 28);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnNewButton = new JButton("JOIN");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				name1 = textField.getText().trim();
				if (name1.isEmpty()) {
					JOptionPane.showMessageDialog(rootPane, "Player name cannot be empty.");
					return;
				}
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					try (
							Connection conn = DriverManager.getConnection(
									"jdbc:mysql://localhost:3306/java_pro", "root", "MySql@root01")) {
						// Check if name already exists
						try (PreparedStatement checkName = conn.prepareStatement(
								"SELECT player_id FROM xox_details WHERE player_name = ?")) {
							checkName.setString(1, name1);
							try (ResultSet nameResult = checkName.executeQuery()) {
								if (nameResult.next()) {
									JOptionPane.showMessageDialog(rootPane,
											"Already Existing Name\nChoose Different Name");
									return;
								}
							}
						}
						// Get next player_id
						int newId = 1;
						try (PreparedStatement maxIdStmt = conn.prepareStatement(
								"SELECT MAX(player_id) AS max_id FROM xox_details");
								ResultSet maxIdResult = maxIdStmt.executeQuery()) {
							if (maxIdResult.next()) {
								newId = maxIdResult.getInt("max_id") + 1;
							}
						}
						// Insert new player
						try (PreparedStatement ps = conn.prepareStatement(
								"INSERT INTO xox_details(player_name, player_id, xwins, owins, ties, gamesplayed, gameslost, gameswon) "
										+
										"VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
							ps.setString(1, name1);
							ps.setInt(2, newId);
							for (int i = 3; i <= 8; i++) {
								ps.setString(i, play);
							}
							int rs = ps.executeUpdate();
							if (rs > 0) {
								JOptionPane.showMessageDialog(rootPane,
										"Go Enjoy Your Game\nYour Gaming Will Be Saved From Now");
							} else {
								JOptionPane.showMessageDialog(rootPane, "Sorry We Failed To Create a Profile For You");
							}
							dispose();
							ProFront spob = new ProFront();
							spob.setVisible(true);
						}
					}
				} catch (ClassNotFoundException ex) {
					JOptionPane.showMessageDialog(rootPane, "MySQL JDBC Driver not found.");
				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(rootPane, "Database error: " + ex.getMessage());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(rootPane, "Error: " + ex.getMessage());
				}
			}
		});
		btnNewButton.setFont(new Font("Cambria Math", Font.BOLD | Font.ITALIC, 13));
		btnNewButton.setBounds(150, 172, 89, 23);
		contentPane.add(btnNewButton);
	}
}