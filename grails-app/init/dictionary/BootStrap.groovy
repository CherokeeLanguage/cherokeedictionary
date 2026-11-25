package dictionary

import net.cherokeedictionary.admin.SourceManagement
import net.cherokeedictionary.relational.Dialect
import net.cherokeedictionary.relational.FormType
import net.cherokeedictionary.searchLogic.SortOrder

class BootStrap {

    def init = { servletContext ->
        // The spring-security-era seed (User/Role/UserRole) removed 2026-11-04
        // (cherokeedictionary#121): inert residue — the operative login is the
        // Admin domain; accounts live in the migrated database.
        def sources = SourceManagement.findAll()
        def sourceList = new LinkedList<Object>()
        sourceList.addAll(sources)
        def sortOrder = SortOrder.instance
        sortOrder.setSortOrder(sourceList)

        seedRelationalLookups()
    }

    // Lookup-table seeds for the relational schema (LexiconEtlService resolves
    // dialects by these codes; EntryWord rows reference these form types).
    private void seedRelationalLookups() {
        def dialects = [
            [dialect: 'O', name: 'Otali', description: 'Oklahoma / Overhill'],
            [dialect: 'G', name: 'Giduwah', description: 'Middle / Valley dialect'],
            [dialect: 'S', name: 'Snowbird', description: 'Snowbird community, NC'],
        ]
        dialects.each { d ->
            if (!Dialect.findByDialect(d.dialect)) {
                new Dialect(dialect: d.dialect, name: d.name, description: d.description).save(failOnError: true)
            }
        }

        def formTypes = [
            [name: 'base_entry', description: 'The primary headword form'],
            [name: 'synonym', description: 'Alternate comma-separated headword variant from the same legacy row'],
            [name: 'noun_plural', description: 'Noun/adjective plural (legacy column e)'],
            [name: 'v_first_pres', description: 'Verb first person present (legacy columns g/h)'],
            [name: 'v_third_past', description: 'Verb third person past (legacy columns i/j)'],
            [name: 'v_third_pres', description: 'Verb third person present (legacy columns k/l)'],
            [name: 'v_second_imper', description: 'Verb second person imperative (legacy columns m/n)'],
            [name: 'v_third_inf', description: 'Verb third person infinitive (legacy columns o/p)'],
        ]
        formTypes.each { f ->
            if (!FormType.findByName(f.name)) {
                new FormType(name: f.name, description: f.description).save(failOnError: true)
            }
        }
    }
    def destroy = {
    }
}
