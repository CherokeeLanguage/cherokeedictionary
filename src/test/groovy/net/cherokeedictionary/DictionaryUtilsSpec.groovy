package net.cherokeedictionary

import spock.lang.Specification

class DictionaryUtilsSpec extends Specification {
    def "normalize returns null for invalid inputs"() {
        expect:
        DictionaryUtils.normalize(input) == null
        
        where:
        input << [null, "null", "-----", ""]
    }

    def "normalize returns input for valid strings"() {
        expect:
        DictionaryUtils.normalize(input) == input
        
        where:
        input << ["word", "variant"]
    }
}
