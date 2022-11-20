package infra

class Output {

    fun write(msg: Any?) {
        print(msg)
    }

    fun clearAndWrite(msg: Any?) {
        print(ANSI_CLEAR_LINE)
        print(msg)
    }

    fun writeln(msg: Any?) {
        println(msg)
    }

    fun clearAndWriteln(msg: Any?) {
        print(ANSI_CLEAR_LINE)
        println(msg)
    }

}