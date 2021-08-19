package net.cherokeedictionary.admin

import grails.gorm.services.Service
import net.cherokeedictionary.admin.Settings

@Service(Settings)
interface SettingsService {

    Settings get(Serializable id)

    List<Settings> list(Map args)

    Long count()

    void delete(Serializable id)

    Settings save(Settings settings)

}