package cherokee.dictionary.taglibUtil

class BaseSection {
    def title = ""
    def titleTranslit = ""
    def vocabulary = [:]
    def topics = []
    def dialogs = []

    String getLinkTitle() {
        return title.replaceAll(" ", "").replaceAll(",", "").replaceAll("\\?", "").replaceAll("\\.", "")
    }
}
