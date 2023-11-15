import java.awt.*;
import javax.swing.*;

// https://www.phind.com/search?cache=f2909367-8739-4879-a500-1924a16b63b8
public class RatioGridLayout extends GridLayout {
    private final double[] _colRatio;

    RatioGridLayout(int rows, int cols, double[] colRatio) {
        super(rows, cols);
        _colRatio = colRatio;
    }

    @Override
    public void layoutContainer(Container parent) {
        int components = parent.getComponentCount();
        if (components == 0) return;

        Insets insets = parent.getInsets();
        int totalWidth = parent.getWidth() - insets.left - insets.right;
        int totalHeight = parent.getHeight() - insets.top - insets.bottom;
        int cols = getColumns();
        int rows = components / cols;
        if (components % cols > 0) rows++;

        int[] colWidths = new int[cols];
        for (int i = 0; i < cols; i++)
            colWidths[i] = (int) (totalWidth * _colRatio[i]);

        int x = insets.left;
        int y = insets.top;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int i = r * cols + c;
                if (i < components) {
                    Component component = parent.getComponent(i);
                    component.setBounds(x, y, colWidths[c], totalHeight / rows);
                    x += colWidths[c];
                }
            }
            y += totalHeight / rows;
            x = insets.left;
        }
    }
}

