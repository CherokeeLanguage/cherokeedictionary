package net.cherokeedictionary.admin

import grails.gorm.services.Service
import net.cherokeedictionary.admin.Announcements

@Service(Announcements)
interface AnnouncementsService {

    Announcements get(Serializable id)

    List<Announcements> list(Map args)

    Long count()

    void delete(Serializable id)

    Announcements save(Announcements announcements)

}