package dictionary

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
// Mechanism plumbing only — no session-handling code.
@Configuration
class SessionStoreDataSourceShim {

    @Bean
    DataSource springSessionDataSource(@Qualifier('dataSource') DataSource dataSource) {
        dataSource
    }

    @Bean
    DefaultCookieSerializer sessionCookieSerializer() {
        new DefaultCookieSerializer(cookieName: 'JSESSIONID', cookiePath: '/')
    }
}
