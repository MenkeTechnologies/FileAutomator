package mainPackage;

public class tester{
    public static void main(String[] args) {
System.err.println("___________" + Thread.currentThread().getStackTrace()[1].getClassName()+ "____Line:" + Thread.currentThread().getStackTrace()[1].getLineNumber() +
"___ dogs");
    }
}