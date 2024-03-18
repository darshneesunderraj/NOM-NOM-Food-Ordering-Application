import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MainPage extends JPanel {
    private DefaultListModel<String> foodListModel;
    private JList<String> foodList;
    private DefaultListModel<String> cartModel;
    private JList<String> cartList;
    private Map<String, Double> foodPrices;
    private Map<String, Integer> cartContents;

    private String userName;
    private String userAddress;
    private String userPhoneNumber;
    private String paymentMethod;

    public MainPage() {
        setLayout(new BorderLayout());

        foodListModel = new DefaultListModel<>();
        foodList = new JList<>(foodListModel);

        cartModel = new DefaultListModel<>();
        cartList = new JList<>(cartModel);

        // Initialize data structures
        foodPrices = new HashMap<>();
        cartContents = new HashMap<>();

        // Fetch data from the database and populate the foodListModel
        fetchFoodDataFromDatabase();

        JScrollPane foodScrollPane = new JScrollPane(foodList);
        JScrollPane cartScrollPane = new JScrollPane(cartList);

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> addToCart());

        JButton clearCartButton = new JButton("Clear Cart");
        clearCartButton.addActionListener(e -> clearCart());

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> performCheckout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Available Food:"), BorderLayout.NORTH);
        leftPanel.add(foodScrollPane, BorderLayout.CENTER);
        leftPanel.add(addToCartButton, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Cart:"), BorderLayout.NORTH);
        rightPanel.add(cartScrollPane, BorderLayout.CENTER);
        rightPanel.add(clearCartButton, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(checkoutButton, BorderLayout.SOUTH);
    }

    private void fetchFoodDataFromDatabase() {
        // Replace with your database connection details
        String url = "jdbc:mysql://localhost:3306/NomsEat";
        String user = "root";
        String password = "Residents31";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            String query = "SELECT ItemName, Price FROM FoodItems";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String foodItem = resultSet.getString("ItemName");
                double price = resultSet.getDouble("Price");
                foodPrices.put(foodItem, price);
                foodListModel.addElement(foodItem + " - $" + price);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToCart() {
        String selectedFood = foodList.getSelectedValue();
        if (selectedFood != null) {
            int quantity = 1; // You can allow the user to specify quantity
            String foodItem = selectedFood.split(" - ")[0];
            cartContents.put(foodItem, cartContents.getOrDefault(foodItem, 0) + quantity);
            updateCartList();
        }
    }

    private void clearCart() {
        cartContents.clear();
        updateCartList();
    }

    private void updateCartList() {
        cartModel.clear();
        double totalCost = 0.0;
        for (Map.Entry<String, Integer> entry : cartContents.entrySet()) {
            String foodItem = entry.getKey();
            int quantity = entry.getValue();
            double price = foodPrices.get(foodItem);
            double itemCost = quantity * price;
            cartModel.addElement(foodItem + " x" + quantity + " = $" + itemCost);
            totalCost += itemCost;
        }
        cartModel.addElement("Total Cost: $" + totalCost);
    }

    private void performCheckout() {
        // Create a new window for user details and payment method input
        JFrame checkoutFrame = new JFrame("Checkout");
        checkoutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        checkoutFrame.setSize(400, 300);

        JPanel checkoutPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(20);

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(15);

        JLabel paymentLabel = new JLabel("Payment Method:");
        String[] paymentOptions = {"Cash on Delivery", "UPI", "GPay", "Netbanking"};
        JComboBox<String> paymentComboBox = new JComboBox<>(paymentOptions);

        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(e -> {
            userName = nameField.getText();
            userAddress = addressField.getText();
            userPhoneNumber = phoneField.getText();
            paymentMethod = (String) paymentComboBox.getSelectedItem();

            if (userName.isEmpty() || userAddress.isEmpty() || userPhoneNumber.isEmpty()) {
                JOptionPane.showMessageDialog(checkoutFrame, "Please fill in all details.");
            } else {
                // Generate the bill and save order details
                generateBill();
                saveOrderDetails();

                // Close the checkout window
                checkoutFrame.dispose();

                // Display a message indicating the order was placed
                JOptionPane.showMessageDialog(this, "Order Placed!\nUser: " + userName + "\nAddress: " + userAddress +
                        "\nPhone: " + userPhoneNumber + "\nPayment Method: " + paymentMethod);
                saveOrderDetails();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        checkoutPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        checkoutPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        checkoutPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        checkoutPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        checkoutPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        checkoutPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        checkoutPanel.add(paymentLabel, gbc);
        gbc.gridx = 1;
        checkoutPanel.add(paymentComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        checkoutPanel.add(placeOrderButton, gbc);

        checkoutFrame.add(checkoutPanel);
        checkoutFrame.setVisible(true);
    }

    private void generateBill() {
        // Implement bill generation logic here
    }

    private void saveOrderDetails() {
    try {
        String fileName = "orders.txt";
        FileWriter writer = new FileWriter(fileName, true); // Append mode

        writer.write("User: " + userName + "\n");
        writer.write("Address: " + userAddress + "\n");
        writer.write("Phone: " + userPhoneNumber + "\n");
        writer.write("Payment Method: " + paymentMethod + "\n");

        // Write the bill details here
        writer.write("Bill Details:\n");
        for (Map.Entry<String, Integer> entry : cartContents.entrySet()) {
            String foodItem = entry.getKey();
            int quantity = entry.getValue();
            double price = foodPrices.get(foodItem);
            double itemCost = quantity * price;
            writer.write(foodItem + " x" + quantity + " = $" + itemCost + "\n");
        }
        double totalCost = calculateTotalCost();
        writer.write("Total Cost: $" + totalCost + "\n\n");

        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private double calculateTotalCost() {
    double totalCost = 0.0;
    for (Map.Entry<String, Integer> entry : cartContents.entrySet()) {
        String foodItem = entry.getKey();
        int quantity = entry.getValue();
        double price = foodPrices.get(foodItem);
        totalCost += quantity * price;
    }
    return totalCost;
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Food Delivery App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            MainPage mainPage = new MainPage();
            frame.add(mainPage);

            frame.setVisible(true);
        });
    }
}

           
