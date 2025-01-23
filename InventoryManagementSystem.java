import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class InventoryManagementSystem extends JFrame {
    private ArrayList<Product> inventory = new ArrayList<>();
    private JTextArea inventoryDisplayArea;

    public InventoryManagementSystem() {
        // Set up the main window
        setTitle("Inventory Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JLabel headerLabel = new JLabel("Inventory Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(50, 120, 180));
        add(headerLabel, BorderLayout.NORTH);

        // Inventory Display Area
        inventoryDisplayArea = new JTextArea();
        inventoryDisplayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(inventoryDisplayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Inventory"));
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));

        JButton addProductButton = new JButton("Add Product");
        JButton searchProductButton = new JButton("Search Product");
        JButton saveLoadButton = new JButton("Save & Load");

        buttonPanel.add(addProductButton);
        buttonPanel.add(searchProductButton);
        buttonPanel.add(saveLoadButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add Product Button Action
        addProductButton.addActionListener(e -> showAddProductForm());

        // Search Product Button Action
        searchProductButton.addActionListener(e -> searchProduct());

        // Save and Load Button Action
        saveLoadButton.addActionListener(e -> saveAndLoadData());

        // Load Current Inventory on Startup
        loadInventory();
        updateInventoryDisplay();
    }

    private void showAddProductForm() {
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Fields
        JLabel idLabel = new JLabel("Product ID:");
        JTextField idField = new JTextField();

        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField();

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(quantityLabel);
        formPanel.add(quantityField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);

        // Form Buttons
        JButton submitButton = new JButton("Add Product");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // Dialog
        JDialog formDialog = new JDialog(this, "Add Product", true);
        formDialog.setLayout(new BorderLayout());
        formDialog.add(formPanel, BorderLayout.CENTER);
        formDialog.add(buttonPanel, BorderLayout.SOUTH);
        formDialog.setSize(400, 300);
        formDialog.setLocationRelativeTo(this);

        // Submit Button Action
        submitButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String quantityStr = quantityField.getText().trim();
            String priceStr = priceField.getText().trim();

            try {
                int quantity = Integer.parseInt(quantityStr);
                double price = Double.parseDouble(priceStr);
                inventory.add(new Product(id, name, quantity, price));
                updateInventoryDisplay();
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                formDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for quantity or price. Please try again.");
            }
        });

        // Cancel Button Action
        cancelButton.addActionListener(e -> formDialog.dispose());

        formDialog.setVisible(true);
    }

    private void searchProduct() {
        String searchTerm = JOptionPane.showInputDialog(this, "Enter Product ID or Name to Search:");
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return;
        }

        boolean found = false;
        for (Product product : inventory) {
            if (product.getId().equalsIgnoreCase(searchTerm) || product.getName().equalsIgnoreCase(searchTerm)) {
                JOptionPane.showMessageDialog(this, "Product Found: " + product);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Product not found.");
        }
    }

    private void saveAndLoadData() {
        int choice = JOptionPane.showConfirmDialog(this, "Do you want to save the data?");
        if (choice == JOptionPane.YES_OPTION) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("inventory.dat"))) {
                oos.writeObject(inventory);
                JOptionPane.showMessageDialog(this, "Inventory data saved successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
            }
        }

        choice = JOptionPane.showConfirmDialog(this, "Do you want to load the data?");
        if (choice == JOptionPane.YES_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("inventory.dat"))) {
                inventory = (ArrayList<Product>) ois.readObject();
                updateInventoryDisplay();
                JOptionPane.showMessageDialog(this, "Inventory data loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
            }
        }
    }

    private void loadInventory() {
        File file = new File("inventory.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                inventory = (ArrayList<Product>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading inventory on startup: " + e.getMessage());
            }
        }
    }

    private void updateInventoryDisplay() {
        inventoryDisplayArea.setText("ID\tName\tQuantity\tPrice\n");
        inventoryDisplayArea.append("----------------------------------------------------\n");
        for (Product product : inventory) {
            inventoryDisplayArea.append(product + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryManagementSystem().setVisible(true));
    }
}

class Product implements Serializable {
    private String id;
    private String name;
    private int quantity;
    private double price;

    public Product(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + quantity + "\t" + price;
    }
}
