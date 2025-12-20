package com.demo.tournament.service;

import com.demo.tournament.entity.Account;
import com.demo.tournament.entity.Site;
import com.demo.tournament.repository.AccountRepository;
import com.demo.tournament.repository.SiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final SiteRepository siteRepository;

    public AccountService(AccountRepository accountRepository, SiteRepository siteRepository) {
        this.accountRepository = accountRepository;
        this.siteRepository = siteRepository;
    }

    @Transactional
    public Account createAccount(Account account) {
        account.setId(null);
        if (account.getStatus() == null) {
            account.setStatus("ACTIVE");
        }
        
        // Validate site exists if provided
        if (account.getSite() != null && account.getSite().getId() != null) {
            Site site = siteRepository.findById(account.getSite().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Site not found"));
            account.setSite(site);
        }
        
        // Check unique constraint
        if (account.getSite() != null) {
            Optional<Account> existing = accountRepository.findBySiteAndUsername(
                    account.getSite(), account.getUsername());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Account with username already exists for this site");
            }
        }
        
        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> getAccountsBySiteId(Long siteId) {
        return accountRepository.findBySiteId(siteId);
    }

    public List<Account> getAccountsBySite(Site site) {
        return accountRepository.findBySite(site);
    }

    @Transactional
    public Account updateAccount(Long id, Account accountUpdate) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
        
        if (accountUpdate.getUsername() != null) {
            // Check unique constraint if username is being changed
            if (account.getSite() != null) {
                Optional<Account> existing = accountRepository.findBySiteAndUsername(
                        account.getSite(), accountUpdate.getUsername());
                if (existing.isPresent() && !existing.get().getId().equals(id)) {
                    throw new IllegalArgumentException("Username already exists for this site");
                }
            }
            account.setUsername(accountUpdate.getUsername());
        }
        if (accountUpdate.getStatus() != null) {
            account.setStatus(accountUpdate.getStatus());
        }
        
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }
}

