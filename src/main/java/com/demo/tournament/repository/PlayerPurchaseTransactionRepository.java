package com.demo.tournament.repository;

import com.demo.tournament.entity.PlayerPurchaseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerPurchaseTransactionRepository extends JpaRepository<PlayerPurchaseTransaction, Long> {

    List<PlayerPurchaseTransaction> findByRound_RoundId(Long roundId);

    List<PlayerPurchaseTransaction> findByGameAccount(String gameAccount);

    List<PlayerPurchaseTransaction> findByStatus(String status);
}
