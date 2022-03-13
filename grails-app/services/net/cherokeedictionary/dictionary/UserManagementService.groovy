package net.cherokeedictionary.admin

import grails.gorm.services.Service
import net.cherokeedictionary.admin.UserManagement

@Service(UserManagement)
interface UserManagementService {

    UserManagement get(Serializable id)

    List<UserManagement> list(Map args)

    Long count()

    void delete(Serializable id)

    UserManagement save(UserManagement userManagement)

}