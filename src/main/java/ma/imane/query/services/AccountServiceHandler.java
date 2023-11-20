package ma.imane.query.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ma.imane.common_api.enumerations.OperationType;
import ma.imane.common_api.events.AccountActivatedEvent;
import ma.imane.common_api.events.AccountCreatedEvent;
import ma.imane.common_api.events.AccountCreditedEvent;
import ma.imane.common_api.events.AccountDebitedEvent;
import ma.imane.common_api.exceptions.AccountNotFoundException;
import ma.imane.common_api.queries.GetAccountQuery;
import ma.imane.common_api.queries.GetAllAccountsQuery;
import ma.imane.query.entities.Account;
import ma.imane.query.entities.Operation;
import ma.imane.query.repositories.AccountRepository;
import ma.imane.query.repositories.OperationRepository;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AccountServiceHandler {

    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    @EventHandler // @Event Sourcing handler is for Aggregate
    public void on(AccountCreatedEvent accountCreatedEvent){
        log.info("Event Received: **| AccountCreatedEvent |** ");
        Account account =  Account
                .builder()
                .id(accountCreatedEvent.getId())
                .balance(accountCreatedEvent.getInitialBalance())
                .accountStatus(accountCreatedEvent.getAccountStatus())
                .currency(accountCreatedEvent.getCurrency())
                .build();
        Account savedAccount = accountRepository.save(account);
        log.info(String.format("New Account Created [ID: %s]", savedAccount.getId()));
    }

    @EventHandler
    public void on(AccountActivatedEvent accountActivatedEvent){
        log.info("Event Received: **| AccountActivatedEvent |** ");
        Account account = accountRepository.findById(accountActivatedEvent.getId()).get();
        account.setAccountStatus(accountActivatedEvent.getAccountStatus());
        Account savedAccount = accountRepository.save(account);
        log.info(String.format("New Account Updated [ID: %s]", savedAccount.getId()));
    }

    @EventHandler
    public void on(AccountCreditedEvent accountCreditedEvent){
        log.info("Event Received: **| AccountCreditedEvent |** ");
        Account account = accountRepository.findById(accountCreditedEvent.getId())
                .orElseThrow(()->{
                    throw new AccountNotFoundException("Account with this ID is NOT Found.");
                });
        account.setBalance(account.getBalance() + accountCreditedEvent.getAmount());

        // update account
        Account savedAccount = accountRepository.save(account);
        log.info(String.format("New Account Updated [ID: %s]", savedAccount.getId()));

        Operation operation = Operation.builder()
                .amount(accountCreditedEvent.getAmount())
                .date(new Date())
                .type(OperationType.CREDIT)
                .account(savedAccount)
                .build();
        operationRepository.save(operation);
        log.info("New  CREDIT Operation Created.");

    }

    @EventHandler
    public void on(AccountDebitedEvent accountDebitedEvent){
        log.info("Event Received: **| AccountDebitedEvent |** ");
        Account account = accountRepository.findById(accountDebitedEvent.getId())
                .orElseThrow(()->{
                    throw new AccountNotFoundException("Account with this ID is NOT Found.");
                });

        account.setBalance(account.getBalance() - accountDebitedEvent.getAmount());

        // update account
        Account savedAccount = accountRepository.save(account);
        log.info(String.format("New Account Updated [ID: %s]", savedAccount.getId()));

        Operation operation = Operation.builder()
                .amount(accountDebitedEvent.getAmount())
                //.date(new Date()) // to be avoided --> the date should be affected in the writing part
                .date(accountDebitedEvent.getOperationDate())
                .type(OperationType.DEBIT)
                .account(savedAccount)
                .build();
        operationRepository.save(operation);
        log.info("New  DEBIT Operation Created.");
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery getAllAccountQuery){
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountQuery getAccountQuery){
        return accountRepository.findById(getAccountQuery.getAccountId()).get();
    }

}
