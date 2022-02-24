package net.cherokeedictionary.admin

class SourceManagement {

    static constraints = {
    }

    String code
    String color
    String description
    String textColor
    String fullName
    boolean enabled
    String bibliographyFullAPA
    String searchParameter
    String url
    int sortOrder

    public String toString() {
        return code
    }
}
