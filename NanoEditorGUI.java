import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NanoEditorGUI extends JFrame {
    private JTextArea textArea;
    private String filename;
    private boolean modified = false;

    public NanoEditorGUI() {
        setTitle("NanoEditor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modified) {
                    int option = JOptionPane.showConfirmDialog(NanoEditorGUI.this,
                            "There are unsaved changes. Exit anyway?", "Confirm Exit",
                            JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu textMenu = new JMenu("Text");
        JMenuItem changeFontItem = new JMenuItem("Change Font");
        JMenuItem changeSizeItem = new JMenuItem("Change Size");

        changeFontItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeFont();
            }
        });

        changeSizeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeSize();
            }
        });

        textMenu.add(changeFontItem);
        textMenu.add(changeSizeItem);
        menuBar.add(textMenu);

        setJMenuBar(menuBar);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filename = file.getAbsolutePath();
            loadFile();
        }
    }

    private void loadFile() {
        try {
            textArea.setText("");
            if (Files.exists(Paths.get(filename))) {
                List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
                for (String line : lines) {
                    textArea.append(line + "\n");
                }
                modified = false;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error while reading file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile() {
        if (filename == null) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                filename = file.getAbsolutePath();
            } else {
                return;
            }
        }
        try {
            List<String> lines = new ArrayList<>();
            for (String line : textArea.getText().split("\n")) {
                lines.add(line);
            }
            Files.write(Path.of(filename), lines, StandardCharsets.UTF_8);
            modified = false;
            JOptionPane.showMessageDialog(this, "File saved successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error while saving file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeFont() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String font = (String) JOptionPane.showInputDialog(this, "Choose font:", "Font",
                JOptionPane.PLAIN_MESSAGE, null, fonts, textArea.getFont().getFamily());
        if (font != null) {
            textArea.setFont(new Font(font, Font.PLAIN, textArea.getFont().getSize()));
        }
    }

    private void changeSize() {
        String sizeStr = JOptionPane.showInputDialog(this, "Enter font size:", textArea.getFont().getSize());
        if (sizeStr != null) {
            try {
                int size = Integer.parseInt(sizeStr);
                textArea.setFont(new Font(textArea.getFont().getFamily(), Font.PLAIN, size));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid size", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NanoEditorGUI editor = new NanoEditorGUI();
            editor.setVisible(true);
        });
    }
}