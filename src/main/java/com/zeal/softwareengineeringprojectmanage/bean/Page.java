package com.zeal.softwareengineeringprojectmanage.bean;

import java.util.List;

public class Page<T> {
    //基本属性
    private int currentPage;//当前页数，由用户指定
    private int pageSize = 10 ;//每页显示的条数，固定的
    private int totalRecords;//总记录条数，数据库查出来的
    private int totalPage;//总页数，计算出来的
    private int startIndex;//每页开始记录的索引，计算出来的
    private int prePage;//上一页
    private int nextPage;//下一页
    private int startPage;//开始页码
    private int endPage;//结束页码

    private List<T> list;//已经分好页的结果集,该list中只有10条记录

    //点击页码要访问的url
    private String url;

    //要想使用我的分页，必须给我两个参数。一个是要看哪一页，另一个是总记录条数
    public Page(int currentPage,int totalRecords,int pageSize){
        this.currentPage = currentPage;
        this.totalRecords = totalRecords;
        this.pageSize=pageSize;

        //计算查询记录的开始索引
        startIndex = (currentPage-1)*pageSize;

        //计算总页数
        totalPage = totalRecords%pageSize==0?(totalRecords/pageSize):(totalRecords/pageSize+1);

        //设置每页显示九个页码
        startPage = currentPage - 4; //5
        endPage = currentPage + 4;  //13

        //设置前一页与后一页
        prePage = currentPage-1;
        if(prePage<1){
            prePage = 1;
        }
        nextPage = currentPage+1;
        if(nextPage>totalPage){
            nextPage = totalPage;
        }

        //设置起始页码与结束页码，看看总页数够不够9页
        if(totalPage>9){
            //超过了9页
            if(startPage < 1){
                startPage = 1;
                endPage = startPage+8;
            }
            if(endPage > totalPage){
                endPage = totalPage;
                startPage = endPage-8;
            }
        }else{
            //不够9页
            startPage = 1;
            endPage = totalPage;
        }
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
