package com.github.otbproject.otbproject.gui;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.api.APIBot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

/**
 * Created by Justin on 09/03/2015.
 */
public class Window extends JFrame implements ActionListener {
    Window frame = this;
    JPanel pnlButtons = new JPanel();
    JPanel pnlText = new JPanel();
    JButton close = new JButton("Close Window");
    JButton exit = new JButton("Stop Bot");

    TextNote text = new TextNote("OTB does not currently have a graphical interface. \nAs such, if you close this window " +
            "without stopping the bot, you may find it difficult to stop later if you wish to do so. \nIf you are not familiar " +
            "and comfortable with using a terminal, you should probably leave this window open until you wish to stop the bot. \n\n" +
            "The PID of the bot is probably " + App.PID + ", if you are using an Oracle JVM, but it may be different, especially if you are using a " +
            "different JVM. Be careful stopping the bot using this PID.");

    public Window() {
        super("OTBProject");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            App.logger.catching(e);
        }
        setSize(400, 280);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pnlText.add(text);
        pnlButtons.add(close);
        pnlButtons.add(exit);
        pnlText.add(pnlButtons);
        getContentPane().add(BorderLayout.CENTER, pnlText);
        setVisible(true);
        close.addActionListener(this);
        exit.addActionListener(this);
        addWindowListener(this.new Closing());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(close)) {
            String confirmMsg = "WARNING: THIS DOES NOT STOP THE BOT.\nClosing this window may make it difficult\nto stop the bot.\nPress \"Cancel\" to keep the window open.";
            String title = "Confirm Close Without Stopping Bot";
            if (JOptionPane.showConfirmDialog(this, confirmMsg, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                this.setVisible(false);
            }

        } else if (event.getSource().equals(exit)) {
            String confirmMsg = "This will stop the bot from running.\nPress \"Cancel\" to continue running the bot.";
            String title = "Confirm Stop Bot";
            if (JOptionPane.showConfirmDialog(this, confirmMsg, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                if (APIBot.getBot() != null && APIBot.getBot().isConnected()) {
                    APIBot.getBot().shutdown();
                }
                System.exit(0);
            }
        }
    }

    private static class TextNote extends JTextArea {
        public TextNote(String text) {
            super(text);
            setBackground(null);
            setEditable(false);
            setBorder(null);
            setLineWrap(true);
            setWrapStyleWord(true);
            setFocusable(false);
            setOpaque(false);
            setPreferredSize(new Dimension(350, 200));
            setFont(UIManager.getFont("Label.font"));
        }
    }

    private class Closing extends WindowAdapter {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if (JOptionPane.showConfirmDialog(frame, "This will stop the bot from running", "Are you sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (APIBot.getBot() != null && APIBot.getBot().isConnected()) {
                    APIBot.getBot().shutdown();
                }
                System.exit(0);
            }
        }
    }
}
