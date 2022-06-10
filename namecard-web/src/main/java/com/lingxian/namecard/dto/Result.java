package com.lingxian.namecard.dto;

/**
 * @Description
 * @Author lingxian36158
 * @Date 2022-05-29 16:51
 * @Since 1.0
 */

public class Result<T> {


    private Integer code;

    private String msg;

    private T data;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    /**
     * 请求成功  状态码 1
     *
     * @param msg  返回信息
     * @param data 返回对象
     * @param <T>  类型
     * @return Result
     */
    public static <T> Result Success(String msg, T data) {
        return new Result(1, msg, data);
    }

    /**
     * 请求成功  状态码 1
     *
     * @param data 返回对象
     * @param <T>  类型
     * @return Result
     */
    public static <T> Result Success(T data) {
        return new Result(1, "成功", data);
    }
    /**
     * 请求成功  状态码 1
     *
     * @param <T>  类型
     * @return Result
     */
    public static <T> Result Success() {
        return new Result(1, "成功",null);
    }

    /**
     * 请求失败   状态码 0
     *
     * @param msg 返回信息
     * @param <T> 类型
     * @return Result
     */
    public static <T> Result Failed(String msg) {
        return new Result(0, msg);
    }

    /**
     * 请求失败  状态 0
     *
     * @param msg  返回信息
     * @param data 返回数据
     * @param <T>  类型
     * @return Result
     */
    public static <T> Result Failed(String msg, T data) {
        return new Result<>(0, msg, data);
    }


    /**
     * 用户未登录
     *
     * @param <T> 类型
     * @return Result
     */
    public static <T> Result getNoLogin() {
        return new Result<>(2, "用户未登录，请重新登录");
    }


    /**
     * 用户没有操作权限
     *
     * @param <T> 类型
     * @return Result
     */
    public static <T> Result getNoAuthorization() {
        return new Result<>(3, "用户没有操作权限，请重新登录");
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

