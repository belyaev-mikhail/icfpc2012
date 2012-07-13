package Walker;

/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/13/12
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class RockMap {
    int width;
    int height;

    char[][] map;


    public RockMap(String[] s) {
        this.width = s[0].length();
        this.height =s.length;

        map = new char[this.width][this.height];
    }
}
