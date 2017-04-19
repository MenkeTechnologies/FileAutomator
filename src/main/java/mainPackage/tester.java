package mainPackage;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jacobmenke on 4/15/17.
 */
public class tester {
    public static void main(String[] args) {




        new Thread(()->{

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            Future future = executorService.submit(()->{
                try {
                    Thread.sleep(2000);
                    System.out.println("finishing");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            });

            try {
                future.get();
                System.out.println("stopping");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("at end");

            executorService.shutdown();


        }).start();

        System.out.println("ending truly");


    }
}
