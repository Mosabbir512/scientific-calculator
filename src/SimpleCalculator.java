import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleCalculator extends JFrame implements ActionListener {

    // Display
    private JTextField display;

    // Calculator variables
    private String currentInput = "";
    private String previousInput = "";
    private String operator = "";
    private boolean isNewInput = true;

    // Memory
    private double memory = 0;

    public SimpleCalculator() {
        setTitle("🧮 Simple Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Display
        display = new JTextField("0");
        display.setFont(new Font("Segoe UI", Font.BOLD, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(display, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 4, 5, 5));
        buttonPanel.setBackground(new Color(240, 240, 240));

        // Button Labels
        String[] buttons = {
                "MC", "MR", "M+", "M-",
                "C", "⌫", "%", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "−",
                "1", "2", "3", "+",
                "0", ".", "±", "="
        };

        // Create buttons
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Segoe UI", Font.BOLD, 18));
            button.setFocusPainted(false);
            button.setBackground(getButtonColor(text));
            button.setForeground(getButtonForeground(text));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            button.addActionListener(this);

            // Hover effect
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(button.getBackground().darker());
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(getButtonColor(text));
                }
            });

            buttonPanel.add(button);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Keyboard support
        setupKeyboardListener();
    }

    private Color getButtonColor(String text) {
        if (text.matches("[0-9]") || text.equals(".")) {
            return Color.WHITE;
        } else if (text.equals("C") || text.equals("⌫")) {
            return new Color(255, 200, 200);
        } else if (text.equals("=")) {
            return new Color(50, 150, 255);
        } else if (text.startsWith("M")) {
            return new Color(230, 200, 255);
        } else {
            return new Color(240, 240, 255);
        }
    }

    private Color getButtonForeground(String text) {
        if (text.equals("C") || text.equals("⌫")) {
            return new Color(180, 0, 0);
        } else if (text.equals("=")) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    private void setupKeyboardListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        String key = KeyEvent.getKeyText(e.getKeyCode());
                        switch (key) {
                            case "0": case "1": case "2": case "3": case "4":
                            case "5": case "6": case "7": case "8": case "9":
                                appendNumber(key);
                                break;
                            case "Period":
                                appendDecimal();
                                break;
                            case "Plus":
                                setOperator("+");
                                break;
                            case "Minus":
                                setOperator("−");
                                break;
                            case "Asterisk":
                                setOperator("×");
                                break;
                            case "Slash":
                                setOperator("÷");
                                break;
                            case "Enter":
                            case "Equals":
                                calculate();
                                break;
                            case "Backspace":
                                deleteLast();
                                break;
                            case "Escape":
                                clearAll();
                                break;
                        }
                    }
                    return false;
                });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            // Numbers
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                appendNumber(command);
                break;

            // Decimal
            case ".":
                appendDecimal();
                break;

            // Operators
            case "+": case "−": case "×": case "÷":
                setOperator(command);
                break;

            // Percentage
            case "%":
                calculatePercentage();
                break;

            // Plus/Minus
            case "±":
                toggleSign();
                break;

            // Equals
            case "=":
                calculate();
                break;

            // Clear
            case "C":
                clearAll();
                break;

            // Delete
            case "⌫":
                deleteLast();
                break;

            // Memory functions
            case "MC":
                memoryClear();
                break;
            case "MR":
                memoryRecall();
                break;
            case "M+":
                memoryAdd();
                break;
            case "M-":
                memorySubtract();
                break;
        }
    }

    // Number input
    private void appendNumber(String num) {
        if (isNewInput) {
            currentInput = "";
            isNewInput = false;
        }
        if (currentInput.equals("0") && !num.equals(".")) {
            currentInput = num;
        } else {
            currentInput += num;
        }
        updateDisplay();
    }

    // Decimal input
    private void appendDecimal() {
        if (isNewInput) {
            currentInput = "0.";
            isNewInput = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        updateDisplay();
    }

    // Set operator
    private void setOperator(String op) {
        if (!currentInput.isEmpty()) {
            if (!operator.isEmpty()) {
                calculate();
            }
            previousInput = currentInput;
            operator = op;
            isNewInput = true;
        }
    }

    // Calculate percentage
    private void calculatePercentage() {
        try {
            double value = Double.parseDouble(currentInput);
            value = value / 100;
            currentInput = formatNumber(value);
            updateDisplay();
        } catch (NumberFormatException e) {
            display.setText("Error");
        }
    }

    // Toggle sign
    private void toggleSign() {
        if (!currentInput.isEmpty() && !currentInput.equals("0")) {
            if (currentInput.startsWith("-")) {
                currentInput = currentInput.substring(1);
            } else {
                currentInput = "-" + currentInput;
            }
            updateDisplay();
        }
    }

    // Calculate
    private void calculate() {
        if (previousInput.isEmpty() || currentInput.isEmpty() || operator.isEmpty()) {
            return;
        }

        try {
            double num1 = Double.parseDouble(previousInput);
            double num2 = Double.parseDouble(currentInput);
            double result = 0;

            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "−":
                    result = num1 - num2;
                    break;
                case "×":
                    result = num1 * num2;
                    break;
                case "÷":
                    if (num2 == 0) {
                        display.setText("Cannot divide by zero");
                        return;
                    }
                    result = num1 / num2;
                    break;
            }

            currentInput = formatNumber(result);
            previousInput = "";
            operator = "";
            isNewInput = true;
            updateDisplay();

        } catch (NumberFormatException e) {
            display.setText("Error");
        }
    }

    // Format number (avoid too many decimals)
    private String formatNumber(double num) {
        if (num == (long) num) {
            return String.format("%d", (long) num);
        } else {
            String formatted = String.format("%.10f", num);
            // Remove trailing zeros
            formatted = formatted.replaceAll("0*$", "");
            // Remove decimal point if no decimals
            formatted = formatted.replaceAll("\\.$", "");
            return formatted;
        }
    }

    // Clear all
    private void clearAll() {
        currentInput = "";
        previousInput = "";
        operator = "";
        isNewInput = true;
        display.setText("0");
    }

    // Delete last character
    private void deleteLast() {
        if (!currentInput.isEmpty() && !isNewInput) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            if (currentInput.isEmpty()) {
                currentInput = "0";
                isNewInput = true;
            }
            updateDisplay();
        }
    }

    // Update display
    private void updateDisplay() {
        if (currentInput.isEmpty()) {
            display.setText("0");
        } else {
            display.setText(currentInput);
        }
    }

    // Memory functions
    private void memoryClear() {
        memory = 0;
        showMessage("Memory Cleared");
    }

    private void memoryRecall() {
        currentInput = formatNumber(memory);
        isNewInput = true;
        updateDisplay();
        showMessage("Memory: " + currentInput);
    }

    private void memoryAdd() {
        try {
            memory += Double.parseDouble(currentInput);
            showMessage("Memory: " + formatNumber(memory));
        } catch (NumberFormatException e) {
            showMessage("Invalid number");
        }
    }

    private void memorySubtract() {
        try {
            memory -= Double.parseDouble(currentInput);
            showMessage("Memory: " + formatNumber(memory));
        } catch (NumberFormatException e) {
            showMessage("Invalid number");
        }
    }

    // Show message
    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Memory", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SimpleCalculator().setVisible(true);
        });
    }
}