import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class LoginPage {
    private static final String CREDENTIALS_FILE = "user_credentials.txt"; // File to store user credentials
    private static final Map<String, String> userDatabase = new HashMap<>(); // Store user credentials in-memory

    public static void main(String[] args) {
        loadUserCredentials(); // Load user credentials from the file

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Food Delivery App");
            frame.setResizable(false); // Disable window resizing
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Calculate dimensions for a 9:16 aspect ratio (e.g., width: 360, height: 640)
            int windowWidth = 800;
            int windowHeight = 600;
            frame.setSize(windowWidth, windowHeight);

            JPanel panel = new JPanel(new GridBagLayout());

            // Create a card layout for switching between login/signup and the main page
            CardLayout cardLayout = new CardLayout();
            JPanel cards = new JPanel(cardLayout);

            JPanel loginPanel = createLoginPanel(cardLayout, cards);
            JPanel signupPanel = createSignupPanel(cardLayout, cards);
            // Create and add the main page
            MainPage mainPage = new MainPage();
            cards.add(loginPanel, "LOGIN");
            cards.add(signupPanel, "SIGNUP");
            cards.add(mainPage, "MAIN");

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            panel.add(cards, gbc);

            frame.add(panel);
            frame.setVisible(true);
        });
    }

    private static JPanel createLoginPanel(CardLayout cardLayout, JPanel cards) {
        JPanel loginPanel = new JPanel(new GridBagLayout());

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton goToSignupButton = new JButton("Sign Up");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Create and add labels to the left of text fields
        JLabel emailLabel = new JLabel("Enter Email:");
        JLabel passwordLabel = new JLabel("Enter Password:");
        
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(emailLabel, gbc);
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);
        gbc.gridy = 0;
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);
        gbc.gridy = 1;
        loginPanel.add(passwordField, gbc);
        
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        loginPanel.add(goToSignupButton, gbc);

        // Set preferred size for buttons
        Dimension buttonSize = new Dimension(120, 30);
        loginButton.setPreferredSize(buttonSize);
        goToSignupButton.setPreferredSize(buttonSize);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginPanel, "Please fill in both username and password.");
                return;
            }

            if (!isValidEmail(username)) {
                JOptionPane.showMessageDialog(loginPanel, "Please enter a valid email address.");
                return;
            }

            // Check user credentials
            if (checkUserCredentials(username, password)) {
                cardLayout.show(cards, "MAIN"); // Switch to the main page
                usernameField.setText(""); // Clear the fields
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(loginPanel, "Invalid credentials. Please try again.");
            }
        });

        goToSignupButton.addActionListener(e -> cardLayout.show(loginPanel.getParent(), "SIGNUP"));

        return loginPanel;
    }

    private static JPanel createSignupPanel(CardLayout cardLayout, JPanel cards) {
        JPanel signupPanel = new JPanel(new GridBagLayout());

        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton signupButton = new JButton("Sign Up");
        JButton goToLoginButton = new JButton("Back to Login");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create and add labels to the left of text fields
        JLabel emailLabel = new JLabel("Enter Email:");
        JLabel passwordLabel = new JLabel("Enter Password:");

        gbc.anchor = GridBagConstraints.WEST;
        signupPanel.add(emailLabel, gbc);
        gbc.gridy = 1;
        signupPanel.add(passwordLabel, gbc);
        gbc.gridy = 0;
        gbc.gridx = 1;
        signupPanel.add(emailField, gbc);
        gbc.gridy = 1;
        signupPanel.add(passwordField, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        signupPanel.add(signupButton, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        signupPanel.add(goToLoginButton, gbc);

        // Set preferred size for buttons
        Dimension buttonSize = new Dimension(120, 30);
        signupButton.setPreferredSize(buttonSize);
        goToLoginButton.setPreferredSize(buttonSize);

        signupButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(signupPanel, "Please fill in both email and password.");
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(signupPanel, "Please enter a valid email address.");
                return;
            }

            if (userExists(email)) {
                JOptionPane.showMessageDialog(signupPanel, "An account with this email already exists.");
            } else {
                // Simulate sending an OTP via email (you should implement a real OTP sending logic)
                String otp = generateOTP();
                sendOTPByEmail(email, otp);

                // Simulate OTP verification
                String enteredOTP = JOptionPane.showInputDialog("Enter the OTP sent to your email:");
                if (enteredOTP != null && enteredOTP.equals(otp)) {
                    // Store user credentials in-memory and save to the file
                    userDatabase.put(email, password);
                    saveUserCredentials(email, password);
                    cardLayout.show(cards, "MAIN"); // Switch to the main page
                    emailField.setText(""); // Clear the fields
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(signupPanel, "OTP verification failed. Please try again.");
                }
            }
        });

        goToLoginButton.addActionListener(e -> cardLayout.show(signupPanel.getParent(), "LOGIN"));

        return signupPanel;
    }

    // Simulate OTP generation (replace with a real OTP generation logic)
    private static String generateOTP() {
        Random rand = new Random();

        // Generate a random 4-digit number
        int min = 1000; // The minimum 4-digit number
        int max = 9999; // The maximum 4-digit number
        int randomNumber = rand.nextInt(max - min + 1) + min;

        // Convert the random number to a string
        String randomString = String.valueOf(randomNumber);

        System.out.println("OTP: " + randomString);
        return randomString; // A fixed OTP for demonstration purposes
    }

    // Simulate sending OTP via email (replace with real email sending logic)
    private static void sendOTPByEmail(String email, String otp) {
        // You can implement email sending here using JavaMail or an email API
        // For demonstration purposes, this is left empty.
    }

    // Validate email address using a regular expression
    private static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Save user credentials to a text file
    private static void saveUserCredentials(String email, String password) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE, true));
            writer.write(email + ":" + password);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if a user with the given email already exists
    private static boolean userExists(String email) {
        return userDatabase.containsKey(email);
    }

    // Check user credentials in-memory
    private static boolean checkUserCredentials(String email, String password) {
        return userDatabase.containsKey(email) && userDatabase.get(email).equals(password);
    }

    // Load user credentials from the file
    private static void loadUserCredentials() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userDatabase.put(parts[0], parts[1]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}