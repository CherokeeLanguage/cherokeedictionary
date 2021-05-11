package cherokee.gilt

import grails.gorm.services.Service

@Service(GILT)
interface GILTService {

    GILT get(Serializable id)

    List<GILT> list(Map args)

    Long count()

    void delete(Serializable id)

    GILT save(GILT GILT)

}