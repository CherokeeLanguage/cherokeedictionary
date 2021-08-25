package net.cherokeedictionary.admin

import grails.gorm.services.Service
import net.cherokeedictionary.admin.SourceManagement

@Service(SourceManagement)
interface SourceManagementService {

    SourceManagement get(Serializable id)

    List<SourceManagement> list(Map args)

    Long count()

    void delete(Serializable id)

    SourceManagement save(SourceManagement sourceManagement)

}