package net.cherokeedictionary.corpus

import grails.gorm.services.Service

@Service(NLPCorpus)
interface NLPCorpusService {

    NLPCorpus get(Serializable id)

    List<NLPCorpus> list(Map args)

    Long count()

    void delete(Serializable id)

    NLPCorpus save(NLPCorpus NLPCorpus)

}