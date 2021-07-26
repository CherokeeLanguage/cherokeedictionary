package cherokee.corpus

import grails.gorm.services.Service

@Service(Verse)
interface VerseService {

    Verse get(Serializable id)

    List<Verse> list(Map args)

    Long count()

    void delete(Serializable id)

    Verse save(Verse verse)

}