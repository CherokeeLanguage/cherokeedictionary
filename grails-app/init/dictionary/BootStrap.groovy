package dictionary

import net.cherokeedictionary.auth.Role
import net.cherokeedictionary.auth.User
import net.cherokeedictionary.auth.UserRole

class BootStrap {

    def init = { servletContext ->
        def roleAdmin = new Role(authority: 'ROLE_ADMIN').save()
        def adminUser = new User(username: 'user', password: 'user').save()
        UserRole.create adminUser, roleAdmin
        UserRole.withSession {     it.flush();     it.clear()}
    }
    def destroy = {
    }
}
