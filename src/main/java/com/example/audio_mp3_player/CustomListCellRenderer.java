package com.example.audio_mp3_player;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class CustomListCellRenderer extends DefaultListCellRenderer {
    private static final Border BORDER = BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(75, 65, 128));

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBackground(isSelected ? new Color(156, 147, 203) : new Color(75, 65, 128));
        label.setForeground(Color.WHITE);
        Border border;
        border = BorderFactory.createTitledBorder(BORDER);
        label.setBorder(border);
        label.setBorder(BorderFactory.createCompoundBorder(label.getBorder(), new EmptyBorder(5, 10, 5, 10)));

        return label;
    }
}

