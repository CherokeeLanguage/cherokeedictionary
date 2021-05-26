package cherokee.mylearningstyle

import grails.gorm.services.Service

@Service(LearningEntry)
interface LearningEntryService {

    LearningEntry get(Serializable id)

    List<LearningEntry> list(Map args)

    Long count()

    void delete(Serializable id)

    LearningEntry save(LearningEntry learningEntry)

}