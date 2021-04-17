package cherokee.dictionary

import grails.gorm.services.Service

@Service(Announcements)
interface AnnouncementsService {

    Announcements get(Serializable id)

    List<Announcements> list(Map args)

    Long count()

    void delete(Serializable id)

    Announcements save(Announcements announcements)

}