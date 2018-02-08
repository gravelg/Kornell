package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ProgressEvent extends GwtEvent<ProgressEventHandler> {

    public static final Type<ProgressEventHandler> TYPE = new Type<ProgressEventHandler>();

    private Integer totalPages;
    private Integer currentPage;
    private Integer pagesVisitedCount;
    private String enrollmentUUID;

    @Override
    public Type<ProgressEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ProgressEventHandler handler) {
        handler.onProgress(this);
    }

    public boolean hasNext() {
        return currentPage < totalPages;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getProgressPercent() {
        return (pagesVisitedCount * 100) / totalPages;
    }

    public void setPagesVisitedCount(int pagesVisitedCount) {
        this.pagesVisitedCount = pagesVisitedCount;
    }

    public Integer getPagesVisitedCount() {
        return this.pagesVisitedCount;
    }

    public String getEnrollmentUUID() {
        return enrollmentUUID;
    }

    public void setEnrollmentUUID(String enrollmentUUID) {
        this.enrollmentUUID = enrollmentUUID;
    }
}