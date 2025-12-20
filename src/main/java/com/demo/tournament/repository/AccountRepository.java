package com.demo.tournament.repository;

import com.demo.tournament.entity.Account;
import com.demo.tournament.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findBySiteAndUsername(Site site, String username);

    List<Account> findBySite(Site site);

    List<Account> findBySiteId(Long siteId);

    Optional<Account> findByUsername(String username);
}

