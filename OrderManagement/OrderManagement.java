import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class OrderManagement extends JFrame {
    private JButton _btnAdd, _btnSort, _btnChange, _btnSave;
    private OrderInfo _orderInput;
    private OrderList _orderList;
    private OrderListView _orderListView;
    private boolean _sortByDate = false;
    private boolean _editMode = false;
    private static final String _defaultOrderPath = "order.txt";

    OrderManagement() {
        _orderList = new OrderList();
        initFrame();
    }

    OrderManagement(String orderFileName) {
        _orderList = new OrderList(orderFileName);
        initFrame();
    }

    private void initFrame() {
        setTitle("Order Management");
        setSize(1280, 720);
        setLayout(new BorderLayout());

        _orderInput = new OrderInfo(this);
        _orderListView = new OrderListView(_orderList);
        JScrollPane scroll = new JScrollPane(_orderListView);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1));
        _btnAdd = new JButton("ADD");
        _btnSort = new JButton("<html><center>SORT BY<br>DATE/CUSTOMER</center></html>");
        _btnChange = new JButton("CHANGE/DONE");
        _btnSave = new JButton("SAVE");
        buttonPanel.add(_btnAdd);
        buttonPanel.add(_btnSort);
        buttonPanel.add(_btnChange);
        buttonPanel.add(_btnSave);
        add(buttonPanel, BorderLayout.EAST);

        _btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _orderInput.showFrame();
            }
        });
        _btnSort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_sortByDate) { // togle date/buyer
                    _orderList.sortByCustomer();
                    _sortByDate = false;
                } else {
                    _orderList.sortByDate();
                    _sortByDate = true;
                }
                _orderListView.updateOrders(_orderList);
            }
        });
        _btnChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (_editMode) { // toggle edit mode
                    _editMode = false;
                } else {
                    _editMode = true;
                }
                _orderListView.setEditMode(_editMode);
            }
        });
        _btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _orderList.saveOrders(_defaultOrderPath);
            }
        });

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addOrder(String order) {
        try {
            _orderList.addOrder(order);
            _orderListView.updateOrders(_orderList);
        } catch (Exception e) {
            System.out.println("ERROR: incorrect order");
        }
    }

    ////////////////////////////////////////////////////////////////////

    private class OrderListView extends JPanel {
        private SelectListener _selector = new SelectListener();
        private BorderLayout _layout = new BorderLayout();

        OrderListView(OrderList orderList) {
            setLayout(_layout);
            setPreferredSize(new Dimension(1200, 700));

            // title
            JPanel titleItemSub = new JPanel();
            titleItemSub.setLayout(new GridLayout(1, 4));
            Color c = new Color(0,0,0,0);
            titleItemSub.add(new TextLabel("ID",     c));
            titleItemSub.add(new TextLabel("Name",   c));
            titleItemSub.add(new TextLabel("Price",  c));
            titleItemSub.add(new TextLabel("Status", c));
            titleItemSub.setBorder(new LineBorder(Color.black, 1));

            JPanel titleItem = new JPanel();
            titleItem.setLayout(new BorderLayout());
            titleItem.add(new TextLabel("<html><br>Item(s)<br><br></html>"), BorderLayout.CENTER);
            titleItem.add(titleItemSub, BorderLayout.SOUTH);

            JPanel title = new JPanel();
            double[] colRatio = {0.12, 0.12, 0.12, 0.12, 0.4, 0.12};
            title.setLayout(new RatioGridLayout(1, 6, colRatio));
            title.add(new TextLabel("Order ID"));
            title.add(new TextLabel("Buyer"));
            title.add(new TextLabel("Date"));
            title.add(new TextLabel("(Total) Amount"));
            title.add(titleItem);
            title.add(new TextLabel("Ship Address"));
            title.setBorder(new LineBorder(Color.black, 3));
            add(title, BorderLayout.NORTH);

            // order data
            add(new JLabel(""), BorderLayout.CENTER);
            updateOrders(orderList);
        }

        public void updateOrders(OrderList orderList) {
            JPanel data = new JPanel();
            data.setLayout(new BoxLayout(data, BoxLayout.Y_AXIS));
            for (int i = 0; i < orderList.numOrders(); i++) 
                data.add(new OrderView(this, orderList.getOrder(i)));
            remove(_layout.getLayoutComponent(BorderLayout.CENTER));
            add(data, BorderLayout.CENTER);

            _selector.reset();
            revalidate();
        }

        public void setEditMode(boolean mode) {
            _selector.setEditMode(mode);
        }
    }

    private class OrderView extends JPanel {
        public Order _order;
        public OrderListView _orderListView;
        public ItemListView _itemListView;
        private JTextField _tfAddr;

        OrderView(OrderListView parent, Order order) {
            _order = order;
            _orderListView = parent;
            addMouseListener(_orderListView._selector);

            double[] colRatio = {0.12, 0.12, 0.12, 0.12, 0.4, 0.12};
            setLayout(new RatioGridLayout(1, 6, colRatio));
            setBackground(Color.white);

            String[] fields = order.get();
            add(new TextLabel(" " + fields[0], JLabel.LEFT));
            add(new TextLabel(fields[1]));
            add(new TextLabel(fields[2]));
            add(new TextLabel(fields[3] + " ", JLabel.RIGHT));

            _itemListView = new ItemListView(this, order);
            add(_itemListView);

            _tfAddr = new JTextField(fields[4]);
            _tfAddr.setBorder(new LineBorder(Color.black, 1));
            _tfAddr.setEditable(false);
            add(_tfAddr);
        }

        public void setSelected(boolean select) {
            if (select)
                setBackground(new Color(196, 196, 196));
            else
                setBackground(Color.white);
        }

        public void setEditMode(boolean mode) {
            _tfAddr.setEditable(mode);
            if (!mode) // change done
                _order.setAddress(_tfAddr.getText());
        }
    }

    private class ItemListView extends JPanel {
        public OrderView _orderView;

        ItemListView(OrderView parent, Order order) {
            _orderView = parent;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            for (int i = 0; ; i++) {
                String[] fields = order.getItem(i);
                if (fields == null) break;
                add(new ItemView(this, fields, i));
            }
        }
    }

    private class ItemView extends JPanel {
        public int _idxItem;
        public ItemListView _itemListView;
        public JComboBox _cbStatus;

        ItemView(ItemListView parent, String[] fields, int idx) {
            _idxItem = idx;
            _itemListView = parent;
            addMouseListener(_itemListView._orderView._orderListView._selector);
            setLayout(new GridLayout(1, 4));
            setBorder(new LineBorder(Color.black, 1));
            setBackground(Color.white);

            Color c = new Color(0,0,0,0);
            add(new TextLabel(fields[0], c));
            add(new TextLabel(fields[1], c));
            add(new TextLabel(fields[2], c));
            String[] listStatus = {"CANCELED", "PREPARING", "SHIPPED", "DELIVERED", "RETURNED"};
            _cbStatus = new JComboBox(listStatus);
            _cbStatus.setSelectedItem(fields[3]);
            _cbStatus.setEnabled(false);
            add(_cbStatus);
        }

        public void setSelected(boolean select) {
            if (select)
                setBackground(new Color(196, 196, 196));
            else
                setBackground(Color.white);
        }

        public void setEditMode(boolean mode) {
            _cbStatus.setEnabled(mode);
            if (!mode) { // change done
                Order order = _itemListView._orderView._order;
                order.setItemStatus(_idxItem, (String)_cbStatus.getSelectedItem());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////

    private class TextLabel extends JLabel {
        private int _halign = JLabel.CENTER;
        private Color _color = Color.black;
        private int _thickness = 1;

        TextLabel(String text) { draw(text); }

        TextLabel(String text, int halign) {
            _halign = halign;
            draw(text);
        }

        TextLabel(String text, Color color) {
            _color = color;
            draw(text);
        }

        TextLabel(String text, int halign, Color color) {
            _halign = halign;
            _color = color;
            draw(text);
        }

        TextLabel(String text, int halign, Color color, int thickness) {
            _halign = halign;
            _color = color;
            _thickness = thickness;
            draw(text);
        }

        private void draw(String text) {
            setText(text);
            setHorizontalAlignment(_halign);
            setBorder(new LineBorder(_color, _thickness));
        }
    }

    private class SelectListener implements MouseListener {
        public OrderView _orderView;
        public ItemView _itemView;
        public boolean _editMode = false;

        public void reset() {
            if (_itemView != null) _itemView.setSelected(false);
            if (_orderView != null) _orderView.setSelected(false);
            _orderView = null;
            _itemView = null;
        }

        public void setEditMode(boolean mode) {
            _editMode = mode;
            if (_itemView != null) _itemView.setEditMode(mode);
            if (_orderView != null) _orderView.setEditMode(mode);
        }

        public void mouseClicked(MouseEvent e) {
            if (_editMode) return;  // selet at non-edit mode

            OrderView ov;
            ItemView iv;
            if (e.getSource() instanceof ItemView) {
                iv = (ItemView)e.getSource();
                ov = iv._itemListView._orderView;
            } else { // instanceof OrderView
                ov = (OrderView)e.getSource();
                iv = (ItemView)ov._itemListView.getComponent(0);
            }

            if (ov != _orderView) {
                if (_orderView != null) _orderView.setSelected(false);
                ov.setSelected(true);
                _orderView = ov;
            }
            if (iv != _itemView) {
                if (_itemView != null) _itemView.setSelected(false);
                iv.setSelected(true);
                _itemView = iv;
            }
        }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    public static void main(String[] args) {
        OrderManagement frame = new OrderManagement(_defaultOrderPath);
    }
}

