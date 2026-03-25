package mainPackage

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        println(args[0])
        System.err.println("debugging")
        for (i in 0 until 10) {
            println("this is a test")
        }
    }
}
