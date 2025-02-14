package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        if (isUseGUI()) {
            new GUIInterface();
        } else {
            new CliInterface();
        }
    }


    private static boolean isUseGUI() {
        try {
            System.out.print("1. GUI\n2. CLI\nSelect user interface: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next("[1-2]");
            return input.equals("1");
        } catch (InputMismatchException e) {
            System.out.println("Choose 1 or 2");
            return isUseGUI();
        }
    }
}

class GUIInterface {
    ATM atm;
    JFrame jFrame;

    public GUIInterface() {
        askHolderName();
//        initFrame();
    }

    private void askHolderName() {
        jFrame = new JFrame("ATM interface");
        jFrame.setSize(400, 200);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setResizable(false);
        jFrame.setAlwaysOnTop(true);
        jFrame.setLocationRelativeTo(null);
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JLabel txt = new JLabel("Enter account holder name");
        JTextField textField = new JTextField(20);
        textField.setToolTipText("Account holder name");
        JButton btn = new JButton("Submit");
        JLabel errorLabel = new JLabel("Enter at least one character");
        btn.addActionListener(e -> {
            if (textField.getText().length() == 0) {
                errorLabel.setForeground(Color.RED);
                jPanel.add(errorLabel, BorderLayout.SOUTH);
                jFrame.revalidate();
            } else {
                jPanel.remove(errorLabel);
                atm = new ATM(textField.getText());
                jFrame.remove(jPanel);
                initFrame();
            }
        });
        jPanel.add(txt);
        jPanel.add(textField);
        jPanel.add(btn);
        jFrame.add(jPanel);
        jFrame.setVisible(true);
    }

    private void initFrame() {
        jFrame.setSize(500, 300);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setResizable(false);
        jFrame.setAlwaysOnTop(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.add(new Label(""), BorderLayout.EAST);
        JPanel jPanel = new JPanel(new GridLayout(1, 2, 20, 10));

        JPanel leftPan = new JPanel(new GridLayout(8, 1, 20, 0));
        JLabel nameLabel = new JLabel("Account holder name", SwingConstants.CENTER);
        JLabel name = new JLabel(atm.holderName(), SwingConstants.CENTER);
        JLabel balanceLabel = new JLabel("Account Balance", SwingConstants.CENTER);
        JLabel balance = new JLabel(String.valueOf(atm.checkBalance()), SwingConstants.CENTER);
        nameLabel.setFont(new Font(nameLabel.getName(), Font.PLAIN, 12));
        name.setFont(new Font(name.getName(), Font.BOLD, 18));
        balanceLabel.setFont(new Font(nameLabel.getName(), Font.PLAIN, 12));
        balance.setFont(new Font(name.getName(), Font.BOLD, 18));
        leftPan.add(new Label(""));
        leftPan.add(nameLabel);
        leftPan.add(name);
        leftPan.add(new Label(""));
        leftPan.add(balanceLabel);
        leftPan.add(balance);


        JPanel rightPan = new JPanel(new GridLayout(6, 1, 20, 5));
        JPanel amountPanel = new JPanel(new FlowLayout());
        JLabel amountLabel = new JLabel("Amount: ");
        JTextField amountTextField = new JTextField(10);
        JLabel errorMessage = new JLabel("Enter valid amount");
        errorMessage.setForeground(Color.RED);
        amountTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    if (amountTextField.getText().length() == 0) {
                        Integer.parseInt(String.valueOf(e.getKeyChar()));
                    } else {
                        Integer.parseInt(amountTextField.getText());
                    }
                    errorMessage.setVisible(false);
                } catch (Exception ee) {
                    errorMessage.setVisible(true);
                }
                jFrame.revalidate();
            }
        });
        amountPanel.add(amountLabel);
        amountPanel.add(amountTextField);
        errorMessage.setVisible(false);
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        depositBtn.addActionListener(e -> {
            try {
                atm.deposit(Integer.parseInt(amountTextField.getText()));
                balance.setText(String.valueOf(atm.checkBalance()));
                amountTextField.setText("");
                rightPan.revalidate();
            } catch (Exception ignored) {
            }
        });
        withdrawBtn.addActionListener(e -> {
            try {
                if (atm.withdraw(Integer.parseInt(amountTextField.getText()))) {
                    balance.setText(String.valueOf(atm.checkBalance()));
                    amountTextField.setText("");
                } else {
                    errorMessage.setVisible(true);
                }

                jFrame.revalidate();
            } catch (Exception ignored) {
            }
        });

        rightPan.add(amountPanel);
        rightPan.add(errorMessage);
        rightPan.add(depositBtn);
        rightPan.add(withdrawBtn);

        jPanel.add(leftPan);
        jPanel.add(rightPan);
        jFrame.add(jPanel);
        jFrame.setVisible(true);
    }
}


class CliInterface {
    ATM atm;

    public CliInterface() {
        run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            if (atm == null) {
                newAccount();
            } else {
                try {
                    System.out.println("1. Check balance\n2. Deposit\n3. Withdraw\n4. Logout\n5. Exit");
                    System.out.print("Choose operation: ");
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.next("[1-5]");
                    switch (Integer.parseInt(input)) {
                        case 1 -> getBalance();
                        case 2 -> depositMoney();
                        case 3 -> withdrawMoney();
                        case 4 -> logout();
                        case 5 -> running = false;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Choose between 1-5");
                }
            }
        }
    }

    private void logout() {
        atm = null;
    }

    private void withdrawMoney() {
        try {
            System.out.print("\nEnter withdraw amount: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next(Pattern.compile("^[0-9]*$"));
            if (atm.withdraw(Integer.parseInt(input))) {
                System.out.println("-Withdraw successful");
            } else {
                System.out.println("-Withdraw unsuccessful");
                System.out.println("Reason: Account does not have enough money.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Enter a valid integer");
            depositMoney();
        }
    }

    private void depositMoney() {
        try {
            System.out.print("\nEnter deposit amount: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next(Pattern.compile("^[0-9]*$"));
            atm.deposit(Integer.parseInt(input));
            System.out.println("+Deposit successful");
        } catch (InputMismatchException e) {
            System.out.println("Enter a valid integer");
            depositMoney();
        }
    }

    private void getBalance() {
        System.out.println("-------------------");
        System.out.println("Account Holder Name: " + atm.holderName());
        System.out.println("Account Balance    : " + atm.checkBalance());
        System.out.println("-------------------");
    }

    private void newAccount() {
        System.out.println();
        System.out.print("Enter account holder name: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        atm = new ATM(name);
    }
}

class ATM {
    private final Bank bank;

    public ATM(String accountHolderName) {
        bank = new Bank(accountHolderName);
    }

    public float checkBalance() {
        return bank.getBalance();
    }

    public String holderName() {
        return bank.getName();
    }

    public void deposit(int amount) {
        bank.addBalance(amount);
    }

    public boolean withdraw(int amount) {
        if (bank.getBalance() >= amount) {
            bank.deductBalance(amount);
            return true;
        } else {
            return false;
        }
    }
}

class Bank {
    private final String name;
    private float balance;

    public Bank(String holderName) {
        name = holderName;
        balance = 0;
    }

    public String getName() {
        return name;
    }

    public float getBalance() {
        return balance;
    }

    public void addBalance(int amount) {
        balance += amount;
    }

    public void deductBalance(int amount) {
        balance -= amount;
    }
}