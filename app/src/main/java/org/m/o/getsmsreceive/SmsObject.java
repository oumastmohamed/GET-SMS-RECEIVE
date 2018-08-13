package org.m.o.getsmsreceive;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by poste05 on 23/03/2018.
 */

public class SmsObject implements Serializable{
    private String phone;
    private String message;
    private Date date;

    public SmsObject() {
    }

    public SmsObject(String phone, String message, Date date) {
        this.phone = phone;
        this.message = message;
        this.date = date;
    }

    public SmsObject(String phone, String message) {
        this.phone = phone;
        this.message = message;
        this.date = new Date();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
