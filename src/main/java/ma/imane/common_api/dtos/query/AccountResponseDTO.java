package ma.imane.common_api.dtos.query;


import ma.imane.common_api.enumerations.AccountStatus;

public class AccountResponseDTO {
        private String id;
        private String currency;
        private double balance;
        private AccountStatus accountStatus;
}
