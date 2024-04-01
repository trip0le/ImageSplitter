import java.io.File;
import javax.swing.*;
import java.util.Observable;

public class EventSource extends Observable implements Runnable {

    @Override
    public void run() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            File[] imageFiles = selectedFolder.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") ||
                            name.toLowerCase().endsWith(".png"));
            if (imageFiles != null && imageFiles.length > 0) {
                setChanged();

                //triggers update()
                notifyObservers(imageFiles);
            }
        }
    }
}
