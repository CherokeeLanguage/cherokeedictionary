package cherokee.gilt

import grails.gorm.services.Service

@Service(GILTLang)
interface GILTLangService {

    GILTLang get(Serializable id)

    List<GILTLang> list(Map args)

    Long count()

    void delete(Serializable id)

    GILTLang save(GILTLang GILTLang)

}