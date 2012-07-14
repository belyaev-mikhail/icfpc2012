package Vis;

import Walker.Move;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class FieldState {

    private List<List<CellState>> cells = new LinkedList<List<CellState>>();
    private int lambdaCounter = 0;
    private int playerX = 0;
    private int playerY = 0;

    private int water = 0;
    private int flooding = 0;
    private int waterproof = 10;

    private FieldState(FieldState that) {
        lambdaCounter = that.lambdaCounter;
        playerX = that.playerX;
        playerY = that.playerY;

        water = that.water;
        flooding = that.flooding;
        waterproof = that.waterproof;

        for(List<CellState> row: that.cells) {
            cells.add(new LinkedList<CellState>(row));
        }
    }

    @Override
    public String toString() {
        return "FieldState{" +
                "cells=" + cells +
                ", lambdaCounter=" + lambdaCounter +
                ", playerX=" + playerX +
                ", playerY=" + playerY +
                ", water=" + water +
                ", flooding=" + flooding +
                ", waterproof=" + waterproof +
                '}';
    }

    public FieldState(String repr) {
        StringTokenizer tkn = new StringTokenizer(repr, "\n", false);
        String token = "";

        boolean hasWater = false;

        while( tkn.hasMoreTokens() ) {
            token = tkn.nextToken();
            if (token.startsWith("Water")) {
                hasWater = true;
                break;
            }
            List<CellState> row = new LinkedList<CellState>();
            cells.add(row);
            for (char c: token.toCharArray()) {
                CellState toPut = CellState.makeCellState(c);
                row.add(toPut);
                if(toPut.equals(CellState.LAMBDA)) lambdaCounter++;
            }
        }

        if (hasWater && tkn.hasMoreTokens()) {
            try {
                String[] waterDef = token.split("\\s");
                if (waterDef.length >= 2 && waterDef[0].equals("Water")) {
                    this.water = Integer.parseInt(waterDef[1]);
                }

                String[] floodDef = tkn.nextToken().split("\\s");
                if (floodDef.length >= 2 && floodDef[0].equals("Flooding")) {
                    this.flooding = Integer.parseInt(floodDef[1]);
                }

                String[] proofDef = tkn.nextToken().split("\\s");
                if (proofDef.length >= 2 && proofDef[0].equals("Waterproof")) {
                    this.waterproof = Integer.parseInt(proofDef[1]);
                }
            } catch(Exception e) {
                System.out.println(e);
            }
        }

        Collections.reverse(cells);

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if(peekCell(x,y).equals(CellState.ROBOT)) {
                    playerX = x;
                    playerY = y;
                }
            }
        }
    }

    public CellState getCell(int x, int y) {
        return cells.get(y).get(x);
    }

    public void setCell(int x, int y, CellState cs) {
        cells.get(y).set(x, cs);
    }

    public CellState peekCell(int x, int y) {
        if (x >= 0 && x < getWidth() && y >=0 && y < getHeight()) return getCell(x,y);
        else return null;
    }

    public int getWidth() {
        return cells.get(0).size();
    }

    public int getHeight() {
        return cells.size();
    }

    public int getLambdaCounter() {
        return lambdaCounter;
    }

    public void setLambdaCounter(int lambdaCounter) {
        this.lambdaCounter = lambdaCounter;
    }

    public int getPlayerX() {
        return playerX;
    }

    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }

    public FieldState clone() {
        return new FieldState(this);
    }
}
