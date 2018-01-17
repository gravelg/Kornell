package kornell.core.to;

public interface Page {

    Integer getCount();
    void setCount(Integer count);

    Integer getPageNumber();
    void setPageNumber(Integer pageNumber);

    Integer getPageSize();
    void setPageSize(Integer pageSize);

    Integer getPageCount();
    void setPageCount(Integer pageCount);

    Integer getSearchCount();
    void setSearchCount(Integer searchCount);
}
