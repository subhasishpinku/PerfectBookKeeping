package perfect.book.keeping.model;


public class LanguageList {
    String languageName;
    String languageCode;

    public LanguageList(String languageName,String languageCode){
        this.languageName=languageName;
        this.languageCode=languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
