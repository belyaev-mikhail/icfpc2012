package Walker;

import Vis.CellState;
import Vis.FieldControl;
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
    FieldControl field;

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

    public List<Point> findRoute2(FieldState field, Point from, final Point to) {
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

            for (int i = 0; i < dy.length; i++) {
                Point p = new Point(sel.getX() + dx[i], sel.getY() + dy[i], sel);
                if (!open.contains(p) && !closed.contains(p) && pointWalkable(p, field) && pointInsideBox(p, field)) {
                    open.add(p);
                }
            }
            sel = Collections.min(open, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return (int)(routeCost(o1,to) - routeCost(o2,to));
                }
            });

        }

        List<Point> route = new LinkedList<Point>();
        for(Point it = sel; it != null; it = it.getParent()) {
            route.add(it);
        }
        Collections.reverse(route);
        return route;
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

    public List<Point> findRoute(FieldState field, Point from, Point to) {
        int dx = (to.getX() - from.getX()) > 0 ? 1 : -1;
        int dy = (to.getY() - from.getY()) > 0 ? 1 : -1;

        List<Point> route = new LinkedList<Point>();
        int xx = from.getX();
        int yy = from.getY();
        route.add(new Point(xx, yy));
        while( xx != to.getX()) {
            xx += dx;
            route.add(new Point(xx, yy));
        }
        while ( yy != to.getY()) {
            yy += dy;
            route.add(new Point(xx, yy));
        }


        boolean routeValid = routeValid(route, field);

        if (!routeValid) {
            boolean leftValid = false;
            boolean leftInBox = true;

            List<Point> leftRoute = route;
            while(!leftValid && leftInBox) {
                leftRoute = getValidRoute(leftRoute, field, true);
                leftValid = routeValid(leftRoute, field);
                leftInBox = routeInsideBox(leftRoute, field);
            }

            boolean rightValid = false;
            boolean rightInBox = true;

            List<Point> rightRoute = route;
            while(!rightValid && rightInBox) {
                rightRoute = getValidRoute(rightRoute, field, false);
                rightValid = routeValid(rightRoute, field);
                rightInBox = routeInsideBox(rightRoute, field);
            }

            int leftLen = (leftValid && leftInBox) ? leftRoute.size() : -1;
            int rightLen = (rightValid && rightInBox) ? rightRoute.size() : -1;

            if (leftLen != -1 && rightLen != -1) {
                if (leftLen > rightLen) {
                    route = leftRoute;
                } else {
                    route = rightRoute;
                }
            } else if (leftLen != -1) {
                route = leftRoute;
            } else if (rightLen != -1) {
                route = rightRoute;
            } else {
                route = new LinkedList<Point>();
            }
        }
        return route;
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

    public static boolean pointWalkable(Point p, FieldState field) {
        System.out.println(p);
        CellState cell = field.peekCell(p.getX(), p.getY());
        if (cell == null) {
            return false;
        }
        switch(cell) {
            case ROCK:
            case WALL:
            case CLOSED_LIFT:
                return false;
            default:
                return true;
        }
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

    public Move buildRoute(FieldState field) {
        this.set(field);

        Point robot = new Point(field.getPlayerX(), field.getPlayerY());

        boolean hasRoute = false;
        int minLen = Integer.MAX_VALUE;
        List<Point> guessRoute = null;

        for(Point lp : lambdas) {
            List<Point> route = findRoute2(field, robot, lp);
            if (route.size() !=0 && route.size() < minLen) {
                guessRoute = route;
                minLen = route.size();
                hasRoute = true;
            }
        }

        if (!hasRoute){
            return Move.ABORT;
        } else {
            return guessRoute == null || guessRoute.size() < 2 ? Move.ABORT : getDxDy(guessRoute.get(0), guessRoute.get(1));
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
