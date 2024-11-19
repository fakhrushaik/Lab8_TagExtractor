import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TagExtractor extends JFrame {
    private JTextArea outputArea;
    private File textFile, stopWordsFile;
    private Set<String> stopWords;

    public TagExtractor() {
        setTitle("Tag/Keyword Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI Components
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JButton loadTextButton = new JButton("Load Text File");
        JButton loadStopWordsButton = new JButton("Load Stop Words File");
        JButton processButton = new JButton("Process");
        JButton saveTagsButton = new JButton("Save Tags");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadTextButton);
        buttonPanel.add(loadStopWordsButton);
        buttonPanel.add(processButton);
        buttonPanel.add(saveTagsButton);

        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button Listeners
        loadTextButton.addActionListener(e -> loadTextFile());
        loadStopWordsButton.addActionListener(e -> loadStopWordsFile());
        processButton.addActionListener(e -> processFiles());
        saveTagsButton.addActionListener(e -> saveTags());
    }

    private void loadTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            textFile = fileChooser.getSelectedFile();
            outputArea.append("Text File Loaded: " + textFile.getName() + "\n");
        }
    }

    private void loadStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            stopWordsFile = fileChooser.getSelectedFile();
            stopWords = loadStopWords(stopWordsFile);
            outputArea.append("Stop Words File Loaded: " + stopWordsFile.getName() + "\n");
        }
    }

    private Set<String> loadStopWords(File file) {
        Set<String> stopWordsSet = new TreeSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWordsSet.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading stop words file.");
        }
        return stopWordsSet;
    }

    private void processFiles() {
        if (textFile == null || stopWords == null) {
            JOptionPane.showMessageDialog(this, "Please load both files first.");
            return;
        }

        Map<String, Integer> wordFrequency = new TreeMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().replaceAll("[^a-z]", " ").split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }

            // Display tags
            outputArea.setText("");
            wordFrequency.forEach((word, freq) -> outputArea.append(word + ": " + freq + "\n"));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error processing text file.");
        }
    }

    private void saveTags() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(outputArea.getText());
                JOptionPane.showMessageDialog(this, "Tags saved to " + outputFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving tags.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagExtractor extractor = new TagExtractor();
            extractor.setVisible(true);
        });
    }
}
