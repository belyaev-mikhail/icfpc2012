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

    public JGameField(FieldState fs) {
        super(new GridLayout(fs.getWidth(), fs.getHeight(), 1, 1), true);
        for(int i = 0; i < fs.getWidth(); ++i) {
            for (int j = 0; j < fs.getHeight(); j++) {
                JLabel cell = new JLabel(Character.toString(fs.getCell(i,j).getRep()));
                this.add(cell);
            }
        }
    }


}
