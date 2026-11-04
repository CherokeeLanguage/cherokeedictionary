package dictionary

import net.cherokeedictionary.admin.SourceManagement
import net.cherokeedictionary.searchLogic.SortOrder

class BootStrap {

    def init = { servletContext ->
        // The spring-security-era seed (User/Role/UserRole) removed 2026-11-04
        // (cherokeedictionary#121): inert residue — the operative login is the
        // Admin domain; accounts live in the migrated database.
        def sources = SourceManagement.findAll()
        def sourceList = new LinkedList<Object>()
        sourceList.addAll(sources)
        def sortOrder = SortOrder.instance
        sortOrder.setSortOrder(sourceList)
    }
    def destroy = {
    }
}
