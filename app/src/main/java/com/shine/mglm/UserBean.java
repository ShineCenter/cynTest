package com.shine.mglm;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserBean implements Serializable {
    @Id(autoincrement = true)
    public Long userId;
    public String name;
    @NotNull
    public String phone;
    public String times;
    public String rebate;//返利次数
    public String last_time;//上次到店时间
    public String current_amount;//当前累计消费
    public String total_amount; //总计消费
    public String arrivalTimes;//到店时间集合
    private static final long serialVersionUID = 1L;
    @Generated(hash = 1541478887)
    public UserBean(Long userId, String name, @NotNull String phone, String times,
            String rebate, String last_time, String current_amount,
            String total_amount, String arrivalTimes) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.times = times;
        this.rebate = rebate;
        this.last_time = last_time;
        this.current_amount = current_amount;
        this.total_amount = total_amount;
        this.arrivalTimes = arrivalTimes;
    }
    @Generated(hash = 1203313951)
    public UserBean() {
    }
    public Long getUserId() {
        return this.userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getTimes() {
        return this.times;
    }
    public void setTimes(String times) {
        this.times = times;
    }
    public String getRebate() {
        return this.rebate;
    }
    public void setRebate(String rebate) {
        this.rebate = rebate;
    }
    public String getLast_time() {
        return this.last_time;
    }
    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }
    public String getCurrent_amount() {
        return this.current_amount;
    }
    public void setCurrent_amount(String current_amount) {
        this.current_amount = current_amount;
    }
    public String getTotal_amount() {
        return this.total_amount;
    }
    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }
    public String getArrivalTimes() {
        return this.arrivalTimes;
    }
    public void setArrivalTimes(String arrivalTimes) {
        this.arrivalTimes = arrivalTimes;
    }
}
