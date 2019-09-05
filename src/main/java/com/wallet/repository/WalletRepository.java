package com.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long>{

}
