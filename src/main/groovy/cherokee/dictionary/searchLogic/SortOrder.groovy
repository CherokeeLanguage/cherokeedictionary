package net.cherokeedictionary.searchLogic

class SortOrder {
    def map = [:]
    private static final INSTANCE = new SortOrder()
    static getInstance() { return INSTANCE }
    private SortOrder() { }

    int getSortOrderByCode(code) {
        return map.get(code)
    }

    def setSortOrder(sortOrder) {
        sortOrder.each {
            map.put(it.code, it.sortOrder)
        }

        return map
    }
}