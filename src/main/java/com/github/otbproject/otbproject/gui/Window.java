package com.github.otbproject.otbproject.gui;

import com.github.otbproject.otbproject.App;

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
            "The PID of the bot is probably "+App.PID+", if you are using an Oracle JVM, but it may be different, especially if you are using a " +
            "different JVM. Be careful stopping the bot using this PID.");

    public Window(){
        super("OTBProject");
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            App.logger.catching(e);
        }
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        if(event.getSource().equals(close)){
            if(JOptionPane.showConfirmDialog(this,"Closing this window may make it difficult to stop the bot","Are you sure?",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ){
                this.setVisible(false);
            }

        }else if(event.getSource().equals(exit)){
            if(JOptionPane.showConfirmDialog(this,"This will stop the bot from running","Are you sure?",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
                if (App.bot != null && App.bot.isConnected()) {
                    App.bot.shutdown();
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
            setPreferredSize(new Dimension(300,220));
            setFont(UIManager.getFont("Label.font"));
        }
    }
    private class Closing extends WindowAdapter {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if(JOptionPane.showConfirmDialog(frame,"This will stop the bot from running","Are you sure?",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
                if (App.bot != null && App.bot.isConnected()) {
                    App.bot.shutdown();
                }
                System.exit(0);
            }
    }
}
}
