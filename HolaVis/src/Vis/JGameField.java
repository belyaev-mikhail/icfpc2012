package Vis;

import Walker.FinishState;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class JGameField extends JPanel implements FieldControlListener {

        //Add the ubiquitous "Hello World" label.
    private FieldControl fs;
    private List<List<JLabel>> labels = new ArrayList<List<JLabel>>();

    private static Map<Character, Icon> fancies = new HashMap<Character, Icon>(8);
    private static Icon deadIcon = new ImageIcon(JGameField.class.getResource("Res/small-DeadR.png"));
    private static Icon deadWaterIcon = new ImageIcon(JGameField.class.getResource("Res/blue-small-DeadR.png"));
    static {
        fancies.put('R',  new ImageIcon(JGameField.class.getResource("Res/small-R.png")) );
        fancies.put('\\', new ImageIcon(JGameField.class.getResource("Res/small-Lam.png")) );
        fancies.put('.',  new ImageIcon(JGameField.class.getResource("Res/small-E.png")) );
        fancies.put('#',  new ImageIcon(JGameField.class.getResource("Res/small-W.png")) );
        fancies.put('*',  new ImageIcon(JGameField.class.getResource("Res/small-Rock.png")) );
        fancies.put('L',  new ImageIcon(JGameField.class.getResource("Res/small-CL.png")) );
        fancies.put('O',  new ImageIcon(JGameField.class.getResource("Res/small-OL.png")) );
        fancies.put(' ',  new ImageIcon(JGameField.class.getResource("Res/small-Empty.png")) );
        fancies.put('@',  new ImageIcon(JGameField.class.getResource("Res/small-LR.png")) );
        for(char c : "ABCDEFGHI".toCharArray()) {
            fancies.put(c, new ImageIcon(JGameField.class.getResource("Res/small-Tram.png")));
        }
        for(char c : "123456789".toCharArray()) {
            fancies.put(c, new ImageIcon(JGameField.class.getResource("Res/small-T.png")));
        }
    }

    private static Map<Character, Icon> fanciesWater = new HashMap<Character, Icon>(8);
    static {
        fanciesWater.put('R',  new ImageIcon(JGameField.class.getResource("Res/blue-small-R.png")) );
        fanciesWater.put('\\', new ImageIcon(JGameField.class.getResource("Res/blue-small-Lam.png")) );
        fanciesWater.put('.',  new ImageIcon(JGameField.class.getResource("Res/blue-small-E.png")) );
        fanciesWater.put('#',  new ImageIcon(JGameField.class.getResource("Res/blue-small-W.png")) );
        fanciesWater.put('*',  new ImageIcon(JGameField.class.getResource("Res/blue-small-Rock.png")) );
        fanciesWater.put('L',  new ImageIcon(JGameField.class.getResource("Res/blue-small-CL.png")) );
        fanciesWater.put('O',  new ImageIcon(JGameField.class.getResource("Res/blue-small-OL.png")) );
        fanciesWater.put(' ',  new ImageIcon(JGameField.class.getResource("Res/blue-small-Empty.png")) );
        fanciesWater.put('@',  new ImageIcon(JGameField.class.getResource("Res/blue-small-LR.png")) );
        for(char c : "ABCDEFGHI".toCharArray()) {
            fanciesWater.put(c, new ImageIcon(JGameField.class.getResource("Res/blue-small-Tram.png")));
        }
        for(char c : "123456789".toCharArray()) {
            fanciesWater.put(c, new ImageIcon(JGameField.class.getResource("Res/blue-small-T.png")));
        }
    }

    public JGameField(FieldControl fs) {
        setFieldControl(fs);
    }

    FieldControl getFieldControl() {
        return fs;
    }

    void setFieldControl(FieldControl fs) {
        for (List<JLabel> row: labels) {
            for (JLabel lab: row) {
                this.remove(lab);
            }
        }
        labels.clear();
        this.setLayout(new GridLayout(fs.getHeight(), fs.getWidth(), 0, 0));

        this.fs = fs;
        fs.addListener(this);

        for(int i = 0; i < fs.getHeight(); ++i) {
            List<JLabel> row = new ArrayList<JLabel>();
            labels.add(row);
            for (int j = 0; j < fs.getWidth(); j++) {
                char ch =  fs.getCell(j,fs.getHeight()-i-1).getRep();
                boolean water = fs.isCellInWater(j,fs.getHeight()-i-1);

                JLabel cell;
                if(fancies.containsKey(ch)){
                    cell = new JLabel(water? fanciesWater.get(ch) : fancies.get(ch));
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
                char ch =  fs.getCell(j,fs.getHeight()-i-1).getRep();
                boolean water = fs.isCellInWater(j,fs.getHeight()-i-1);
                boolean dead = (fs.getFinishingState() == FinishState.DIE) && ch == 'R';
                if(fancies.containsKey(ch)) {
                    if(dead && water) cell.setIcon(deadWaterIcon);
                    else if(dead && !water) cell.setIcon(deadIcon);
                    else cell.setIcon(water? fanciesWater.get(ch) : fancies.get(ch));
                    cell.setText("");
                } else {
                    cell.setIcon(null);
                    cell.setText(Character.toString(ch));
                }

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
