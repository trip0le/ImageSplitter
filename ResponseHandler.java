import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Observer;
import java.util.Observable;

public class ResponseHandler extends JFrame implements Observer {

    private JPanel imagePanel;
    private BufferedImage redComponent;
    private BufferedImage greenComponent;
    private BufferedImage blueComponent;

    public ResponseHandler() {
        setTitle("Image Splitter");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            EventSource eventSource = new EventSource();
            eventSource.addObserver(this);
            Thread thread = new Thread(eventSource);
            thread.start();
        });

        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (redComponent != null && greenComponent != null && blueComponent != null) {
                    int panelWidth = getWidth() / 3;
                    int panelHeight = getHeight();
                    g.drawImage(redComponent, 0, 0, panelWidth, panelHeight, null);
                    g.drawImage(greenComponent, panelWidth, 0, panelWidth, panelHeight, null);
                    g.drawImage(blueComponent, 2 * panelWidth, 0, panelWidth, panelHeight, null);
                }
            }
        };

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(browseButton, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(imagePanel), BorderLayout.CENTER);

        setVisible(true);
    }

    public void update(Observable obj, Object arg) {
        if (arg instanceof File[]) {
            File[] imageFiles = (File[]) arg;
            try {
                int index = 0;
                while (true) {
                    BufferedImage originalImage = ImageIO.read(imageFiles[index]);
                    BufferedImage[] argbComponents = splitImage(originalImage);
                    redComponent = argbComponents[0];
                    greenComponent = argbComponents[1];
                    blueComponent = argbComponents[2];
                    imagePanel.repaint();
                    Thread.sleep(1000);
                    index++;
                    if (index >= imageFiles.length) {
                        index = 0;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage[] splitImage(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage redComponent = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage greenComponent = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage blueComponent = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = originalImage.getRGB(0, 0, width, height, null, 0, width);

        for (int i = 0; i < pixels.length; i++) {
            int rgb = pixels[i];
            int alpha = (rgb >> 24) & 0xFF;
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            redComponent.setRGB(i % width, i / width, (alpha << 24) | (red << 16) | 0x000000);
            greenComponent.setRGB(i % width, i / width, (alpha << 24) | (0 << 16) | (green << 8) | 0x000000);
            blueComponent.setRGB(i % width, i / width, (alpha << 24) | (0 << 16) | (0 << 8) | blue);
        }

        return new BufferedImage[]{redComponent, greenComponent, blueComponent};
    }
}
