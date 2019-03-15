package com.xlmall.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;

    public Category(Integer id, Integer parentId, String name, Boolean status, Integer sortOrder, Date createTime, Date updateTime) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.status = status;
        this.sortOrder = sortOrder;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Category() {
        super();
    }
}