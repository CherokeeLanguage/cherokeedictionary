package net.cherokeedictionary.corpus

import grails.gorm.services.Service

@Service(CorpusSource)
interface CorpusSourceService {

    CorpusSource get(Serializable id)

    List<CorpusSource> list(Map args)

    Long count()

    void delete(Serializable id)

    CorpusSource save(CorpusSource corpusSource)

}