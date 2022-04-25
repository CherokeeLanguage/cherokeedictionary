package dictionary

import net.cherokeedictionary.auth.Role
import net.cherokeedictionary.auth.User
import net.cherokeedictionary.auth.UserRole
import net.cherokeedictionary.admin.SourceManagement
import net.cherokeedictionary.searchLogic.SortOrder

class BootStrap {

    def init = { servletContext ->
        def roleAdmin = new Role(authority: 'ROLE_ADMIN')
        roleAdmin.save()
        def adminUser = new User(username: 'user', password: 'user')
        adminUser.save()
        def adminUserRole = new UserRole(user: adminUser, role: roleAdmin)
        adminUserRole.save()
        def sources = SourceManagement.findAll()
        def sourceList = new LinkedList<Object>()
        sourceList.addAll(sources)
        def sortOrder = SortOrder.instance
        sortOrder.setSortOrder(sourceList)
    }
    def destroy = {
    }
}
