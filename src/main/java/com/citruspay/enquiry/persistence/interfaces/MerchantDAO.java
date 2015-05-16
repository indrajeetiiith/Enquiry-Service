package com.citruspay.enquiry.persistence.interfaces;


import com.citruspay.enquiry.persistence.entity.Merchant;

public interface MerchantDAO {


	Merchant findById(Integer merchantId);
	
	void delete(Integer merchantId);
	
	public Merchant findBySecretId(String secretId);
	
}
