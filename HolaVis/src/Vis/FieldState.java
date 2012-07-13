package Vis;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class FieldState {

    private List<List<CellState>> cells = new LinkedList<List<CellState>>();

    public FieldState(String repr) {
        StringTokenizer tkn = new StringTokenizer(repr, "\n", false);
        int x = 0, y = 0;
        String token;
        while( tkn.hasMoreTokens() ) {
            token = tkn.nextToken();
            List<CellState> row = new LinkedList<CellState>();
            cells.add(row);
            for (char c: token.toCharArray()) {
                row.add(CellState.makeCellState(c));
            }
        }
    }

    public CellState getCell(int x, int y) {
        return cells.get(y).get(x);
    }

    public int getWidth() {
        return cells.get(0).size();
    }

    public int getHeight() {
        return cells.size();
    }
}
