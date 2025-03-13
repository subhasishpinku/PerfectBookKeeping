package perfect.book.keeping.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompanyModifyResponse {

    public class Card {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("type")
        @Expose
        private Integer type;
        @SerializedName("card_number")
        @Expose
        private String cardNumber;
        @SerializedName("expiry")
        @Expose
        private String expiry;
        @SerializedName("cvv")
        @Expose
        private String cvv;
        @SerializedName("card_holder_name")
        @Expose
        private String cardHolderName;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpiry() {
            return expiry;
        }

        public void setExpiry(String expiry) {
            this.expiry = expiry;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public String getCardHolderName() {
            return cardHolderName;
        }

        public void setCardHolderName(String cardHolderName) {
            this.cardHolderName = cardHolderName;
        }

    }

    public class CompanyAddress {

        @SerializedName("address1")
        @Expose
        private String address1;
        @SerializedName("address2")
        @Expose
        private String address2;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("zipcode")
        @Expose
        private String zipcode;

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

    }

    public class CompanyBillingAddress {

        @SerializedName("address1")
        @Expose
        private String address1;
        @SerializedName("address2")
        @Expose
        private String address2;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("zipcode")
        @Expose
        private String zipcode;

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

    }

    public class CompanyImage {

        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;
        @SerializedName("original")
        @Expose
        private String original;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

    }

    public class CompanySubscription {

        @SerializedName("payment_status")
        @Expose
        private Integer paymentStatus;
        @SerializedName("amount")
        @Expose
        private Integer amount;
        @SerializedName("package")
        @Expose
        private Package _package;
        @SerializedName("card")
        @Expose
        private Card card;

        public Integer getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(Integer paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Package getPackage() {
            return _package;
        }

        public void setPackage(Package _package) {
            this._package = _package;
        }

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

    }

    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("created_by")
        @Expose
        private Integer createdBy;
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("is_govt_id_show")
        @Expose
        private Integer is_govt_id_show;
        @SerializedName("govt_id")
        @Expose
        private String govtId;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("is_receipt")
        @Expose
        private Integer isReceipt;
        @SerializedName("no_of_subusers")
        @Expose
        private Integer noOfSubusers;
        @SerializedName("date_format")
        @Expose
        private String dateFormat;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("role")
        @Expose
        private Role role;
        @SerializedName("company-subscription")
        @Expose
        private CompanySubscription companySubscription;
        @SerializedName("company_address")
        @Expose
        private CompanyAddress companyAddress;
        @SerializedName("company_billing_address")
        @Expose
        private CompanyBillingAddress companyBillingAddress;
        @SerializedName("company_image")
        @Expose
        private CompanyImage companyImage;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGovtId() {
            return govtId;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public void setGovtId(String govtId) {
            this.govtId = govtId;
        }

        public Integer getIsReceipt() {
            return isReceipt;
        }

        public void setIsReceipt(Integer isReceipt) {
            this.isReceipt = isReceipt;
        }

        public Integer getNoOfSubusers() {
            return noOfSubusers;
        }

        public void setNoOfSubusers(Integer noOfSubusers) {
            this.noOfSubusers = noOfSubusers;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public CompanySubscription getCompanySubscription() {
            return companySubscription;
        }

        public void setCompanySubscription(CompanySubscription companySubscription) {
            this.companySubscription = companySubscription;
        }

        public CompanyAddress getCompanyAddress() {
            return companyAddress;
        }

        public void setCompanyAddress(CompanyAddress companyAddress) {
            this.companyAddress = companyAddress;
        }

        public CompanyBillingAddress getCompanyBillingAddress() {
            return companyBillingAddress;
        }

        public void setCompanyBillingAddress(CompanyBillingAddress companyBillingAddress) {
            this.companyBillingAddress = companyBillingAddress;
        }

        public CompanyImage getCompanyImage() {
            return companyImage;
        }

        public void setCompanyImage(CompanyImage companyImage) {
            this.companyImage = companyImage;
        }


        public Integer getIs_govt_id_show() {
            return is_govt_id_show;
        }

        public void setIs_govt_id_show(Integer is_govt_id_show) {
            this.is_govt_id_show = is_govt_id_show;
        }
    }



        @SerializedName("data")
        @Expose
        private List<Datum> data;
        @SerializedName("message")
        @Expose
        private String message;

        public List<Datum> getData() {
            return data;
        }

        public void setData(List<Datum> data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


    public class Module {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("module-permission")
        @Expose
        private ModulePermission modulePermission;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ModulePermission getModulePermission() {
            return modulePermission;
        }

        public void setModulePermission(ModulePermission modulePermission) {
            this.modulePermission = modulePermission;
        }

    }

    public class ModulePermission {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("permission")
        @Expose
        private Integer permission;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getPermission() {
            return permission;
        }

        public void setPermission(Integer permission) {
            this.permission = permission;
        }

    }

    public class Package {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("subs_type_id")
        @Expose
        private Integer subsTypeId;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("price")
        @Expose
        private Integer price;
        @SerializedName("short_desc")
        @Expose
        private String shortDesc;
        @SerializedName("status")
        @Expose
        private Integer status;

        @SerializedName("color_code")
        @Expose
        private String color_code;
        @SerializedName("subs_type_data")
        @Expose
        private SubsTypeData subsTypeData;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getSubsTypeId() {
            return subsTypeId;
        }

        public void setSubsTypeId(Integer subsTypeId) {
            this.subsTypeId = subsTypeId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public String getShortDesc() {
            return shortDesc;
        }

        public void setShortDesc(String shortDesc) {
            this.shortDesc = shortDesc;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public SubsTypeData getSubsTypeData() {
            return subsTypeData;
        }

        public void setSubsTypeData(SubsTypeData subsTypeData) {
            this.subsTypeData = subsTypeData;
        }

        public String getColor_code() {
            return color_code;
        }
    }

    public class Role {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("module")
        @Expose
        private Module module;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Module getModule() {
            return module;
        }

        public void setModule(Module module) {
            this.module = module;
        }

    }

    public class SubsTypeData {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("status")
        @Expose
        private Integer status;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

    }
}
