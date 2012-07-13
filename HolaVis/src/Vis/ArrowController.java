package Vis;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created with IntelliJ IDEA.
 * User: kopcap
 * Date: 13.07.12
 * Time: 20:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class ArrowController  {

    JPanel father;


    protected ArrowController(JPanel father) {
        this.father = father;
        father.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UP");
        father.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),"DOWN");
        father.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),"RIGHT");
        father.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),"LEFT");
        father.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),"ENTER");
        father.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"ESCAPE");

        father.getActionMap().put("UP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goUp();
            }
        });

        father.getActionMap().put("DOWN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goDown();
            }
        });

        father.getActionMap().put("LEFT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goLeft();
            }
        });

        father.getActionMap().put("RIGHT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goRight();
            }
        });

        father.getActionMap().put("ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                standAndWait();
            }
        });


        father.getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abortGame();
            }
        });
    }

    public abstract void goUp();
    public abstract void goDown();
    public abstract void goLeft();
    public abstract void goRight();
    public abstract void abortGame();
    public abstract void standAndWait();
}
