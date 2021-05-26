package cherokee.gilt

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class GILTLangServiceSpec extends Specification {

    GILTLangService GILTLangService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new GILTLang(...).save(flush: true, failOnError: true)
        //new GILTLang(...).save(flush: true, failOnError: true)
        //GILTLang GILTLang = new GILTLang(...).save(flush: true, failOnError: true)
        //new GILTLang(...).save(flush: true, failOnError: true)
        //new GILTLang(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //GILTLang.id
    }

    void "test get"() {
        setupData()

        expect:
        GILTLangService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<GILTLang> GILTLangList = GILTLangService.list(max: 2, offset: 2)

        then:
        GILTLangList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        GILTLangService.count() == 5
    }

    void "test delete"() {
        Long GILTLangId = setupData()

        expect:
        GILTLangService.count() == 5

        when:
        GILTLangService.delete(GILTLangId)
        sessionFactory.currentSession.flush()

        then:
        GILTLangService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        GILTLang GILTLang = new GILTLang()
        GILTLangService.save(GILTLang)

        then:
        GILTLang.id != null
    }
}
