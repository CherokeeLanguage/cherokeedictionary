package cherokee.dictionary

import grails.gorm.services.Service

@Service(Likespreadsheets)
interface LikespreadsheetsService {

    Likespreadsheets get(Serializable id)

    List<Likespreadsheets> list(Map args)

    Long count()

    void delete(Serializable id)

    Likespreadsheets save(Likespreadsheets likespreadsheets)

}