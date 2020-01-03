package com.wallet.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.dto.WalletItemDTO;
import com.wallet.entity.UserWallet;
import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.response.Response;
import com.wallet.service.UserWalletService;
import com.wallet.service.WalletItemService;
import com.wallet.util.Util;
import com.wallet.util.enums.TypeEnum;

@RestController
@RequestMapping("wallet-item")
public class WalletItemController {

	@Autowired
	private WalletItemService service;
	@Autowired
	private UserWalletService userWalletService;
	
	private static final Logger log = LoggerFactory.getLogger(WalletItemController.class);

	@PostMapping
	public ResponseEntity<Response<WalletItemDTO>> create(@Valid @RequestBody WalletItemDTO dto, BindingResult result) {

		Response<WalletItemDTO> response = new Response<WalletItemDTO>();

		if (result.hasErrors()) {
			result.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));

			return ResponseEntity.badRequest().body(response);
		}

		WalletItem wi = service.save(this.convertDtoToEntity(dto));

		response.setData(this.convertEntityToDto(wi));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping(value = "/{wallet}")
	public ResponseEntity<Response<Page<WalletItemDTO>>> findBetweenDates(@PathVariable("wallet") Long wallet,
			@RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
			@RequestParam(name = "page", defaultValue = "0") int page) {
		
		Response<Page<WalletItemDTO>> response = new Response<Page<WalletItemDTO>>();
		
		
		Optional<UserWallet> uw = userWalletService.findByUsersIdAndWalletId(Util.getAuthenticatedUserId(), wallet);
		
		if (!uw.isPresent()) {
			response.getErrors().add("Você não tem acesso a essa carteira");
			return ResponseEntity.badRequest().body(response);
		}
		
		Page<WalletItem> items = service.findBetweenDates(wallet, startDate, endDate, page);
		Page<WalletItemDTO> dto = items.map(i -> this.convertEntityToDto(i));
		response.setData(dto);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping(value = "/type/{wallet}")
	public ResponseEntity<Response<List<WalletItemDTO>>> findByWalletIdAndType(@PathVariable("wallet") Long wallet,
			@RequestParam("type") String type) {
		
		log.info("Buscando por carteira {} e tipo {}", wallet, type);
		
		Response<List<WalletItemDTO>> response = new Response<List<WalletItemDTO>>();
		List<WalletItem> list = service.findByWalletAndType(wallet, TypeEnum.getEnum(type));

		List<WalletItemDTO> dto = new ArrayList<>();
		list.forEach(i -> dto.add(this.convertEntityToDto(i)));
		response.setData(dto);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping(value = "/total/{wallet}")
	public ResponseEntity<Response<BigDecimal>> sumByWalletId(@PathVariable("wallet") Long wallet) {

		Response<BigDecimal> response = new Response<BigDecimal>();
		BigDecimal value = service.sumByWalletId(wallet);
		response.setData(value == null ? BigDecimal.ZERO : value);

		return ResponseEntity.ok().body(response);
	}

	@PutMapping
	public ResponseEntity<Response<WalletItemDTO>> update(@Valid @RequestBody WalletItemDTO dto, BindingResult result) {

		Response<WalletItemDTO> response = new Response<WalletItemDTO>();

		Optional<WalletItem> wi = service.findById(dto.getId());

		if (!wi.isPresent()) {
			result.addError(new ObjectError("WalletItem", "WalletItem não encontrado"));
		} else if (wi.get().getWallet().getId().compareTo(dto.getWallet()) != 0) {
				result.addError(new ObjectError("WalletItemChanged", "Você não pode alterar a carteira"));
		}

		if (result.hasErrors()) {
			result.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));

			return ResponseEntity.badRequest().body(response);
		}

		WalletItem saved = service.save(this.convertDtoToEntity(dto));

		response.setData(this.convertEntityToDto(saved));
		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping(value = "/{walletItemId}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> delete(@PathVariable("walletItemId") Long walletItemId) {
		Response<String> response = new Response<String>();

		Optional<WalletItem> wi = service.findById(walletItemId);

		if (!wi.isPresent()) {
			response.getErrors().add("WalletItem de id " + walletItemId + " não encontrada");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
		service.deleteById(walletItemId);
		response.setData("WalletItem de id "+ walletItemId + " apagada com sucesso");
		return ResponseEntity.ok().body(response);
	}

	private WalletItem convertDtoToEntity(WalletItemDTO dto) {
		WalletItem wi = new WalletItem();
		wi.setDate(dto.getDate());
		wi.setDescription(dto.getDescription());
		wi.setId(dto.getId());
		wi.setType(TypeEnum.getEnum(dto.getType()));
		wi.setValue(dto.getValue());

		Wallet w = new Wallet();
		w.setId(dto.getWallet());
		wi.setWallet(w);

		return wi;
	}

	private WalletItemDTO convertEntityToDto(WalletItem wi) {
		WalletItemDTO dto = new WalletItemDTO();
		dto.setDate(wi.getDate());
		dto.setDescription(wi.getDescription());
		dto.setId(wi.getId());
		dto.setType(wi.getType().getValue());
		dto.setValue(wi.getValue());
		dto.setWallet(wi.getWallet().getId());

		return dto;
	}
}
