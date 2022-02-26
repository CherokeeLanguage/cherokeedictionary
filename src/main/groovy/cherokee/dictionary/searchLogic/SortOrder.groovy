package net.cherokeedictionary.searchLogic

class SortOrder {
    def map = [:]
    private static final INSTANCE = new SortOrder()
    static getInstance() { return INSTANCE }
    private SortOrder() { }

    int getSortOrderByCode(code) {
        def returnValue = map.get(code)
        if (returnValue) {
            return returnValue
        } else {
            return 1000
        }
    }

    def setSortOrder(sortOrder) {
        sortOrder.each {
            map.put(it.code, it.sortOrder)
        }

        return map
    }
}