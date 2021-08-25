package net.cherokeedictionary.audio

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class GenericAudioServiceSpec extends Specification {

    GenericAudioService genericAudioService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new GenericAudio(...).save(flush: true, failOnError: true)
        //new GenericAudio(...).save(flush: true, failOnError: true)
        //GenericAudio genericAudio = new GenericAudio(...).save(flush: true, failOnError: true)
        //new GenericAudio(...).save(flush: true, failOnError: true)
        //new GenericAudio(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //genericAudio.id
    }

    void "test get"() {
        setupData()

        expect:
        genericAudioService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<GenericAudio> genericAudioList = genericAudioService.list(max: 2, offset: 2)

        then:
        genericAudioList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        genericAudioService.count() == 5
    }

    void "test delete"() {
        Long genericAudioId = setupData()

        expect:
        genericAudioService.count() == 5

        when:
        genericAudioService.delete(genericAudioId)
        sessionFactory.currentSession.flush()

        then:
        genericAudioService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        GenericAudio genericAudio = new GenericAudio()
        genericAudioService.save(genericAudio)

        then:
        genericAudio.id != null
    }
}
