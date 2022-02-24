package dictionary

import net.cherokeedictionary.auth.Role
import net.cherokeedictionary.auth.User
import net.cherokeedictionary.auth.UserRole
import net.cherokeedictionary.admin.SourceManagement
import net.cherokeedictionary.searchLogic.SortOrder

class BootStrap {

    def init = { servletContext ->
        def roleAdmin = new Role(authority: 'ROLE_ADMIN').save()
        def adminUser = new User(username: 'user', password: 'user').save()
        def sources = SourceManagement.findAll()
        def sourceList = new LinkedList<Object>()
        sourceList.addAll(sources)
        def sortOrder = SortOrder.instance
        sortOrder.setSortOrder(sourceList)
        UserRole.create adminUser, roleAdmin
        UserRole.withSession {     it.flush();     it.clear()}
    }
    def destroy = {
    }
}
