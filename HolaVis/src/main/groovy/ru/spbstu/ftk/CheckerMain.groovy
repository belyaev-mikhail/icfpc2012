package ru.spbstu.ftk

/**
 * Created with IntelliJ IDEA.
 * User: belyaev
 * Date: 11/5/12
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
class CheckerMain {
    public static void main(String[] args) {
        def pname = args[0]
        Process p
        if(pname.endsWith(".jar")) {
            pname = "java -jar ${pname}"
        }
        p = pname.execute()
        println pname

        p.outputStream.write(new File(args[1]).bytes)
        p.outputStream.flush()

        println "map loaded"
        println p.text

        p.destroy()

    }
}
