package Walker;

import Vis.CellState;
import Vis.FieldControl;
import Vis.FieldPlayback;
import Vis.FieldState;

import java.util.*;

import static Vis.CellState.*;

/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/13/12
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Walker {
    FieldControl control;

    List<Point> lambdas;

    Point robot;

    Moves moves = new Moves(0, 0);

    Random rnd = new Random();

    public class Point{
        int x;
        int y;
        Point parent = null;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(int x, int y, Point parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
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

    public Point aStar(FieldState field, Point from, final Point to, boolean fast) {
        from.setParent(null);
        List<Point> open = new LinkedList<Point>();
        List<Point> closed = new LinkedList<Point>();
        List<Point> route = new LinkedList<Point>();

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
                if (fast) {
                    sel = p;
                    break;
                } else {
                    if(routePlaybackOk(p)) {
                        sel = p;
                        break;
                    } else {
                        open.remove(p);
                    }
                }
            }
        }

        return sel;
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

            System.out.println("a star");
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
                System.out.println("Test playback:" + p);
                if(routePlaybackOk(p)) {
                    System.out.println("Ok path:" + p);
                    sel = p;
                    break;
                } else {
                    System.out.println("Excluding death point:" + p);
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
        while(p.getParent() != null) {
            moves.add(getDxDy(p.getParent(), p));
            p = p.getParent();
        }
        Collections.reverse(moves);
        return moves;
    }

    public boolean routePlaybackOk(Point open) {
        List<Move> moves = getMovesFromPoint(open);

        FieldPlayback playback = new FieldPlayback(moves, control);
        playback.play();

        FieldControl result = playback.getFieldControl();

        return !result.playerIsDead();
    }

    public static int getParentPathSize(Point current) {
        int size = 0;
        for(Point p = current; p.getParent() != null; p = p.getParent()) {
            size ++;
        }
        return  size;
    }

    public static double routeCost(Point current, Point stop) {
        return getParentPathSize(current) + stop.diff(current);
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
                if(from == null) return false;
                else if(from.getX() < p.getX() && field.peekCell(p.getX()+1, p.getY()) == CellState.EMPTY) {
                    return true;
                } else if(from.getX() > p.getX() && field.peekCell(p.getX()-1, p.getY()) == CellState.EMPTY) {
                    return true;
                } else return false;
            case WALL:
            case CLOSED_LIFT:
                return false;
            default:
                return true;
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
                if ((c1 == CellState.EMPTY || c1 == CellState.ROBOT) && c2 == CellState.ROCK) {
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
                        && c3 == CellState.ROCK
                        && (c4 == CellState.ROCK || c4 == CellState.LAMBDA))  {
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
                        && c3 == CellState.ROCK && c4 == CellState.ROCK
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

    public void set(FieldState field) {
        this.lambdas = new LinkedList<Point>();

        for(int x = 0; x < field.getWidth(); x++) {
            for(int y = 0; y <field.getHeight(); y++) {
                switch (field.getCell(x, y)) {
                    case LAMBDA:
                    case OPEN_LIFT:
                        lambdas.add(new Point(x,y));
                        break;
                    case ROBOT:
                        robot = new Point(x,y);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public List<Move> buildRoute(FieldControl control) {
        FieldState field = control.getState();
        this.control = control;
        this.set(field);

        Point robot = new Point(field.getPlayerX(), field.getPlayerY());

        boolean hasRoute = false;
        int minLen = Integer.MAX_VALUE;
        List<Move> guessRoute = new LinkedList<Move>();

        Point destination = robot;

        //System.out.println("routes:");
        List<Point> fastLambdas = new LinkedList<Point>();
        for(Point lp : lambdas) {
            Point sel = aStar(field, robot, lp, true);
            if (getParentPathSize(sel) != 0) {
                fastLambdas.add(sel);
            }
        }

        System.out.println("Fast lambdas: " + fastLambdas);
        while(!fastLambdas.isEmpty()) {
            Collections.sort(fastLambdas, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return getParentPathSize(o1) - getParentPathSize(o2);
                }
            });

            if (!fastLambdas.isEmpty()) {
                Point top = fastLambdas.get(0);
                if (routePlaybackOk(top)) {
                    destination = top;
                    break;
                } else {
                    Point slow = aStar(field, robot, top, false);
                    fastLambdas.remove(top);
                    if (getParentPathSize(slow) != 0) {
                        fastLambdas.add(slow);
                    }
                }
            }
        }

        return getMovesFromPoint(destination);
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
