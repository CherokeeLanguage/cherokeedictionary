package net.cherokeedictionary

class DictionaryUtils {
    static String normalize(val) {
        if (val == null || val == "null" || val == "-----" || val == "") return null
        return val
    }
}
