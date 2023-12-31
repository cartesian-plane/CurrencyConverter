import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.TimerTask;


public class Converter {
    private static String cachedInput;
    private static String[] currentRates;

    private static boolean isInitialized = false;

    private static int api_call_count = 0;

    private static int timerCount = 0;
    private static final double[][] exchangeRates = {
            {1.0, 1.09, 0.86},  // EUR to {EUR, USD, GBP}
            {0.92, 1.0, 0.79},  // USD to {EUR, USD, GBP}
            {1.16, 1.26, 1.0}    // GBP to {EUR, USD, GBP}
    };


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        final int FRAME_WIDTH = 800;
        final int FRAME_HEIGHT = 600;
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Currency converter");


        if (!isInitialized) {
            currentRates = APIRequest.getCurrentRates().split(",");
            api_call_count++;
        }


        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        JPanel upperPanel = new JPanel(new FlowLayout());

        String[] currencies = {"EUR", "USD", "GBP"};

        JLabel leftLabel = new JLabel("From:");

        JComboBox<String> leftComboBox = new JComboBox<>(currencies);

        JTextField textField = new JTextField(20);

        upperPanel.add(leftLabel);
        upperPanel.add(leftComboBox);
        upperPanel.add(textField);


        JLabel rightLabel = new JLabel("To:");
        JComboBox<String> rightComboBox = new JComboBox<>(currencies);
        upperPanel.add(rightLabel);
        upperPanel.add(rightComboBox);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(upperPanel, gbc);

        JButton converter = new JButton("Convert");

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(converter, gbc);

        JLabel resultLabel = new JLabel("Converted value: ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(resultLabel, gbc);


        Action refresh = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action to be performed when the keystroke is triggered
                currentRates = APIRequest.getCurrentRates().split(",");
                api_call_count++;
                displayUpdateMessage(resultLabel);
            }
        };

        int delay = 10 * 60 * 1000; // 10 minutes in milliseconds
        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Your action to be executed every 10 minutes goes here
                timerCount++;
                System.out.println("Timer clicked: count = " + timerCount);
                displayUpdateMessage(resultLabel);
            }
        });

        timer.start();


        KeyStroke keyStroke = KeyStroke.getKeyStroke("control pressed R");
        // Bind the KeyStroke to the action for the panel
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "triggerKeystroke");
        mainPanel.getActionMap().put("triggerKeystroke", refresh);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userInput = textField.getText();
                if (inputIsValid(userInput)) {
                    System.out.println("Valid input: " + userInput);
                    cachedInput = userInput;
                } else {
                    System.out.println("Rejecting invalid input: " + userInput);
                }

            }
        });


        converter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = textField.getText();
                if (inputIsValid(userInput)) {
                    cachedInput = userInput;


                    String convertFrom = (String) leftComboBox.getSelectedItem();
                    String convertTo = (String) rightComboBox.getSelectedItem();

                    if (convertFrom.equals(convertTo)) {
                        String message = "Converting from " + convertFrom + " to " + convertTo + " does nothing.";
                        resultLabel.setText(message);
                    }


//                    int initialCurrencyIndex = getIndexForCurrency(convertFrom);
//                    int targetCurrencyIndex = getIndexForCurrency(convertTo);

                    //double exchangeRate = exchangeRates[initialCurrencyIndex][targetCurrencyIndex];
                    double exchangeRate = getExchangeRate(convertFrom, convertTo);
                    double initialValue = Double.parseDouble(cachedInput);
                    double convertedValue = initialValue * exchangeRate;

                    BigDecimal roundedAmount = new BigDecimal(convertedValue).setScale(2, RoundingMode.HALF_UP);
                    String currencySymbol = getCurrencySymbol(convertTo);

                    String message = "Converted value: " + currencySymbol + roundedAmount;
                    resultLabel.setText(message);


                    double rate = getExchangeRate("EUR", "GBP");
                    System.out.println("Final rate: " + rate);


                } else {
                    String message = "Invalid input: " + userInput;
                    resultLabel.setText(message);
                }


            }
        });

        frame.add(mainPanel);

        frame.setVisible(true);
    }


    private static boolean inputIsValid(String input) {
        if (input.equals("")) {
            return false;
        }
        boolean isValid = input.matches("^[0-9]+(\\.[0-9]+)?$") && input.length() <= 18;

        return isValid;
    }

    private static int getIndexForCurrency(String currency) {
        switch (currency) {
            case "EUR":
                return 0;
            case "USD":
                return 1;
            case "GBP":
                return 2;
        }
        // this will never be reached
        return 0;

    }

    private static String getCurrencySymbol(String currency) {
        if (currency.equals("EUR")) {
            return "€";

        } else if (currency.equals("USD")) {
            return "$";
        } else if (currency.equals("GBP")) {
            return "£";
        }
        // this will never be reached
        return "€";
    }

    /**
     * First currency is the one to convert FROM, the other one is the one to convert TO.
     *
     * @param currency1
     * @param currency2
     * @return
     */
    private static double getExchangeRate(String currency1, String currency2) {

        double exchangeRate1 = 0;
        double exchangeRate2 = 1;
        boolean firstRate = false;
        boolean secondRate = false;

        if (currency1.equals(currency2)) {
            return 1;
        }

        if (currency1.equals("USD")) {
            for (int i = 0; i < currentRates.length; i++) {
                if (currentRates[i].contains(currency2)) {
                    String[] parts = currentRates[i].split(":");
                    exchangeRate1 = Double.parseDouble(parts[1]);
                    return exchangeRate1;
                }

            }
        }

        if (currency2.equals("USD")) {
            for (int i = 0; i < currentRates.length; i++) {
                if (currentRates[i].contains(currency1)) {
                    String[] parts = currentRates[i].split(":");
                    exchangeRate1 = Double.parseDouble(parts[1]);
                    return (1 / exchangeRate1);
                }

            }
        }


        for (int i = 0; i < currentRates.length; i++) {
            if (currentRates[i].contains(currency1)) {
                String[] parts = currentRates[i].split(":");
                exchangeRate1 = Double.parseDouble(parts[1]);
                System.out.println("Rate 1: " + exchangeRate1);
                firstRate = true;
            } else if (currentRates[i].contains(currency2)) {
                String[] parts = currentRates[i].split(":");
                System.out.println("Parts2:" + parts[1]);
                exchangeRate2 = Double.parseDouble(parts[1]);
                System.out.println("Rate 2: " + exchangeRate2);
                secondRate = true;
            }

            if (firstRate && secondRate) {
                break;
            }

        }

        double trueExchangeRate = exchangeRate2 / exchangeRate1;

        return trueExchangeRate;

    }

    /**
     * Displays the time at which the exchange rates were last updated
     *
     * @param resultLabel the label in which to update the text
     */
    private static void displayUpdateMessage(JLabel resultLabel) {
        LocalTime timeOfRefresh = LocalTime.now();
        String[] parts = timeOfRefresh.toString().split(":");
        String refreshMessage = "Last update at: ";
        refreshMessage += parts[0];
        refreshMessage += ":";
        refreshMessage += parts[1];

        resultLabel.setText(refreshMessage);
    }
}
