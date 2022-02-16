/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab130_4_in;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleChat extends JFrame implements ISimpleChat {

    private JButton sendButton;
    private JButton exitButton;
    private JTextField inText;
    private JTextArea outText;
    private Socket socket;
    private ServerSocket serverSocket;
    private int choice;

    public SimpleChat() {
        init();
        int choice = choices();
        if (choice == 0) {
            setTitle("СЕРВЕР");
            new Thread(() -> {
                try {
                    server();
                } catch (ChatException e) {
                    System.out.println("Ошибка#3: " + e.getMessage());
                }
            }).start();
        } else {
            setTitle("КЛИЕНТ");
            new Thread(() -> {
                try {
                    client();
                } catch (ChatException e) {
                    System.out.println("Ошибка#1: " + e.getMessage());
                }
            }).start();
        }
        setVisible(true);
    }

    private void init() {
        setSize(500, 500);
        setLocation(600, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        

        sendButton = new JButton("Отправить");
        sendButton.setBackground(Color.PINK);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inText.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(SimpleChat.this, "Ошибка: пустая строка");
                    return;
                }
                try {
                    sendMessage(inText.getText());
                } catch (ChatException ex) {
                    System.out.println("Ошибка#2: " + ex.getMessage());
                }
            }
        });
        exitButton = new JButton("Выход");
        exitButton.setBackground(Color.PINK);
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });
        inText = new JTextField(20);
        
        outText = new JTextArea();
        outText.setEditable(false);
        outText.setBackground(Color.YELLOW);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        southPanel.setBackground(Color.GREEN);
        southPanel.add(inText);
        southPanel.add(sendButton);
        

        add(southPanel, BorderLayout.NORTH);
        add(outText, BorderLayout.CENTER);
        add(exitButton, BorderLayout.SOUTH);
     
    }

    private int choices() {
        Object[] choiceBut = {"сервер", "клиент"};
        choice = JOptionPane.showOptionDialog(this,
                new String[] {"Добро пожаловать!", 
                                            "Выберите:"},
                null, JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, choiceBut, null);
        return choice;
    }

    private String Adress() {
        String adress = "";
        while (true) {
            adress = JOptionPane.showInputDialog("Введите адрес сервера: ");
            if (adress == null) {
                dispose();
                System.exit(0);
            }
            if (adress.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "Ошибка: пустая строка!");
                continue;
            }
            break;
        }
        return adress;

    }

    private int Port() {
        String portString = "";
        int portInt = 0;
        while (true) {
            portString = JOptionPane.showInputDialog("Введите порт сервера: ");
            if (portString == null) {
                dispose();
                System.exit(0);
            }
            if (portString.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "Ошибка: пустая строка!");
                continue;
            }
            try {
                portInt = Integer.parseInt(portString);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка: неправильно введен номер порта!");
                continue;
            }
            break;
        }
        return portInt;
    }

    @Override
    public void client() throws ChatException {
        String adress = Adress();
        int port = Port();
        try {
            socket = new Socket(adress, port);
            System.out.println("клиент подключен");
            getMessage();
        } catch (IOException e) {
            JOptionPane.showInputDialog(this, 
                              new String[] {"Неверно введен адрес и порт!", 
                                            "Повторите адрес и порт:"}, null,
                                            JOptionPane.YES_NO_CANCEL_OPTION);
            client();
        }
    }

    @Override
    public void server() throws ChatException {
        try {
            serverSocket = new ServerSocket(ISimpleChat.SERVER_PORT);
            socket = serverSocket.accept();
            System.out.println("сервер подключен");
            getMessage();
        } catch (IOException e) {
            throw new ChatException(e.getMessage());
        }
    }

    @Override
    public void getMessage() throws ChatException {
        try (Scanner scanner = new Scanner(socket.getInputStream())) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                outText.append(socket.getInetAddress() + ": " + socket.getPort() + " --> " + line + "\n");
            }
        } catch (IOException e) {
            throw new ChatException(e.getMessage());
        }
    }

    @Override
    public void sendMessage(String message) throws ChatException {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(message);
            outText.append("Ваше сообщение: " + message + "\n");
            inText.setText(null);
        } catch (IOException e) {
            throw new ChatException(e.getMessage());
        }
    }

    @Override
    public void close() throws ChatException {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.getOutputStream().close();
                socket.getInputStream().close();
                socket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            throw new ChatException(e.getMessage());
        }
    }
}