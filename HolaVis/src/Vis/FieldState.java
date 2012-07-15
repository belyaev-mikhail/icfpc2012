package Vis;

import Walker.Move;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.*;

public class FieldState {

    private List<List<CellState>> cells = new ArrayList<List<CellState>>();
    private int lambdaCounter = 0;
    private int playerX = 0;
    private int playerY = 0;

    private int water = -1;
    private int flooding = 0;
    private int waterproof = 10;

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public int getWaterproof() {
        return waterproof;
    }

    public void setWaterproof(int waterproof) {
        this.waterproof = waterproof;
    }

    public int getFlooding() {
        return flooding;
    }

    public void setFlooding(int flooding) {
        this.flooding = flooding;
    }

    private FieldState(FieldState that) {
        lambdaCounter = that.lambdaCounter;
        playerX = that.playerX;
        playerY = that.playerY;

        water = that.water;
        flooding = that.flooding;
        waterproof = that.waterproof;

        for(List<CellState> row: that.cells) {
            cells.add(new ArrayList<CellState>(row));
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

        List<String> strField = new LinkedList<String>();

        while( tkn.hasMoreTokens() ) {
            token = tkn.nextToken();
            if (token.startsWith("Water")) {
                hasWater = true;
                break;
            }
            strField.add(token);
        }
        int longestRow = Collections.max(strField, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        }).length();
        for(String str: strField) {
            while(str.length() < longestRow) {
                str += ' ';
            }
            List<CellState> row = new LinkedList<CellState>();
            cells.add(row);
            for (char c: str.toCharArray()) {
                CellState toPut = CellState.makeCellState(c);
                row.add(toPut);
                if(toPut.equals(CellState.LAMBDA)) lambdaCounter++;
            }
        }

        if (hasWater && tkn.hasMoreTokens()) {
            try {
                String[] waterDef = token.split("\\s");
                if (waterDef.length >= 2 && waterDef[0].equals("Water")) {
                    this.water = Integer.parseInt(waterDef[1]) - 1;
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
                System.err.println("Format error:" + e);
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
