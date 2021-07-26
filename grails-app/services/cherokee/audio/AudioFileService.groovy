package cherokee.audio

import grails.gorm.services.Service

@Service(AudioFile)
interface AudioFileService {

    AudioFile get(Serializable id)

    List<AudioFile> list(Map args)

    Long count()

    void delete(Serializable id)

    AudioFile save(AudioFile audioFile)

}