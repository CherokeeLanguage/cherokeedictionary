package cherokee.dictionary

import grails.gorm.services.Service

@Service(SourceManagement)
interface SourceManagementService {

    SourceManagement get(Serializable id)

    List<SourceManagement> list(Map args)

    Long count()

    void delete(Serializable id)

    SourceManagement save(SourceManagement sourceManagement)

}