package com.example.mytperestaurantapp.presenter;

import android.os.Handler;
import android.util.Log;

import com.example.mytperestaurantapp.tasks.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Executor implements Contract.Presenter {

    Random rand = new Random(System.currentTimeMillis());
    String[] names = {"Sarah", "John", "Renee", "Ian", "Andrew", "Marco", "Dalo", "Monique", "etc."};

    Handler handler;
    Contract.View view;

    private static final int CORE_THREADS = 4;
    private static final int MAX_THREADS = 4;
    private static long KEEP_ALIVE_TIME = 100;

    private BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(4);

    ThreadPoolExecutor tpe;

    public Executor(Contract.View view, Handler handler) {
        this.view = view;
        this.handler = handler;
    }

    @Override
    public void generateNewCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();

        int count = rand.nextInt(5);
        for(int i = 0; i < count; i++) {
            customers.add(new Customer(names[rand.nextInt(names.length)], rand.nextInt(15)));
        }

        view.receiveNewCustomers(customers);
    }

    @Override
    public void runCustomers(final List<Customer> customers) {
        if(tpe == null) {
            tpe = new ThreadPoolExecutor(CORE_THREADS, MAX_THREADS, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, taskQueue);
        }

        for(int i = 0; i < customers.size(); i++) {
            final Customer current = customers.get(i);
            final int in = i;
            tpe.execute(new Runnable() {
                @Override
                public void run() {
                    for(float j = 0; j <= current.time; j += 1) {
                        final float jn = j;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setProgress(in, jn, current.time);
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Log.d("TAG_X", "run: Something interrupted us");
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setFinished(in);
                        }
                    });

                }
            });
        }
        view.success(true);
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
