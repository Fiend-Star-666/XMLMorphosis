package org.xml_to_db.core.dbModels;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserProfile implements DatabaseModelObject {
    private int userId;
    private String username;
    private String email;
    private String fullName;
    private Date dateOfBirth;
    private String country;
    private Date registrationDate;
    private Date lastLoginDate;
    private boolean isActive;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
}