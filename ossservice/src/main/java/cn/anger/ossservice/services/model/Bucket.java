package cn.anger.ossservice.services.model;

import cn.anger.ossservice.services.Oss;

import java.util.Date;

/**
 * @author Anger
 * created on 2023/2/26
 * 桶对象
 */
public class Bucket {
    private String name;
    private String owner;
    private Date createDate;
    private Oss.Type type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Oss.Type getType() {
        return type;
    }

    public void setType(Oss.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", createDate=" + createDate +
                ", type=" + type +
                '}';
    }
}
