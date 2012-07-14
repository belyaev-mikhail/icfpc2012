package Vis;

import Walker.Move;

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

    public FieldState(String repr) {
        StringTokenizer tkn = new StringTokenizer(repr, "\n", false);
        String token;
        while( tkn.hasMoreTokens() ) {
            token = tkn.nextToken();
            List<CellState> row = new LinkedList<CellState>();
            cells.add(row);
            for (char c: token.toCharArray()) {
                CellState toPut = CellState.makeCellState(c);
                row.add(toPut);
                if(toPut.equals(CellState.LAMBDA)) lambdaCounter++;
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
