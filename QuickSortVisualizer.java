// Comparing Sorting Algorithms - CS 404
// Quick Sort Algorithm
// Coded by Trinity McCann using Eclipse and OpenAI's ChatGPT 4o Model

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class QuickSortVisualizer extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;
    private static final int BAR_WIDTH = 5;
    private static final int NUM_BARS = WIDTH / BAR_WIDTH;
    private int[] array = new int[NUM_BARS];
    private int delay = 20;

    public QuickSortVisualizer() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        generateArray();
        new Thread(this::quickSortAnimation).start();
    }

    private void generateArray() {
        Random rand = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(HEIGHT);
        }
    }

    private void quickSortAnimation() {
        quickSort(0, array.length - 1);
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                swap(i, j);
                repaintWithDelay();
            }
        }
        swap(i + 1, high);
        repaintWithDelay();
        return i + 1;
    }

    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        playTone(array[i], HEIGHT, 10);  // sound for bar i
        playTone(array[j], HEIGHT, 10);  // sound for bar j
    }


    private void repaintWithDelay() {
        repaint();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < array.length; i++) {
            g.setColor(Color.getHSBColor((float) array[i] / HEIGHT, 1f, 1f));
            g.fillRect(i * BAR_WIDTH, HEIGHT - array[i], BAR_WIDTH, array[i]);
        }

        // Quick Sort information
        g.setColor(Color.WHITE);
        g.drawString("Quick Sort Definition: Divide and conquer sorting algorithm.", 10, 20);
        g.drawString("Time Complexity: Best O(n log n), Avg O(n log n), Worst O(n^2)", 10, 40);
        g.drawString("Recurrence Relation: T(n) = T(k) + T(n-k-1) + O(n)", 10, 60);
    }
    
    private void playTone(int value, int maxValue, int durationMs) {
        float sampleRate = 44100;
        int numSamples = (int)(durationMs * sampleRate / 1000);
        byte[] buffer = new byte[2 * numSamples];

        double frequency = 220 + ((double) value / maxValue) * 1000; // Range from 220Hz to 1220Hz

        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * i * frequency / sampleRate;
            short sample = (short)(Math.sin(angle) * Short.MAX_VALUE);
            buffer[2 * i] = (byte)(sample & 0xff);
            buffer[2 * i + 1] = (byte)((sample >> 8) & 0xff);
        }

        new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                    line.open(format);
                    line.start();
                    line.write(buffer, 0, buffer.length);
                    line.drain();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Quick Sort Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new QuickSortVisualizer());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
