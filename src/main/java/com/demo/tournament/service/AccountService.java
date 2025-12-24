package com.demo.tournament.service;

import com.demo.tournament.entity.Account;
import com.demo.tournament.entity.Platform;
import com.demo.tournament.repository.AccountRepository;
import com.demo.tournament.repository.PlatformRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PlatformRepository platformRepository;

    public AccountService(AccountRepository accountRepository, PlatformRepository platformRepository) {
        this.accountRepository = accountRepository;
        this.platformRepository = platformRepository;
    }

    @Transactional
    public Account createAccount(Account account) {
        account.setId(null);
        if (account.getStatus() == null) {
            account.setStatus("ACTIVE");
        }
        
        // Validate platform exists if provided
        if (account.getPlatform() != null && account.getPlatform().getId() != null) {
            Platform platform = platformRepository.findById(account.getPlatform().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Platform not found"));
            account.setPlatform(platform);
        }

        // Check unique constraint
        if (account.getPlatform() != null) {
            Optional<Account> existing = accountRepository.findByPlatformAndUsername(
                    account.getPlatform(), account.getUsername());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Account with username already exists for this platform");
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

    public List<Account> getAccountsByPlatformId(Long platformId) {
        return accountRepository.findByPlatformId(platformId);
    }

    public List<Account> getAccountsByPlatform(Platform platform) {
        return accountRepository.findByPlatform(platform);
    }

    @Transactional
    public Account updateAccount(Long id, Account accountUpdate) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
        
        if (accountUpdate.getUsername() != null) {
            // Check unique constraint if username is being changed
            if (account.getPlatform() != null) {
                Optional<Account> existing = accountRepository.findByPlatformAndUsername(
                        account.getPlatform(), accountUpdate.getUsername());
                if (existing.isPresent() && !existing.get().getId().equals(id)) {
                    throw new IllegalArgumentException("Username already exists for this platform");
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

