package com.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.entity.WalletItem;

public interface WalletItemRepository extends JpaRepository<WalletItem, Long> {

}
