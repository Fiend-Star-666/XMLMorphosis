package org.xml_to_db.core.dbModels;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DataRetrievalLog {
    private int logID;
    private Integer companyId;
    private String employeeNumber;
    private String employeeLastName;
    private String employeeFirstName;
    private String employeeLocation;
    private String baseSalary;
    private String dailyRate;
    private String oDContract_ProductionPercentage;
    private String oDContract_EnrolledInHealthBenefits;
    private String oDContract_PTODays;
    private String oDContract_CEReimbursement;
    private String oDContract_PaidCEDays;
    private String oDContract_LegalEntity;
    private String oDContract_AgreementType;
    private Date oDContract_RenewalDate;
    private String oDContract_AutoRenew;
    private String oDContract_ContractSchedule;
    private String oDContract_ContractScheduleFullDescription;
    private String oDContract_ProductionPaymentFrequency;
    private String oDContract_AOADuesPercentage;
    private String oDContract_LicensureDues;
    private String oDContract_MultipleLocations;
    private String oDContract_SecondaryLocation;
    private String locationState;
    private String region;
    private Date hireDate;
    private Date originalHireDate;
    private Date terminationDate;
    private String employeeGender;
    private Date employeeBirthDate;
    private Date seniorityDate;
    private String job;
    private String payType;
    private String payClass;
    private String oDContract_ProductionThreshold;
    private String oDContract_TuitionReimbursement;
    private String oDContract_CarTravelAllowance;
    private String oDContract_SignOnBonus;
    private String oDContract_StateDuesPercentage;
    private String oDContract_PaidHolidays;
    private String oDContract_PLIProvision;
    private Date retrievedDate;
    private Date createdDate;
    private String createdBy;
    private Date updatedDate;
    private String updatedBy;

}
