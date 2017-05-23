package mainPackage;

/**
 * Created by jacobmenke on 5/20/17.
 */
public class test {
    public static void main(String[] args) {

        System.out.println(args[0]);

        System.err.println("__________Class:" + Thread.currentThread().getStackTrace()[1].getClassName() + "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
                "___________ this is debugging");

        for (int i = 0; i < 10; i++) {
            System.out.println("this is a test");
        }



    }
}
