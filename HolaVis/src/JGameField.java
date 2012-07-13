import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: kopcap
 * Date: 13.07.12
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */
    public class JGameField extends JPanel {

        //Add the ubiquitous "Hello World" label.
    private FieldState fs;

    public JGameField(FieldState fs) {
        super(new GridLayout(fs.getHeight(), fs.getWidth(), 1, 1), true);
        this.fs = fs;
        update();
    }

    public void update() {
        for(int i = 0; i < fs.getHeight(); ++i) {
            for (int j = 0; j < fs.getWidth(); j++) {
                JLabel cell = new JLabel(Character.toString(fs.getCell(j,i).getRep()));
                this.add(cell);
            }
        }
    }


}
