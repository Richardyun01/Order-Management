import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class OrderInfo extends JFrame {
    private JButton _btnInput;
    private TitleTextField _tfName, _tfAddress, _tfItemId, _tfItemName, _tfItemPrice;

    OrderInfo(OrderManagement orderManager) {
        setTitle("Input Order Information");
        setSize(800, 300);
        double[] colRatio = {0.3, 0.6, 0.1};
        setLayout(new RatioGridLayout(1, 3, colRatio));

        _tfName = new TitleTextField("Byuer Name");
        _tfAddress = new TitleTextField("Shipping Address");
        _tfItemId = new TitleTextField("Item ID");
        _tfItemName = new TitleTextField("Item Name");
        _tfItemPrice = new TitleTextField("Item Price");

        JPanel inputPanel1 = new JPanel();
        inputPanel1.setLayout(new GridLayout(2, 1));
        inputPanel1.add(_tfName);
        inputPanel1.add(_tfAddress);
        add(inputPanel1);

        JPanel inputPanel2 = new JPanel();
        inputPanel2.setLayout(new GridLayout(1, 3));
        inputPanel2.add(_tfItemId);
        inputPanel2.add(_tfItemName);
        inputPanel2.add(_tfItemPrice);
        add(inputPanel2);

        _btnInput = new JButton("INPUT");
        _btnInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                orderManager.addOrder(get());
            }
        });
        add(_btnInput);

        pack();
        setVisible(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showFrame() {
        _tfName.setText("");
        _tfAddress.setText("");
        _tfItemId.setText("");
        _tfItemName.setText("");
        _tfItemPrice.setText("");
        setVisible(true);
    }

    public String get() {
        return ":: " +                         // ID
               _tfName.getText() + " :: " +    // Buyer
               ":: " +                         // Time
               ":: " +                         // Amount
               _tfItemId.getText() + ";" +
               _tfItemName.getText() + ";" +
               _tfItemPrice.getText() + ";" +
               "PREPARING :: " +               // Item
               _tfAddress.getText();           // Address
    }
    
    private class TitleTextField extends JPanel {
        private JLabel _title = new JLabel();
        private JTextField _text = new JTextField();

        TitleTextField(String title) {
            draw(title);
        }

        TitleTextField(String title, String text) {
            _text.setText(text);
            draw(title);
        }

        private void draw(String title) {
            setLayout(new BorderLayout());
            _title.setText(title);
            _title.setFont(new Font("SansSerif ", Font.BOLD, 14));
            _title.setBorder(new EmptyBorder(10, 5, 10, 5));
            add(_title, BorderLayout.NORTH);
            add(_text, BorderLayout.CENTER);
        }

        public String getText() {
            return _text.getText();
        }

        public void setText(String text) {
            _text.setText(text);
        }
    }
}

