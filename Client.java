import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    // declare components
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Client() {
        try {
            System.out.println("Sending Request to Server...");
            socket = new Socket("127.0.0.1", 7777);

            System.out.println("Connection Done.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            CreateGUI();
            handleEvents();

            startReading();
            // startWriting();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CreateGUI() {
        this.setLayout(null);

        JPanel p1 = new JPanel();
        p1.setBackground(new Color(173, 216, 230));
        p1.setBounds(0, 0, 400, 70);
        this.add(p1);

        JLabel name = new JLabel("<<<<<<<<< Client Side >>>>>>>>>>");
        name.setBounds(30, 15, 400, 30);
        name.setForeground(Color.black);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        p1.add(name);

        JLabel status = new JLabel("Status: Active");
        status.setBounds(30, 45, 150, 18);
        status.setForeground(Color.black);
        status.setFont(new Font("SAN_SERIF", Font.BOLD, 15));
        p1.add(status);

        this.setTitle("Client Messager");
        this.setSize(400, 700);
        this.getContentPane().setBackground(Color.PINK); // Set pink background color
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // component code
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        messageArea.setEditable(false);

        // frame Layout
        this.setLayout(new BorderLayout());

        // Adding components to frame
        this.add(heading, BorderLayout.NORTH);


        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);


        this.add(messageInput, BorderLayout.SOUTH);

        // Automatically scroll down when new content is added
        DefaultCaret caret = (DefaultCaret) messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.setVisible(true);
    }


    private void handleEvents() {
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                }
            }
        });
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("reader started.....");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Server has terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server has terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Server: " + msg + "\n");
                }
                System.out.println("Connection Closed");
            } catch (Exception e) {
                System.out.println("Connection Closed");
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("Client is running");
        SwingUtilities.invokeLater(() -> new Client());
    }
}
