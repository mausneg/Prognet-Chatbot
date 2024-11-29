/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.prognet.chatbot.View;
import java.awt.*;
import javax.swing.*;

import com.prognet.chatbot.Controller.ChatbotController;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author LENOVO
 */
public class HistoryCard extends javax.swing.JPanel {
    private Color defaultBackgroundColor;
    private Color hoverBackgroundColor = new Color(211, 211, 211);
    private int historyId;
    private ChatbotController chatbotController;
    private String firstMessage;
    private boolean isClicked = false;

    /**
     * Creates new form HistoryCard
     */
    public HistoryCard(int historyId, String firstMessage, ChatbotController chatbotController) {
        initComponents();
        this.historyId = historyId;
        this.firstMessage = firstMessage;
        this.chatbotController = chatbotController;
        defaultBackgroundColor = this.getBackground();
        setText(firstMessage);
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isClicked) { // Only change color if card has not been clicked
                    setBackground(hoverBackgroundColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isClicked) { // Only change color if card has not been clicked
                    setBackground(defaultBackgroundColor);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                cardClicked(); // Handle card click event
            }
        });

    }

    public Color getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setClicked(boolean isClicked) {
        this.isClicked = isClicked;
    }

    private void cardClicked() {
        chatbotController.setClickedHistoryCard(this);
        chatbotController.setChatClicked();
    }

    public void setText(String text) {
        jLabel8.setText(text);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(512, 48));
        setMinimumSize(new java.awt.Dimension(128, 48));
        setPreferredSize(new java.awt.Dimension(128, 48));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel8.setText("Text");

        jButton1.setBackground(new java.awt.Color(239, 68, 68));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("DEL");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        chatbotController.deleteChat(historyId);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel8;
    // End of variables declaration//GEN-END:variables
}