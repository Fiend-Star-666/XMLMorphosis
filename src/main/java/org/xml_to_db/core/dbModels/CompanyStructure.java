package org.xml_to_db.core.dbModels;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CompanyStructure implements DatabaseModelObject {
    private int companyId;
    private String companyName;
    private Address headquarters;
    private List<Department> departments;
    private Map<String, Employee> employeeDirectory;
    private FinancialData financialInfo;
    private Date foundedDate;
    private String[] industryTags;
    private Map<String, List<String>> departmentProjects;
    private boolean isPubliclyTraded;
    private Date lastUpdated;
    private String updatedBy;

    @Data
    @AllArgsConstructor
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
    }

    @Data
    @AllArgsConstructor
    public static class Department {
        private int departmentId;
        private String departmentName;
        private Employee manager;
        private List<Employee> employees;
        private Date established;
    }

    @Data
    @AllArgsConstructor
    public static class Employee {
        private int employeeId;
        private String firstName;
        private String lastName;
        private String position;
        private Date hireDate;
        private double salary;
        private Map<String, String> skills;
    }

    @Data
    @AllArgsConstructor
    public static class FinancialData {
        private double annualRevenue;
        private double operatingCosts;
        private Map<String, Double> quarterlyEarnings;
        private List<Investment> investments;
    }

    @Data
    @AllArgsConstructor
    public static class Investment {
        private String investmentName;
        private double amount;
        private Date investmentDate;
        private String category;
    }
}
