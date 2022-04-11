// Jac Stephen McDowall
// S2133078
// Glasgow Caledonian University

package jacmcdowall.mcdowall_jac_s2133078;

public class Item {
    private String title;
    private String description;
    private String geoLocation;

    public Item(String inputTitle, String inputDescription, String inputGeoLocation) {
       title = inputTitle;
       description = inputDescription;
       geoLocation = inputGeoLocation;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getGeoLocation() {
        return this.geoLocation;
    }

//    public void setTitle(String value) {
//        this.title = value;
//    }
//
//    public void setDescription(String value) {
//        this.description = value;
//    }
//
//    public void setGeoLocation(String value) {
//        this.geoLocation = value;
//    }
}
