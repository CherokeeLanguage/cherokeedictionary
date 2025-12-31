package net.cherokeedictionary.searchLogic

class SortOrderComparator implements Comparator<Object>{
    int compare(Object o1, Object o2) {
        def sortOrder = SortOrder.instance
        if (sortOrder.getSortOrderByCode(o1.source) > sortOrder.getSortOrderByCode(o2.source)) {
            return 1
        } else if (sortOrder.getSortOrderByCode(o1.source) == sortOrder.getSortOrderByCode(o2.source)) {
            return 0
        } else {
            return -1
        }
    }
}