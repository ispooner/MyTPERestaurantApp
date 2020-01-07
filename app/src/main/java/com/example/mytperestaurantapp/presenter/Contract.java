package com.example.mytperestaurantapp.presenter;

import android.os.Handler;

import com.example.mytperestaurantapp.tasks.Customer;

import java.util.List;

public interface Contract {
    public interface Presenter {
        public void generateNewCustomers();

        public void runCustomers(List<Customer> customers);

        public void setHandler(Handler handler);
    }

    public interface View {
        public void receiveNewCustomers(List<Customer> customers);

        public void setProgress(int index, float amount, float total);

        public void setFinished(int index);

        public void success(Boolean successful);
    }
}
