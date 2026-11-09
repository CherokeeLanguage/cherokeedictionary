package dictionary

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.jdbc.JdbcOperationsSessionRepository
import org.springframework.session.web.http.DefaultCookieSerializer

import javax.sql.DataSource

// cherokeedictionary#122: two mechanisms, both required for spring-session-jdbc
// 2.1.13 on Boot 2.1 in a Grails application:
// 1. GORM registers its dataSource bean after Boot's condition evaluation, so
//    JdbcSessionConfiguration's @ConditionalOnBean(DataSource) never fires
//    (SessionRepositoryUnavailableException at boot). The re-exported bean
//    definition is the early-visible DataSource the condition needs; the
//    repository itself is spring-session's own self-wiring configuration.
// 2. Boot 2.1 has no cookie-name property for the session store; its
//    DefaultCookieSerializer defaults to name "SESSION", breaking the
//    JSESSIONID contract (SC-3). The early-visible DefaultCookieSerializer bean
//    (this import is processed before auto-configuration, so the
//    @ConditionalOnMissingBean backs off) restores name JSESSIONID, path "/".
// 3. cherokeedictionary#135: the 2.1.13 attribute INSERT raced duplicate
//    keys before this remedy — every request writes its own session
//    copy, and the ADDED-delta path issues CREATE_SESSION_ATTRIBUTE_QUERY
//    (INSERT ... SELECT ... FROM SPRING_SESSION WHERE SESSION_ID = ?) per
//    request; two overlapping requests both marking the same attribute ADDED
//    (the remove-then-set pattern Grails' FLASH_SCOPE rides every redirect-
//    adjacent request) collide on the (SESSION_PRIMARY_ID, ATTRIBUTE_NAME)
//    primary key and 500 the loser's response commit. 2.1.13 has no flush-mode
//    knob on the JDBC store (that arrives with JdbcIndexedSessionRepository,
//    2.2+), so the remedy here is the store's own extension point:
//    setCreateSessionAttributeQuery, re-armed as an idempotent upsert — the
//    second racing INSERT updates the bytes instead of erroring (last-writer-
//    wins, the same semantics concurrent same-attribute writes already carry).
//    The suite's session:parallel-commit-clean wave is the guard (RED on the
//    unfixed store, GREEN with this override).
// Mechanism plumbing only — no session-handling code.
@Configuration
class SessionStoreDataSourceShim {

    // The 2.1.13 setter stores its string LITERALLY — unlike the built-in
    // constants it gets no %TABLE_NAME% substitution, and nothing in this
    // application sets spring.session.jdbc.table-name, so the default
    // SPRING_SESSION tables are hardcoded here (a table-name change would
    // need this query re-armed with it — kept adjacent to the store's other
    // plumbing so the coupling is auditable).
    static final String UPSERT_ATTRIBUTE_INSERT =
        "INSERT INTO SPRING_SESSION_ATTRIBUTES(SESSION_PRIMARY_ID, " +
        "ATTRIBUTE_NAME, ATTRIBUTE_BYTES) SELECT PRIMARY_ID, ?, ? " +
        "FROM SPRING_SESSION WHERE SESSION_ID = ? " +
        "ON DUPLICATE KEY UPDATE ATTRIBUTE_BYTES = VALUES(ATTRIBUTE_BYTES)"

    @Bean
    DataSource springSessionDataSource(@Qualifier('dataSource') DataSource dataSource) {
        dataSource
    }

    @Bean
    DefaultCookieSerializer sessionCookieSerializer() {
        new DefaultCookieSerializer(cookieName: 'JSESSIONID', cookiePath: '/')
    }

    // static: a BeanPostProcessor must not drag the configuration class into
    // early instantiation; the repository bean is a plain singleton created
    // after BPP registration, so the override lands before the first save.
    @Bean
    static BeanPostProcessor sessionAttributeUpsertQuery() {
        new UpsertAttributeQuery()
    }

    private static class UpsertAttributeQuery implements BeanPostProcessor {
        Object postProcessBeforeInitialization(Object bean, String beanName) {
            bean
        }

        Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof JdbcOperationsSessionRepository) {
                bean.setCreateSessionAttributeQuery(
                    SessionStoreDataSourceShim.UPSERT_ATTRIBUTE_INSERT)
            }
            bean
        }
    }
}
