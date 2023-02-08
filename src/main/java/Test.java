import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Semaphore sem = new Semaphore(0);
        Runnable task = () -> {
            try {
                sem.acquire();
                System.out.println("Нить выполнила задачу");
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(5000);

                sem.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(task).start();
        new  Thread(task).start();
        new Thread(task).start();
      //  Thread.sleep(3000);
        sem.release(2);
    }
}
