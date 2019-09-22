package com.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.entity.UserWallet;

public interface UserWalletRepository  extends JpaRepository<UserWallet, Long>{

	Optional<UserWallet> findByUsersIdAndWalletId(Long user, Long wallet);
}
