package com.self.library.entity;

import java.math.BigDecimal;
import java.util.Date;

public class BookEntity
{
    private Integer id;

    private String bookName;

    private String bookNo;

    private BigDecimal price;

    private Boolean show;

    private Integer pageNum;

    private Integer tagId;

    private Integer publishId;

    private String author;

    private Date publishDate;

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

    public String getBookName()
    {
        return bookName;
    }

    public void setBookName(String bookName)
    {
        this.bookName = bookName == null ? null : bookName.trim();
    }

    public String getBookNo()
    {
        return bookNo;
    }

    public void setBookNo(String bookNo)
    {
        this.bookNo = bookNo == null ? null : bookNo.trim();
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public Boolean getShow()
    {
        return show;
    }

    public void setShow(Boolean show)
    {
        this.show = show;
    }

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getTagId()
    {
        return tagId;
    }

    public void setTagId(Integer tagId)
    {
        this.tagId = tagId;
    }

    public Integer getPublishId()
    {
        return publishId;
    }

    public void setPublishId(Integer publishId)
    {
        this.publishId = publishId;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author == null ? null : author.trim();
    }

    public Date getPublishDate()
    {
        return publishDate;
    }

    public void setPublishDate(Date publishDate)
    {
        this.publishDate = publishDate;
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