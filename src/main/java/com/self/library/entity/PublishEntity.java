package com.self.library.entity;

import java.util.Date;

public class PublishEntity
{
    private Integer id;

    private String publishName;

    private String createUser;

    private Date createDate;

    private String modifyUser;

    private Date modifyDate;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getPublishName()
    {
        return publishName;
    }

    public void setPublishName(String publishName)
    {
        this.publishName = publishName == null ? null : publishName.trim();
    }

    public String getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(String createUser)
    {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public String getModifyUser()
    {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser)
    {
        this.modifyUser = modifyUser == null ? null : modifyUser.trim();
    }

    public Date getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate)
    {
        this.modifyDate = modifyDate;
    }
}