package ru.spbstu.ftk.Walker;

import ru.spbstu.ftk.Vis.CellState;
import ru.spbstu.ftk.Vis.FieldControl;
import ru.spbstu.ftk.Vis.FieldPlayback;
import ru.spbstu.ftk.Vis.FieldState;

import java.util.*;

import static ru.spbstu.ftk.Vis.CellState.*;

public class Walker {
    FieldControl control;

    Point robot;

    public static final int TOP_LAMBDAS = 30;

    public static final double EPS = 1e-3;

    public class Point{
        int x;
        int y;
        Point parent = null;
        int weight = 0;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(int x, int y, Point parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
        public Point(int x, int y, Point parent, int weight) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.weight = weight;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public Point getParent() {
            return parent;
        }

        public void setParent(Point parent) {
            this.parent = parent;
        }

        public double diff(Point that) {
            return Math.sqrt( Math.pow((double)(this.x - that.getX()), 2) + Math.pow((double)(this.y - that.getY()), 2));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            if (y != point.y) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public Point hyperAStar(final FieldState field, Point from, Point to) {
        List<List<Integer>> heightMatrix = new ArrayList<List<Integer>>(field.getHeight());
        for (int i = 0; i < field.getHeight(); i++) {
            List<Integer> row = new ArrayList<Integer>(field.getWidth());
            for (int j = 0; j < field.getWidth(); j++) {
                row.add(0);
            }
            heightMatrix.add(row);
        }

        Point fst = aStar(field, from, to, true, heightMatrix);
        FieldPlayback playbackResult = routePlayBackResult(fst);
        if(playbackResult.isOK()) return fst;
        else {
            List<List<Move>> deadRoutes = new LinkedList<List<Move>>();

            while(true) {
//                System.out.println("Dead routes: ");
//                for(List<Move> route: deadRoutes) {
//                    for(Move m: route) {
//                        System.out.print(m.getRep());
//                    }
//                    System.out.println();
//                }
                List<Move> moves = getMovesFromPoint(fst);
//                System.out.println("New route: ");
//                for(Move m: moves) {
//                    System.out.print(m.getRep());
//                }
//                System.out.println();
                for(List<Move> deadRoute : deadRoutes) {
                    if(subList(deadRoute, 0, playbackResult.getSteps()).equals(subList(moves, 0, playbackResult.getSteps()))){
//                       System.out.println("Found equals! PoD = " + playbackResult.getSteps());
//                        for(Move mv: deadRoute) {
//                            System.out.print(mv.getRep());
//                        }
//                        System.out.println();
//                        for(Move mv: moves) {
//                            System.out.print(mv.getRep());
//                        }
//                        System.out.println();
                       return from;
                   }
                }
//              if(deadRoutes.contains(moves)) return from;

                deadRoutes.add(moves);

                Point p = fst;
                while(p.getParent() != null) {
                    p = p.getParent();
                    Integer height = heightMatrix.get(p.y).get(p.x);
                    heightMatrix.get(p.y).set(p.x, height+25);
                }

                fst = aStar(field, from, to, true, heightMatrix);
                moves = getMovesFromPoint(fst);
                for(List<Move> deadRoute : deadRoutes) {
                    if(subList(deadRoute, 0, playbackResult.getSteps()).equals(subList(moves, 0, playbackResult.getSteps()))){
//                        System.out.println("Found equals! PoD = " + playbackResult.getSteps());
//                        for(Move mv: deadRoute) {
//                            System.out.print(mv.getRep());
//                        }
//                        System.out.println();
//                        for(Move mv: moves) {
//                            System.out.print(mv.getRep());
//                        }
//                        System.out.println();
                        return from;
                    }
                }

                playbackResult = routePlayBackResult(moves);
                if(playbackResult.isOK()) return fst;

            }


        }
    }

    static <T> List<T> subList(List<T> arg, int from, int to) {
        from = Math.min(from,0);
        to = Math.min(to, arg.size()-1);
        return arg.subList(from,to);
    }

    public Point aStar(final FieldState field, Point from, final Point to, final boolean fast, final List<List<Integer>> heightMap) {

        from.setParent(null);
        List<Point> open = new LinkedList<Point>();
        List<Point> closed = new LinkedList<Point>();
        List<Point> route = new LinkedList<Point>();

        Point sel;
        sel = from;

        open.add(sel);
        for(;;){
            if(sel.equals(to)) {
                if(open.isEmpty()) {
                    sel = from;
                }
                break;
            }

            closed.add(sel);
            open.remove(sel);

            int[] dy = {0,1,0,-1};
            int[] dx = {1,0,-1,0};

            CellState cellState = field.peekCell(sel.x,sel.y);
            Point pivot = sel;
            if(cellState.isTrampoline()) {
                FieldControl.Cell cell = field.findTrampolineTarget(cellState);
                pivot = new Point(cell.getX(), cell.getY(), sel.getParent());
            }
            //System.out.println("a star");
            for (int i = 0; i < dy.length; i++) {
                Point p = new Point(pivot.getX() + dx[i], pivot.getY() + dy[i], sel);
                if (!open.contains(p) && !closed.contains(p) && pointWalkableFrom(sel, p, field) && pointInsideBox(p, field)) {
                    if (p.getParent().equals(from)) {
                        if (!pointDangerous(p, field)) {
                            open.add(p);
                        }
                    } else {
                        open.add(p);
                    }
                }
            }

            if (open.isEmpty()) {
                sel = from;
                break;
            }
            Collections.sort(open,  new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {


                    double rc1 = routeCost(o1, to, field);
                    double rc2 = routeCost(o2, to, field);

                    for(Point pp = o1; pp != null; pp = pp.getParent()) {
                        if(heightMap != null) {
                            rc1 += heightMap.get(pp.getY()).get(pp.getX());
                        }
                        rc1 += pp.weight;
                        if(field.peekCell(pp.x,pp.y).isRock()) {
                            //System.out.println(pp);
                            rc1 += 16;
                        }
                    }
                    for(Point pp = o2; pp != null; pp = pp.getParent()) {
                        if(heightMap != null) {
                            rc2 += heightMap.get(pp.getY()).get(pp.getX());
                        }
                        rc2 += pp.weight;
                        if(field.peekCell(pp.x,pp.y).isRock()){
                            //System.out.println(pp);
                            rc2 += 16;
                        }
                    }

                   // System.out.println("rc1:"+rc1);
                   // System.out.println("rc2:"+rc2);


                    double diff = rc1 - rc2;

                    int cmp = Math.abs(diff) < EPS ? 0 : (int) Math.signum(diff);
                    return cmp;

//                    if (cmp == 0) {
//                        boolean direct1 = o1.equals(to);
//                        boolean direct2 = o2.equals(to);
//
//                        if (direct1) {
//                            return -1;
//                        } else if (direct2) {
//                            return 1;
//                        } else {
//                            return 0;
//                        }
//                    } else {
//                        return cmp;
//                    }
                }
            });

            while(!open.isEmpty()){
                Point p = open.get(0);
                if (fast) {
                    sel = p;
                    break;
                } else {
                    if(routePlaybackOk(p)) {
                        sel = p;
                        break;
                    } else {
                        open.remove(p);
//                        Point parentP = p.getParent();
//                        Point retardedP = new Point(parentP.getX(), parentP.getY(), parentP);
//
//                        boolean hasOtherPossibleRoutes = false;
//                        for (int i = 0; i < dy.length; i++) {
//                            Point reopen = new Point(p.getX() + dx[i], p.getY() + dy[i]);
//                            if (!reopen.equals(parentP) && closed.contains(reopen)) {
//                                System.out.println(reopen);
//                                hasOtherPossibleRoutes = true;
//                                Point actualReopen = closed.get(closed.indexOf(reopen));
//                                closed.remove(actualReopen);
//                                open.add(actualReopen);
//                            }
//                        }
//                        if (hasOtherPossibleRoutes) {
//                            closed.remove(parentP);
//                            open.add(retardedP);
//                            System.out.println("Add retarded point " + retardedP + " with path length " + getParentPathSize(retardedP, field));
//                        }
                        if (open.isEmpty()) {
                            sel = from;
                        }
                    }
                }
            }
        }

        return sel;
    }
    public Point aStar(final FieldState field, Point from, final Point to, final boolean fast) {
        return aStar(field, from, to, fast, null);

    }

    public List<Move> findRoute2(FieldState field, Point from, final Point to) {
        from.setParent(null);
        List<Point> open = new LinkedList<Point>();
        List<Point> closed = new LinkedList<Point>();

        Point sel;
        sel = from;

        open.add(sel);
        for(;;){
            if(sel.equals(to) || open.isEmpty()) {
                break;
            }

            closed.add(sel);
            open.remove(sel);

            int[] dy = {0,1,0,-1};
            int[] dx = {1,0,-1,0};

            //System.out.println("a star");
            for (int i = 0; i < dy.length; i++) {
                Point p = new Point(sel.getX() + dx[i], sel.getY() + dy[i], sel);
                if (!open.contains(p) && !closed.contains(p) && pointWalkableFrom(sel, p, field) && pointInsideBox(p, field)) {
                    if (p.getParent().equals(from)) {
                        if (!pointDangerous(p, field)) {
                            open.add(p);
                        }
                    } else {
                        open.add(p);
                    }
                }
            }

            if (open.isEmpty()) {
                sel = from;
                break;
            }
            Collections.sort(open,  new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return (int)(routeCost(o1,to) - routeCost(o2,to));
                }
            });

            while(!open.isEmpty()){
                Point p = open.get(0);
                //System.out.println("Test playback:" + p);
                if(routePlaybackOk(p)) {
                    //System.out.println("Ok path:" + p);
                    sel = p;
                    break;
                } else {
                    //System.out.println("Excluding death point:" + p);
                    open.remove(p);
                }
            }
        }

        List<Move> moves = new LinkedList<Move>();
        Point p = sel;
        while(p.getParent() != null) {
            moves.add(getDxDy(p.getParent(), p));
            p = p.getParent();
        }
        Collections.reverse(moves);

        return moves;
    }

    public  List<Move>  getMovesFromPoint(Point pp) {
        List<Move> moves = new LinkedList<Move>();
        Point p = pp;
        int razors = control.getState().getRazors();
        while(p.getParent() != null) {
            if (control.peekCell(p.getX(), p.getY()) == BEARD && razors > 0 ) {
                moves.add(Move.SHAVE);
                razors--;
            }
            moves.add(getDxDy(p.getParent(), p));
            p = p.getParent();
        }
        Collections.reverse(moves);
        return moves;
    }

    public boolean routePlaybackOk(Point open) {
        List<Move> moves = getMovesFromPoint(open);

        return routePlaybackOk(moves);
    }

    public boolean routePlaybackOk(List<Move> moves) {
        FieldPlayback result = routePlayBackResult(moves);

        return !result.getFieldControl().playerIsDead();
    }

    public FieldPlayback routePlayBackResult(Point open) {
        List<Move> moves = getMovesFromPoint(open);
        return routePlayBackResult(moves);
    }

    public FieldPlayback routePlayBackResult(List<Move> moves) {
        FieldPlayback playback = new FieldPlayback(moves, control);
        playback.play();

        return playback;
    }

    public static int getParentPathSize(Point current) {
        return getParentPathSize(current, null);
    }
    public static int getParentPathSize(Point current, FieldState fieldState) {
        int size = 0;
        for(Point p = current; p.getParent() != null; p = p.getParent()) {
            if(fieldState != null && fieldState.peekCell(p.x,p.y).isTrampoline()) size += 25;
            size ++;
            if (fieldState!= null && fieldState.peekCell(p.x,p.y) == BEARD ) {
                size++;
            }
        }
        return  size;
    }

    public static double routeCost(Point current, Point stop) {
        return routeCost(current, stop, null);
    }
    public static double routeCost(Point current, Point stop, FieldState field) {
        return getParentPathSize(current, field) + stop.diff(current);
    }

    public static boolean routeInsideBox(List<Point> route, FieldState field) {
        for(Point p : route) {
            if(!pointWalkable(p, field)) {
                return false;
            }
        }
        return true;
    }

    public static boolean routeValid(List<Point> route, FieldState field) {
        for(Point p : route) {
            if(!pointWalkable(p, field)) {
                return false;
            }
        }
        return true;
    }

    public static boolean pointWalkableFrom(Point from, Point p, FieldState field) {
        CellState cell = field.peekCell(p.getX(), p.getY());
        if (cell == null) {
            return false;
        }
        switch(cell) {
            case ROCK:
            case LAMBDAROCK:
                if(from == null) return false;
                else if(from.getX() < p.getX() && field.peekCell(p.getX()+1, p.getY()) == CellState.EMPTY) {
                    return true;
                } else if(from.getX() > p.getX() && field.peekCell(p.getX()-1, p.getY()) == CellState.EMPTY) {
                    return true;
                } else return false;
            case WALL:
            case CLOSED_LIFT:
                return false;
            case BEARD:
                return field.getRazors() > 0;
            default:
                if(cell.isTarget()) return false;
                else return true;
        }
    }

    public static boolean pointWalkable(Point p, FieldState field) {
        return pointWalkableFrom(null, p, field);
    }
    public boolean pointDangerous(Point p, FieldState field) {
        // *
        //
        // rR     r new loc, R old loc
        Point pp1 = new Point(p.getX(), p.getY() + 1);
        Point pp2 = new Point(p.getX(), p.getY() + 2);
        if (pointInsideBox(pp1, field) && pointInsideBox(pp2, field)) {
            CellState c1 = getFieldCellState(pp1, field);
            CellState c2 = getFieldCellState(pp2, field);
            if (c1 != null && c2 != null) {
                if ((c1 == CellState.EMPTY || c1 == CellState.ROBOT) && c2.isRock()) {
                    return true;
                }
            }
        } else {
            return false;
        }
        // *                            42
        // *                            31
        //  rR     r new loc, R old loc  rR
        Point pp3 = new Point(p.getX() - 1, p.getY() + 1);
        Point pp4 = new Point(p.getX() - 1, p.getY() + 2);
        if (pointInsideBox(pp3, field) && pointInsideBox(pp4, field)) {
            CellState c1 = getFieldCellState(pp1, field);
            CellState c2 = getFieldCellState(pp2, field);
            CellState c3 = getFieldCellState(pp3, field);
            CellState c4 = getFieldCellState(pp4, field);
            if (c1 != null && c2 != null && c3 != null && c4 != null) {
                if ((c1 == CellState.EMPTY || c1 == CellState.ROBOT) && c2 == CellState.EMPTY
                        && c3.isRock()
                        && (c4.isRock() || c4 == CellState.LAMBDA))  {
                    return true;
                }
            }
        }
        //   *                               246
        //   *                               135
        // Rr      r new loc, R old loc     Rr
        pp1 = new Point(p.getX(), p.getY() + 1);
        pp2 = new Point(p.getX(), p.getY() + 2);
        pp3 = new Point(p.getX() + 1, p.getY() + 1);
        pp4 = new Point(p.getX() + 1, p.getY() + 2);
        Point pp5 = new Point(p.getX() + 2, p.getY() + 1);
        Point pp6 = new Point(p.getX() + 2, p.getY() + 2);

        if (pointInsideBox(pp3, field) && pointInsideBox(pp4, field)
                && pointInsideBox(pp5, field) && pointInsideBox(pp6, field)) {
            CellState c1 = getFieldCellState(pp1, field);
            CellState c2 = getFieldCellState(pp2, field);
            CellState c3 = getFieldCellState(pp3, field);
            CellState c4 = getFieldCellState(pp4, field);
            CellState c5 = getFieldCellState(pp5, field);
            CellState c6 = getFieldCellState(pp6, field);
            if (c1 != null && c2 != null && c3 != null && c4 != null && c5 != null && c6 != null) {
                if ((c1 == CellState.EMPTY || c1 == CellState.ROBOT) && c2 == CellState.EMPTY
                        && c3.isRock() && c4.isRock()
                        && c5 != CellState.EMPTY && c6 != CellState.EMPTY)  {
                    return true;
                }
            }
        }
        return false;
    }



    public static CellState getFieldCellState(Point p, FieldState field) {
        return field.peekCell(p.getX(), p.getY());
    }

    public static boolean pointInsideBox(Point p, FieldState field) {
        if (p.getX() < 0 || p.getX() >= field.getWidth()) {
            return false;
        } else if (p.getY() < 0 || p.getY() >= field.getHeight()) {
            return false;
        }
        return true;
    }

    public List<Point> getValidRoute(List<Point> route, FieldState field, boolean left) {
        List<Point> route2 = new LinkedList<Point>();
        List<Point> routeReduced = new LinkedList<Point>(route);

        Point last = routeReduced.get(0);
        for(Point p : routeReduced) {
            if(!pointInsideBox(p, field)) {
                break;
            }
            if (pointWalkable(p, field)) {
                route2.add(p);
                last = p;
            } else {
                int dxx = p.getX() - last.getX();
                int dyy = p.getY() - last.getY();

                if (left) {
                    dxx = -dxx;
                    dyy = -dyy;
                }

                Point pp = new Point(last.getX() + dyy, last.getY() + dxx);
                route2.add(pp);

                last = pp;
            }
        }

        return route2;
    }

    public List<Point> getLambdas(FieldState field) {
        List<Point> lambdas = new LinkedList<Point>();

        for(int x = 0; x < field.getWidth(); x++) {
            for(int y = 0; y <field.getHeight(); y++) {
                switch (field.getCell(x, y)) {
                    case LAMBDA:
                    case OPEN_LIFT:
                    case RAZOR:
                        lambdas.add(new Point(x,y));
                        break;
                    case ROBOT:
                        robot = new Point(x,y);
                        break;
                    case LAMBDAROCK:
                        Point p = getPointUnderRock(new Point(x,y), field);
                        if (p != null) {
                            lambdas.add(p);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return lambdas;
    }

    public Point getPointUnderRock(Point p, FieldState field) {
        Point pp = p;
        while(field.peekCell(pp.getX(), pp.getY()).isRock()) {
            pp = new Point(pp.getX(), pp.getY() -1);
        }
        if (pointWalkable(pp, field)) {
            return pp;
        } else {
            return null;
        }
    }

    public List<Point> getTopLambdas(List<Point> ls, Point robot) {
        List<Point> top = new LinkedList<Point>();
        List<Point> lls = new LinkedList<Point>(ls);

        final Point r = robot;

        while (top.size() < TOP_LAMBDAS && !lls.isEmpty()) {
            Point min = Collections.min(lls, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return (int) (routeCost(r, o1) - routeCost(r, o2));
                }
            });
            top.add(min);
            lls.remove(min);
        }

        return top;
    }

    public List<Move> buildRoute(FieldControl control) {
        final FieldState field = control.getState();
        this.control = control;
        List<Point> lambdas = getLambdas(field);

        Point robot = new Point(field.getPlayerX(), field.getPlayerY());

        boolean hasRoute = false;
        int minLen = Integer.MAX_VALUE;
        List<Move> guessRoute = new LinkedList<Move>();

        Point destination = robot;
        //lambdas = getTopLambdas(lambdas, robot);

        //System.out.println("routes:");
        List<Point> fastLambdas = new LinkedList<Point>();
        for(Point lp : lambdas) {
            Point sel = aStar(field, robot, lp, true);
            if (getParentPathSize(sel) != 0) {
                fastLambdas.add(sel);
            }
        }

        //System.out.println("Fast lambdas: " + fastLambdas);
        while(!fastLambdas.isEmpty()) {
            Collections.sort(fastLambdas, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return getParentPathSize(o1, field) - getParentPathSize(o2, field);
                }
            });

            if (!fastLambdas.isEmpty()) {
                Point top = fastLambdas.get(0);
                if (routePlaybackOk(top)) {
                    destination = top;
                    break;
                } else {
                    //System.err.println("Taking slow route :(");
                    Point slow = hyperAStar(field, robot, top);
                    fastLambdas.remove(top);
                    if (getParentPathSize(slow) != 0) {
                        fastLambdas.add(slow);
                    }
                }
            }
        }

        List<Move> moves = getMovesFromPoint(destination);
        if (moves.isEmpty()) {
            for(Move move : Move.values()) {
                List<Move> mm = new LinkedList<Move>();
                mm.add(move);
                FieldPlayback result = routePlayBackResult(mm);
//                System.out.println("Move: " + move);
//                System.out.println("Player dead: " + result.getFieldControl().playerIsDead());
//                System.out.println("Change: " + result.isSituationChanging());
                if (!result.getFieldControl().playerIsDead() && result.isSituationChanging()) {
                    return mm;
                }
            }
            return moves;
        }  else {
            return moves;
        }
    }

    Move getDxDy(Point start, Point next) {
        int dx = next.getX() - start.getX();
        int dy = next.getY() - start.getY() ;

        if (dx > 0) {
            return Move.RIGHT;
        }
        if (dy > 0) {
            return Move.UP;
        }
        if (dx < 0) {
            return Move.LEFT;
        }
        if (dy < 0 ) {
            return Move.DOWN;
        }
        return Move.WAIT;
    }
}
