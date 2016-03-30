package reset;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
/***
 * 小猫写的例子
 * 杭州-小猫  15:36:13
考察一个多线程的场景，一个Http Request的controller有10个线程同时并行处理，要求：
1) 主线程必须等待10个线程全部执行成功后主线程返回“Success”
2) 只要其中任意一个线程出错，不仅当前出错的线程要回滚，其他所有线程不管成功失败也要回滚，主线程等待所有线程回滚完毕后输出相应的错误字符“Fail”。

 *
 */
public class Test {

    public static class RollBack {

        private boolean rollback;

        public RollBack(boolean rollback) {
            this.rollback = rollback;
        }

        public boolean isRollback() {
            return rollback;
        }

        public void setRollback(boolean rollback) {
            this.rollback = rollback;
        }
    }

    //线程
    public static class Task implements Runnable {

        private Queue<Boolean> results;

        private Boolean lock;

        private RollBack rollBack;

        public Task(Queue<Boolean> results, Boolean lock, RollBack rollBack) {
            this.results = results;
            this.lock = lock;
            this.rollBack = rollBack;
        }

        //执行成功返回true
        private Boolean processTask() {
            //todo business
            return true;
        }

        private void submit() {

        }

        private void rollback() {

        }

        @Override
        public void run() {
            Boolean result = processTask();
            results.add(result);
            try {
                lock.wait();
            } catch (InterruptedException e) {
                rollback();
            }
            if (this.rollBack.isRollback()) {
                this.rollback();
            } else {
                this.submit();
            }
        }
    }

    public String handle() throws InterruptedException {
        BlockingQueue<Boolean> results = new LinkedBlockingDeque(10);
        Thread[] threads = new Thread[10];
        Boolean lock = false;
        RollBack rollBack = new RollBack(false);
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Task(results, lock, rollBack));
            threads[i].start();
        }

        for (int i = 0; i < 10; i++) {
            Boolean result = results.take();
            if (!result) {
                rollBack.setRollback(true);
            }
        }

        lock.notifyAll();
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }

        return "SUCCESS";
    }
}

