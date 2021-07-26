package cherokee.audio

import grails.gorm.services.Service

@Service(GenericAudio)
interface GenericAudioService {

    GenericAudio get(Serializable id)

    List<GenericAudio> list(Map args)

    Long count()

    void delete(Serializable id)

    GenericAudio save(GenericAudio genericAudio)

}