package ar.edu.itba.paw.model;

import java.util.List;

public class PagedList<T> {
    private final List<T> list;
    private final int totalElements;

    public PagedList(List<T> list, int totalElements) {
        this.list = list;
        this.totalElements = totalElements;
    }

    public List<T> getList() {
        return list;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public static <T> PagedList<T> of (List<T> list, int totalElements) {
        return new PagedList<>(list, totalElements);
    }
}
