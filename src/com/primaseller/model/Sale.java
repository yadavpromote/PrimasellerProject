package com.primaseller.model;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author rkyadav
 */
public class Sale {

    private Date date;
    private String email;
    private String paymentMethod;
    private Map<Book, Integer> items;

    public Sale(Date date, String email, String paymentMethod) {
        this.date = date;
        this.email = email;
        this.paymentMethod = paymentMethod;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Map<Book, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Book, Integer> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Sale{" + "date=" + date + ", email=" + email + ", paymentMethod=" + paymentMethod + ", items=" + items + '}';
    }
}
