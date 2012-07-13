package Walker;

import Vis.CellState;
import Vis.FieldState;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static Vis.CellState.*;

/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/13/12
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Walker {
    FieldState field;

    List<Point> lambdas;

    Point robot;

    Moves moves = new Moves(0, 0);

    Random rnd = new Random();

    public class Point{
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
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
    }

    public List<Point> findRoute(FieldState field, Point from, Point to) {
        int dx = (to.getX() - from.getX()) > 0 ? 1 : -1;
        int dy = (to.getY() - from.getY()) > 0 ? 1 : -1;

        List<Point> route = new LinkedList<Point>();
        for(int xx = from.getX(); xx != to.getX(); xx += dx) {
            for(int yy = from.getY(); yy != to.getY(); yy += dy) {
                route.add(new Point(xx, yy));
            }
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
        switch(field.getCell(p.getX(), p.getY())) {
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

        Point last = route.get(0);
        for(Point p : route) {
            if (pointWalkable(p, field)) {
                route2.add(p);
            } else {
                int dxx = p.getX() - last.getX();
                int dyy = p.getY() - last.getY();

                if (left) {
                    dxx = -dxx;
                    dyy = -dyy;
                }

                Point pp = new Point(p.getX() + dyy, p.getY() + dxx);
                route.add(pp);
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
            List<Point> route = findRoute(field, robot, lp);
            if (route.size() !=0 && route.size() < minLen) {
                guessRoute = route;
                minLen = route.size();
                hasRoute = true;
            }
        }

        if (!hasRoute){
            return Move.ABORT;
        } else {
            return guessRoute == null ? Move.ABORT : getDxDy(guessRoute.get(0), guessRoute.get(1));
        }
    }

    Move getDxDy(Point start, Point next) {
        int dx = start.getX() - next.getX();
        int dy = start.getY() - next.getY();

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
