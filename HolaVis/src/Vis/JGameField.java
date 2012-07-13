package Vis;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JGameField extends JPanel implements FieldControlListener {

        //Add the ubiquitous "Hello World" label.
    private FieldControl fs;
    private List<List<JLabel>> labels = new LinkedList<List<JLabel>>();

    private static Map<Character, Icon> fancies = new HashMap<Character, Icon>(8);
//    static {
//        fancies.put('R', new ImageIcon(JGameField.class.getResource("R.png")) );
//    }

    public JGameField(FieldControl fs) {
        super(new GridLayout(fs.getHeight(), fs.getWidth(), 1, 1), true);
        this.fs = fs;
        fs.addListener(this);

        for(int i = 0; i < fs.getHeight(); ++i) {
            List<JLabel> row = new LinkedList<JLabel>();
            labels.add(row);
            for (int j = 0; j < fs.getWidth(); j++) {
                char ch =  fs.getCell(j,fs.getHeight()-i-1).getRep();
                JLabel cell;
                if(fancies.containsKey(ch)){
                     cell = new JLabel(fancies.get(ch));
                }
                else cell = new JLabel(Character.toString(ch));
                this.add(cell);
                row.add(cell);
            }
        }
    }

    public void update() {
        for(int i = 0; i < fs.getHeight(); ++i) {
            for (int j = 0; j < fs.getWidth(); j++) {
                JLabel cell = labels.get(i).get(j);
                cell.setText(Character.toString(fs.getCell(j,fs.getHeight()-i-1).getRep()));
            }
        }
    }


    @Override
    public void onChange() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }
}
