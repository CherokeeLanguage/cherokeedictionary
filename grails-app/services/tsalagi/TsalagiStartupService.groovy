package tsalagi

import groovy.sql.Sql
import org.springframework.beans.factory.InitializingBean

import javax.sql.DataSource

/**
 * Runs automatically when Grails boots (`lazyInit = false`) using the active
 * `dataSource` (`cherokeedictionary`).
 *
 * Ensures Spring Session 2.x JDBC tables (`SPRING_SESSION` and `SPRING_SESSION_ATTRIBUTES`)
 * exist and upgrades obsolete Spring Session 1.x `SPRING_SESSION` tables (which lacked
 * `PRIMARY_ID` and `EXPIRY_TIME`).
 */
class TsalagiStartupService implements InitializingBean {

    static transactional = false
    static lazyInit = false

    DataSource dataSource

    @Override
    void afterPropertiesSet() throws Exception {
        if (dataSource == null) return

        Sql sql = new Sql(dataSource)
        try {
            ensureSpringSessionTables(sql)
        } catch (Exception ex) {
            log.warn("[TsalagiStartupService] Could not verify SPRING_SESSION tables: ${ex.message}")
        } finally {
            sql.close()
        }
    }

    private void ensureSpringSessionTables(Sql sql) {
        // Detect legacy Spring Session 1.x SPRING_SESSION table (missing PRIMARY_ID or EXPIRY_TIME)
        boolean needsRecreate = false
        try {
            sql.rows('SELECT PRIMARY_ID, EXPIRY_TIME FROM SPRING_SESSION WHERE 1 = 0')
        } catch (Exception missingColsEx) {
            needsRecreate = true
        }

        if (needsRecreate) {
            log.info('[TsalagiStartupService] Upgrading legacy SPRING_SESSION schema to Spring Session 2.x...')
            try { sql.execute('DROP TABLE IF EXISTS SPRING_SESSION_ATTRIBUTES') } catch (Exception ignored) {}
            try { sql.execute('DROP TABLE IF EXISTS SPRING_SESSION') } catch (Exception ignored) {}
        }

        sql.execute('''
            CREATE TABLE IF NOT EXISTS SPRING_SESSION (
                PRIMARY_ID CHAR(36) NOT NULL,
                SESSION_ID CHAR(36) NOT NULL,
                CREATION_TIME BIGINT NOT NULL,
                LAST_ACCESS_TIME BIGINT NOT NULL,
                MAX_INACTIVE_INTERVAL INT NOT NULL,
                EXPIRY_TIME BIGINT NOT NULL,
                PRINCIPAL_NAME VARCHAR(100),
                CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID),
                UNIQUE KEY SPRING_SESSION_IX1 (SESSION_ID),
                KEY SPRING_SESSION_IX2 (EXPIRY_TIME),
                KEY SPRING_SESSION_IX3 (PRINCIPAL_NAME)
            ) ENGINE=InnoDB ROW_FORMAT=DYNAMIC
        ''')

        sql.execute('''
            CREATE TABLE IF NOT EXISTS SPRING_SESSION_ATTRIBUTES (
                SESSION_PRIMARY_ID CHAR(36) NOT NULL,
                ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
                ATTRIBUTE_BYTES BLOB NOT NULL,
                CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
                CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID)
                    REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
            ) ENGINE=InnoDB ROW_FORMAT=DYNAMIC
        ''')
    }
}
