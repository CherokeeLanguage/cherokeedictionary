package cherokee.gilt

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class GILTServiceSpec extends Specification {

    GILTService GILTService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new GILT(...).save(flush: true, failOnError: true)
        //new GILT(...).save(flush: true, failOnError: true)
        //GILT GILT = new GILT(...).save(flush: true, failOnError: true)
        //new GILT(...).save(flush: true, failOnError: true)
        //new GILT(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //GILT.id
    }

    void "test get"() {
        setupData()

        expect:
        GILTService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<GILT> GILTList = GILTService.list(max: 2, offset: 2)

        then:
        GILTList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        GILTService.count() == 5
    }

    void "test delete"() {
        Long GILTId = setupData()

        expect:
        GILTService.count() == 5

        when:
        GILTService.delete(GILTId)
        sessionFactory.currentSession.flush()

        then:
        GILTService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        GILT GILT = new GILT()
        GILTService.save(GILT)

        then:
        GILT.id != null
    }
}
