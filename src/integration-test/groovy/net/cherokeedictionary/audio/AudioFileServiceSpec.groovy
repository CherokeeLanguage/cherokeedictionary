package net.cherokeedictionary.audio

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class AudioFileServiceSpec extends Specification {

    AudioFileService audioFileService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new AudioFile(...).save(flush: true, failOnError: true)
        //new AudioFile(...).save(flush: true, failOnError: true)
        //AudioFile audioFile = new AudioFile(...).save(flush: true, failOnError: true)
        //new AudioFile(...).save(flush: true, failOnError: true)
        //new AudioFile(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //audioFile.id
    }

    void "test get"() {
        setupData()

        expect:
        audioFileService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<AudioFile> audioFileList = audioFileService.list(max: 2, offset: 2)

        then:
        audioFileList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        audioFileService.count() == 5
    }

    void "test delete"() {
        Long audioFileId = setupData()

        expect:
        audioFileService.count() == 5

        when:
        audioFileService.delete(audioFileId)
        sessionFactory.currentSession.flush()

        then:
        audioFileService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        AudioFile audioFile = new AudioFile()
        audioFileService.save(audioFile)

        then:
        audioFile.id != null
    }
}
