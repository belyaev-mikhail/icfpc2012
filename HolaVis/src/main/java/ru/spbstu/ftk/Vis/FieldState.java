package ru.spbstu.ftk.Vis;

import ru.spbstu.ftk.Walker.Move;

import java.util.*;

public class FieldState {

    private int[][] cells;
    private int lambdaCounter = 0;
    private int playerX = 0;
    private int playerY = 0;

    private int water = -1;
    private int flooding = 0;
    private int waterproof = 10;

    private int growth = 25;
    private int razors = 0;

    // trampoline settings
    private Map<Character,Character> tramp = new HashMap<Character, Character>();

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

    public void setRazors(int razors) {
        this.razors = razors;
    }

    public int getGrowth() {
        return growth;

    }

    public int getRazors() {
        return razors;
    }


    //static int fieldStateClones = 0;
    private FieldState(FieldState that) {
        //fieldStateClones++;
        //System.out.println("Cloning fieldstate #"+fieldStateClones);

        lambdaCounter = that.lambdaCounter;
        playerX = that.playerX;
        playerY = that.playerY;

        water = that.water;
        flooding = that.flooding;
        waterproof = that.waterproof;

        growth = that.growth;
        razors = that.razors;

        cells = new int[that.cells.length][that.cells[0].length];

        for (int i = 0; i < cells.length; i++) {
            System.arraycopy(that.cells[i],0,cells[i],0,cells[i].length);
        }

        tramp = new HashMap<Character, Character>(that.tramp);
    }

    @Override
    public String toString() {
        return "FieldState{" +
                "changes=" + cells +
                ", lambdaCounter=" + lambdaCounter +
                ", playerX=" + playerX +
                ", playerY=" + playerY +
                ", water=" + water +
                ", flooding=" + flooding +
                ", waterproof=" + waterproof +
                '}';
    }

    public FieldState(String repr) {
        String[] parts = repr.split("\n\n");
        StringTokenizer tkn = new StringTokenizer(parts[0], "\n", false);
        String token = "";

        List<String> strField = new LinkedList<String>();

        while( tkn.hasMoreTokens() ) {
            token = tkn.nextToken();
            strField.add(token);
        }
        int longestRow = Collections.max(strField, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        }).length();

        int curRow = 0;
        cells = new int[strField.size()][longestRow];
        for(String str: strField) {
            while(str.length() < longestRow) {
                str += ' ';
            }
            //List<CellState> row = new LinkedList<CellState>();
            //cells.add(row);
            for (int i = 0; i < str.length(); i++) {
                CellState toPut = CellState.makeCellState(str.charAt(i));
                cells[curRow][i] = Arrays.binarySearch(CellState.values, toPut);
                //row.add(toPut);
                if(toPut.equals(CellState.LAMBDA)) lambdaCounter++;
                if(toPut.equals(CellState.LAMBDAROCK)) lambdaCounter++;
            }
            curRow++;
        }

        if (parts.length > 1) {
            tkn = new StringTokenizer(parts[1], "\n", false);
            token = "";
            try {
                while (tkn.hasMoreTokens()) {
                    token = tkn.nextToken();
                    String[] keyValue = token.split("\\s");
                    if (keyValue.length >= 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];

                        if (key.equals("Water")) {
                            this.water = Integer.parseInt(value) -1;

                        } else if (key.equals("Flooding")) {
                            this.flooding = Integer.parseInt(value);

                        } else if (key.equals("Waterproof")) {
                            this.waterproof = Integer.parseInt(value);

                        } else if (key.equals("Trampoline")
                                && keyValue.length == 4
                                && keyValue[2].equals("targets")) {
                            String src = value;
                            String dst = keyValue[3];
                            tramp.put(src.charAt(0), dst.charAt(0));
                        } else if (key.equals("Growth")) {
                            this.growth = Integer.parseInt(value);
                        } else if (key.equals("Razors")) {
                            this.razors = Integer.parseInt(value);
                        }
                    }
                }
            } catch(Exception e) {
                System.err.println("Format error:" + e);
            }
        }

        // reverse cells
        for(int i = 0; i < cells.length/2; i++)
        {
            int[] temp = cells[i];
            cells[i] = cells[cells.length - i - 1];
            cells[cells.length - i - 1] = temp;
        }


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
        return CellState.values[cells[y][x]];
    }

    public void setCell(int x, int y, CellState cs) {
        cells[y][x] = Arrays.binarySearch(CellState.values, cs);
    }

    public CellState peekCell(int x, int y) {
        if (x >= 0 && x < getWidth() && y >=0 && y < getHeight()) return getCell(x,y);
        else return null;
    }

    public int getWidth() {
        return cells[0].length;
    }

    public int getHeight() {
        return cells.length;
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

    public Map<Character, Character> getTrampolines() {
        return tramp;
    }

    FieldControl.Cell findByState(CellState cs) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if(peekCell(x,y) == cs) return new FieldControl.Cell (x,y,cs);
            }
        }
        return null;
    }

    public FieldControl.Cell findTrampolineTarget(CellState tramp) {
        Character targetChar = getTrampolines().get(tramp.getRep());
        CellState targetState = CellState.makeCellState(targetChar);
        FieldControl.Cell targetCell = findByState(targetState);
        return targetCell;
    }

    public FieldState clone() {
        return new FieldState(this);
    }
}
