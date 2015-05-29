package com.citruspay.enquiry.gateway;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.lyra.vads.ws.v4.CreatePaymentInfo;
import com.lyra.vads.ws.v4.CreateWithThreeDSResponse;
import com.lyra.vads.ws.v4.TransactionInfo;

public class PayZenPGSignatureStringCreator {
	
	private static final Logger	logger = LoggerFactory.getLogger(PayZenPGSignatureStringCreator.class);
	
	public static String createPlusSeparatedString(List<Object> strings) {
        Joiner joiner = Joiner.on("+").useForNull("");
        return joiner.join(strings);
	}
		
	/**
	 * 
	 * @param createInfo
	 * @param payzenCertificate
	 * @return String
	 * This method will create a String which will get encrypted
	 */
	public static String createSignatureDataForMpiRequestCall(CreatePaymentInfo createInfo,
			String payzenCertificate) {
		
		PayZenPGDataEncryptor encrytData = null;
		String wsSignature = null;
		
		List<Object> params = new ArrayList<Object>();
		
		//Starting PaymentGeneralInfo parameters
		params.add(createInfo.getPaymentGeneralInfo().getSiteId());
		params.add(createInfo.getPaymentGeneralInfo().getTransactionId());
		params.add(createInfo.getPaymentGeneralInfo().getPaymentSource());
		params.add(createInfo.getPaymentGeneralInfo().getOrderId());
		params.add(createInfo.getPaymentGeneralInfo().getOrderInfo());
		params.add(createInfo.getPaymentGeneralInfo().getOrderInfo2());
		params.add(createInfo.getPaymentGeneralInfo().getOrderInfo3());
		params.add(createInfo.getPaymentGeneralInfo().getAmount());
		params.add(createInfo.getPaymentGeneralInfo().getCurrency());
		params.add(createInfo.getPaymentGeneralInfo().getValidationMode());
		
		//Starting CardInfo parameters
		params.add(createInfo.getCardInfo().getCardNumber());
		params.add(createInfo.getCardInfo().getCardNetwork());
		params.add(createInfo.getCardInfo().getExpiryMonth());
		params.add(createInfo.getCardInfo().getExpiryYear());
		params.add(createInfo.getCardInfo().getCvv());
		params.add(createInfo.getCardInfo().getCardIdent());
		
		if (createInfo.getCardInfo().getCardBirthDay() != null) {
			//Converting Birthday in format required while calculating Signature
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			//String strBirthDay = dateFormat.format(new Date());
			
			params.add(dateFormat.format(new Date()));
		} else {
			params.add(createInfo.getCardInfo().getCardBirthDay());
		}
		params.add(createInfo.getCardInfo().getContractNumber());
		params.add(createInfo.getCardInfo().getPaymentOptionCode());
		
		//Starting SubPaymentInfo parameters
		params.add(createInfo.getSubPaymentInfo().getSubPaymentType());
		params.add(createInfo.getSubPaymentInfo().getSubReference());
		params.add(createInfo.getSubPaymentInfo().getSubPaymentNumber());
		
		//Starting CustomerInfo parameters
		params.add(createInfo.getCustomerInfo().getCustomerId());
		params.add(createInfo.getCustomerInfo().getCustomerTitle());
		params.add(createInfo.getCustomerInfo().getCustomerStatus());
		params.add(createInfo.getCustomerInfo().getCustomerName());
		
		//new params added firstname and lastname
		params.add(createInfo.getCustomerInfo().getCustomerFirstName());
		params.add(createInfo.getCustomerInfo().getCustomerLastName());
		
		params.add(createInfo.getCustomerInfo().getCustomerPhone());
		params.add(createInfo.getCustomerInfo().getCustomerEmail());
		params.add(createInfo.getCustomerInfo().getCustomerAddressNumber());
		params.add(createInfo.getCustomerInfo().getCustomerAddress());
		params.add(createInfo.getCustomerInfo().getCustomerDistrict());
		params.add(createInfo.getCustomerInfo().getCustomerZip());
		params.add(createInfo.getCustomerInfo().getCustomerCity());
		params.add(createInfo.getCustomerInfo().getCustomerCountry());
		params.add(createInfo.getCustomerInfo().getLanguage());
		params.add(createInfo.getCustomerInfo().getCustomerIP());
		
		if (createInfo.getCustomerInfo().isCustomerSendEmail() != null) {
			if(createInfo.getCustomerInfo().isCustomerSendEmail() == false) {
				params.add("0");
			} else {
				params.add("1");
			}
		} else {
			params.add(createInfo.getCustomerInfo().isCustomerSendEmail());
		}
		params.add(createInfo.getCustomerInfo().getCustomerCellPhone());
		
		if (createInfo.getCustomerInfo().getExtInfo() != null) {
			if(createInfo.getCustomerInfo().getExtInfo().size() > 0) {
				for(int i=0; i < createInfo.getCustomerInfo().getExtInfo().size(); i++) {
					params.add(createInfo.getCustomerInfo().getExtInfo().get(i).getKey());
					params.add(createInfo.getCustomerInfo().getExtInfo().get(i).getValue());
				}
			} else {
				params.add(null);
			}
		} else {
			params.add(createInfo.getCustomerInfo().getExtInfo());
		}
		
		//Starting ShippingInfo parameters
		params.add(createInfo.getShippingInfo().getShippingCity());
		params.add(createInfo.getShippingInfo().getShippingCountry());
		params.add(createInfo.getShippingInfo().getShippingDeliveryCompanyName());
		params.add(createInfo.getShippingInfo().getShippingName());
		params.add(createInfo.getShippingInfo().getShippingPhone());
		params.add(createInfo.getShippingInfo().getShippingSpeed());
		params.add(createInfo.getShippingInfo().getShippingState());
		params.add(createInfo.getShippingInfo().getShippingStatus());
		params.add(createInfo.getShippingInfo().getShippingStreetNumber());
		params.add(createInfo.getShippingInfo().getShippingStreet());
		params.add(createInfo.getShippingInfo().getShippingStreet2());
		params.add(createInfo.getShippingInfo().getShippingDistrict());
		params.add(createInfo.getShippingInfo().getShippingType());
		params.add(createInfo.getShippingInfo().getShippingZipCode());
		
		//Starting ExtraInfo parameters
		params.add(createInfo.getExtraInfo().getCtxMode());
		params.add(createInfo.getExtraInfo().getBrowserUserAgent());
		params.add(createInfo.getExtraInfo().getBrowserAccept());
		
		//Starting ExtraInfo parameters
		params.add(payzenCertificate);
		
		String createInfoData = createPlusSeparatedString(params);
		try {
			encrytData = new PayZenPGDataEncryptor();
			wsSignature = encrytData.encryptString(createInfoData);
		} catch(Exception e) {
			logger.error("Exception while Creating Signature for createInfoData: ",e);
		}
		return wsSignature;
	}
	
	/**	Creating Signature Data String for Calculating Signature,
	 *  which will be compared with signature Received from PayZen.
	 *  This is used post we received response of createWithThreeDSResponse() method,
	 *  during server to server call response.
	 */
	public static String createSignatureStringForMpiResponse(
			CreateWithThreeDSResponse createWithThreeDSResponse, String payzenCertificate) {
		
		PayZenPGDataEncryptor encrytData = null;
		String wsSignature = null;
		
		List<Object> params = new ArrayList<Object>();
		
		if (createWithThreeDSResponse != null) {
			
			//Starting createWithThreeDSResponse parameters
			params.add(createWithThreeDSResponse.getErrorCode());
			params.add(createWithThreeDSResponse.getExtendedErrorCode());
			params.add(createWithThreeDSResponse.getTimestamp());
			
			//Starting createWithThreeDSResponse -> VeResPAReqInfo parameters
			if (createWithThreeDSResponse.getVeResPAReqInfo() != null) {
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getErrorCode());
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getErrorDetail());
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getTimestamp());
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getThreeDSAcctId());
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getThreeDSAcsUrl());
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getThreeDSBrand());
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getThreeDSEncodedPareq());
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getThreeDSEnrolled());
				params.add(createWithThreeDSResponse.getVeResPAReqInfo().getThreeDSRequestId());
			} else {
				params.add(createWithThreeDSResponse.getVeResPAReqInfo());
			}
			
			//Starting createWithThreeDSResponse -> TransactionInfo parameters
			if (createWithThreeDSResponse.getTransactionInfo() != null) {
				params.add(createWithThreeDSResponse.getTransactionInfo().getErrorCode());
				params.add(createWithThreeDSResponse.getTransactionInfo().getExtendedErrorCode());
				params.add(createWithThreeDSResponse.getTransactionInfo().getTransactionStatus());
				params.add(createWithThreeDSResponse.getTransactionInfo().getTimestamp());
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> PaymentGeneralInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo() != null) {
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getSiteId());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getPaymentSource());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getOrderId());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getOrderInfo());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getOrderInfo2());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getOrderInfo3());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getTransactionId());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getSequenceNumber());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getAmount());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getInitialAmount());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getCurrency());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getEffectiveAmount());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getEffectiveCurrency());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getType());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getMultiplePayment());
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo().getExtTransId());
					
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getPaymentGeneralInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> CardInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getCardInfo() != null) {
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getCardNumber());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getCardNetwork());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getCardBrand());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getCardCountry());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getExpiryMonth());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getExpiryYear());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getContractNumber());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getCardBankCode());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo().getCardProductCode());
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getCardInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> ThreeDSInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo() != null) {
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSTransactionCondition());
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSEnrolled());
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSStatus());
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSEci());
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSXid());
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSCavvAlgorithm());
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSCavv());
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSSignValid());
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo().getThreeDSBrand());
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getThreeDSInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> AuthorizationInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getAuthorizationInfo() != null) {
					params.add(createWithThreeDSResponse.getTransactionInfo().getAuthorizationInfo().getAuthMode());
					params.add(createWithThreeDSResponse.getTransactionInfo().getAuthorizationInfo().getAuthAmount());
					params.add(createWithThreeDSResponse.getTransactionInfo().getAuthorizationInfo().getAuthCurrency());
					params.add(createWithThreeDSResponse.getTransactionInfo().getAuthorizationInfo().getAuthNumber());
					params.add(createWithThreeDSResponse.getTransactionInfo().getAuthorizationInfo().getAuthResult());
					params.add(createWithThreeDSResponse.getTransactionInfo().getAuthorizationInfo().getAuthCVV2CVC2());
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getAuthorizationInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> MarkInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getMarkInfo() != null) {
					params.add(createWithThreeDSResponse.getTransactionInfo().getMarkInfo().getMarkAmount());
					params.add(createWithThreeDSResponse.getTransactionInfo().getMarkInfo().getMarkCurrency());
					params.add(createWithThreeDSResponse.getTransactionInfo().getMarkInfo().getMarkNb());
					params.add(createWithThreeDSResponse.getTransactionInfo().getMarkInfo().getMarkResult());
					params.add(createWithThreeDSResponse.getTransactionInfo().getMarkInfo().getMarkCVV2CVC2());
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getMarkInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> WarrantyDetailsInfo
				//parameters
				if(createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo() != null){
					params.add(createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().getPaymentError());
					params.add(createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().getWarrantyResult());
					
					if (createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().getLocalControl() != null) {
						if (createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().getLocalControl().size() > 0) {
							for(int i=0; i < createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().getLocalControl().size(); i++) {							
								params.add(createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().getLocalControl().get(i).getName());
								if (createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().getLocalControl().get(i).isResult() == false) {
									params.add("0");
								} else {
									params.add("1");
								}
							}
						} else {
							params.add(null);
						}
					} else {
						params.add(createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().getLocalControl());
					}
					
					if (createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().isLitige() != null) {
						if (createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().isLitige() == false) {
							params.add("0");
						} else {
							params.add("1");
						}
					} else {
						params.add(createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo().isLitige());
					}
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getWarrantyDetailsInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> CaptureInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getCaptureInfo() != null) {
					params.add(createWithThreeDSResponse.getTransactionInfo().getCaptureInfo().getCaptureNumber());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCaptureInfo().getRapprochementStatut());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCaptureInfo().getRefundAmount());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCaptureInfo().getRefundCurrency());
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getCaptureInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> CustomerInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getCustomerInfo() != null) {
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerId());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerTitle());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerStatus());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerName());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerFirstName());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerLastName());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerPhone() );
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerEmail());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerAddressNumber());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerAddress());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerDistrict());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerZip());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerCity());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerCountry());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getLanguage());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerIP());
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getCustomerCellPhone());
					
					if (createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getExtInfo() != null) {
						if (createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getExtInfo().size() > 0) {
							for(int i=0; i < createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getExtInfo().size(); i++) {
								params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getExtInfo().get(i).getKey());
								params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getExtInfo().get(i).getValue());
							}
						} else {
							params.add(null);
						}
					} else {
						params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo().getExtInfo());
					}
					
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getCustomerInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> ShippingInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getShippingInfo() != null) {
					
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingCity());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingCountry());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingDeliveryCompanyName());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingName());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingPhone());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingSpeed());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingState());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingStatus());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingStreetNumber());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingStreet());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingStreet2());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingDistrict());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingType());
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo().getShippingZipCode());
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getShippingInfo());
				}
				
				//Starting createWithThreeDSResponse -> TransactionInfo -> ExtraInfo
				//parameters
				if (createWithThreeDSResponse.getTransactionInfo().getExtraInfo() != null) {
					params.add(createWithThreeDSResponse.getTransactionInfo().getExtraInfo().getCtxMode());
				} else {
					params.add(createWithThreeDSResponse.getTransactionInfo().getExtraInfo());
				}
				
				params.add(createWithThreeDSResponse.getTransactionInfo().getTransactionStatusLabel());
			} else {
				params.add(createWithThreeDSResponse.getTransactionInfo());
			}
		} else {
			params.add(createWithThreeDSResponse);
		}
		params.add(payzenCertificate);
		
		String createInfoData = createPlusSeparatedString(params);
		try {
			encrytData = new PayZenPGDataEncryptor();
			wsSignature = encrytData.encryptString(createInfoData);
		} catch(Exception e) {
			logger.error("Exception while Creating Signature for createInfoData: ",e);
		}
		return wsSignature;
	}
	
	/**	Creating Signature Data String for Calculating Signature,
	 *  which will be compared with signature Received from PayZen.
	 *  This generating Signature will be used post 3d-secure Response is,
	 *  received from Payzen.
	 */
	public static String createSignatureStringToSendInFinalizeWithThreeDS(String threeDSrequestId,
			String pares, String payzenCertificate) {
		
		PayZenPGDataEncryptor encrytData = null;
		String wsSignature = null;
		
		List<Object> params = new ArrayList<Object>();
		
		params.add(threeDSrequestId);
		params.add(pares);
		params.add(payzenCertificate);
		
		String createInfoData = createPlusSeparatedString(params);
		try {
			encrytData = new PayZenPGDataEncryptor();
			wsSignature = encrytData.encryptString(createInfoData);
		} catch(Exception e) {
			logger.error("Exception while Creating Signature for createInfoData: ",e);
		}
		return wsSignature;
	}
	
	/**	Creating Signature Data String for Calculating Signature,
	 *  which will be compared with signature Received from PayZen.
	 *  This generating Signature will be used post finalyzecall's response is,
	 *  received from Payzen.
	 */
	public static String 
		createSignatureStringForPaymentResponse(TransactionInfo transactionInfo,
				String payzenCertificate) {
		
		PayZenPGDataEncryptor encrytData = null;
		String wsSignature = null;
		
		List<Object> params = new ArrayList<Object>();
		
		params.add(transactionInfo.getErrorCode());
		params.add(transactionInfo.getExtendedErrorCode());
		params.add(transactionInfo.getTransactionStatus());
		params.add(transactionInfo.getTimestamp());
		//params.add(transactionInfo.getTransactionStatusLabel());
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> PaymentGeneralInfo
		//parameters
		if (transactionInfo.getPaymentGeneralInfo() != null) {
			params.add(transactionInfo.getPaymentGeneralInfo().getSiteId());
			params.add(transactionInfo.getPaymentGeneralInfo().getPaymentSource());
			params.add(transactionInfo.getPaymentGeneralInfo().getOrderId());
			params.add(transactionInfo.getPaymentGeneralInfo().getOrderInfo());
			params.add(transactionInfo.getPaymentGeneralInfo().getOrderInfo2());
			params.add(transactionInfo.getPaymentGeneralInfo().getOrderInfo3());
			params.add(transactionInfo.getPaymentGeneralInfo().getTransactionId());
			params.add(transactionInfo.getPaymentGeneralInfo().getSequenceNumber());
			params.add(transactionInfo.getPaymentGeneralInfo().getAmount());
			params.add(transactionInfo.getPaymentGeneralInfo().getInitialAmount());
			params.add(transactionInfo.getPaymentGeneralInfo().getCurrency());
			params.add(transactionInfo.getPaymentGeneralInfo().getEffectiveAmount());
			params.add(transactionInfo.getPaymentGeneralInfo().getEffectiveCurrency());
			params.add(transactionInfo.getPaymentGeneralInfo().getType());
			params.add(transactionInfo.getPaymentGeneralInfo().getMultiplePayment());
			params.add(transactionInfo.getPaymentGeneralInfo().getExtTransId());
		} else {
			params.add(transactionInfo.getPaymentGeneralInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> CardInfo
		//parameters
		if (transactionInfo.getCardInfo() != null) {
			params.add(transactionInfo.getCardInfo().getCardNumber());
			params.add(transactionInfo.getCardInfo().getCardNetwork());
			params.add(transactionInfo.getCardInfo().getCardBrand());
			params.add(transactionInfo.getCardInfo().getCardCountry());
			params.add(transactionInfo.getCardInfo().getExpiryMonth());
			params.add(transactionInfo.getCardInfo().getExpiryYear());
			params.add(transactionInfo.getCardInfo().getContractNumber());
			params.add(transactionInfo.getCardInfo().getCardBankCode());
			params.add(transactionInfo.getCardInfo().getCardProductCode());
		} else {
			params.add(transactionInfo.getCardInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> ThreeDSInfo
		//parameters
		if (transactionInfo.getThreeDSInfo() != null) {
			params.add(transactionInfo.getThreeDSInfo().getThreeDSTransactionCondition());
			params.add(transactionInfo.getThreeDSInfo().getThreeDSEnrolled());
			params.add(transactionInfo.getThreeDSInfo().getThreeDSStatus());
			params.add(transactionInfo.getThreeDSInfo().getThreeDSEci());
			params.add(transactionInfo.getThreeDSInfo().getThreeDSXid());
			params.add(transactionInfo.getThreeDSInfo().getThreeDSCavvAlgorithm());
			params.add(transactionInfo.getThreeDSInfo().getThreeDSCavv());
			params.add(transactionInfo.getThreeDSInfo().getThreeDSSignValid());
			params.add(transactionInfo.getThreeDSInfo().getThreeDSBrand());
		} else {
			params.add(transactionInfo.getThreeDSInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> AuthorizationInfo
		//parameters
		if (transactionInfo.getAuthorizationInfo() != null) {
			params.add(transactionInfo.getAuthorizationInfo().getAuthMode());
			params.add(transactionInfo.getAuthorizationInfo().getAuthAmount());
			params.add(transactionInfo.getAuthorizationInfo().getAuthCurrency());
			params.add(transactionInfo.getAuthorizationInfo().getAuthNumber());
			params.add(transactionInfo.getAuthorizationInfo().getAuthResult());
			params.add(transactionInfo.getAuthorizationInfo().getAuthCVV2CVC2());
		} else {
			params.add(transactionInfo.getAuthorizationInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> MarkInfo
		//parameters
		if (transactionInfo.getMarkInfo() != null) {
			params.add(transactionInfo.getMarkInfo().getMarkAmount());
			params.add(transactionInfo.getMarkInfo().getMarkCurrency());
			params.add(transactionInfo.getMarkInfo().getMarkNb());
			params.add(transactionInfo.getMarkInfo().getMarkResult());
			params.add(transactionInfo.getMarkInfo().getMarkCVV2CVC2());
		} else {
			params.add(transactionInfo.getMarkInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> WarrantyDetailsInfo
		//parameters
		if (transactionInfo.getWarrantyDetailsInfo() != null) {
			params.add(transactionInfo.getWarrantyDetailsInfo().getPaymentError());
			params.add(transactionInfo.getWarrantyDetailsInfo().getWarrantyResult());
			
			if (transactionInfo.getWarrantyDetailsInfo().getLocalControl() != null) {
				if (transactionInfo.getWarrantyDetailsInfo().getLocalControl().size() > 0) {
					for(int i=0; i < transactionInfo.getWarrantyDetailsInfo().getLocalControl().size(); i++) {
						
						params.add(transactionInfo.getWarrantyDetailsInfo().getLocalControl().get(i).getName());
						
						if (transactionInfo.getWarrantyDetailsInfo().getLocalControl().get(i).isResult() == false) {
							params.add("0");
						} else {
							params.add("1");
						}
					}
				} else {
					params.add(null);
				}
			} else {
				params.add(transactionInfo.getWarrantyDetailsInfo().getLocalControl());
			}
			
			if (transactionInfo.getWarrantyDetailsInfo().isLitige() != null) {
				
				if (transactionInfo.getWarrantyDetailsInfo().isLitige() == false) {
					params.add("0");
				} else {
					params.add("1");
				}
			} else {
				params.add(transactionInfo.getWarrantyDetailsInfo().isLitige());
			}
		} else {
			params.add(transactionInfo.getWarrantyDetailsInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> CaptureInfo
		//parameters
		if (transactionInfo.getCaptureInfo() != null) {
			params.add(transactionInfo.getCaptureInfo().getCaptureNumber());
			params.add(transactionInfo.getCaptureInfo().getRapprochementStatut());
			params.add(transactionInfo.getCaptureInfo().getRefundAmount());
			params.add(transactionInfo.getCaptureInfo().getRefundCurrency());
		} else {
			params.add(transactionInfo.getCaptureInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> CustomerInfo
		//parameters
		if (transactionInfo.getCustomerInfo() != null) {
			params.add(transactionInfo.getCustomerInfo().getCustomerId());
			params.add(transactionInfo.getCustomerInfo().getCustomerTitle());
			params.add(transactionInfo.getCustomerInfo().getCustomerStatus());
			params.add(transactionInfo.getCustomerInfo().getCustomerName());
			//new params
			params.add(transactionInfo.getCustomerInfo().getCustomerFirstName());
			params.add(transactionInfo.getCustomerInfo().getCustomerLastName());
			
			params.add(transactionInfo.getCustomerInfo().getCustomerPhone());
			params.add(transactionInfo.getCustomerInfo().getCustomerEmail());
			params.add(transactionInfo.getCustomerInfo().getCustomerAddressNumber());
			params.add(transactionInfo.getCustomerInfo().getCustomerAddress());
			params.add(transactionInfo.getCustomerInfo().getCustomerDistrict());
			params.add(transactionInfo.getCustomerInfo().getCustomerZip());
			params.add(transactionInfo.getCustomerInfo().getCustomerCity());
			params.add(transactionInfo.getCustomerInfo().getCustomerCountry());
			params.add(transactionInfo.getCustomerInfo().getLanguage());
			params.add(transactionInfo.getCustomerInfo().getCustomerIP());
			params.add(transactionInfo.getCustomerInfo().getCustomerCellPhone());
			
			if (transactionInfo.getCustomerInfo().getExtInfo() != null) {
				if (transactionInfo.getCustomerInfo().getExtInfo().size() > 0) {
					for(int i=0; i < transactionInfo.getCustomerInfo().getExtInfo().size(); i++) {
						params.add(transactionInfo.getCustomerInfo().getExtInfo().get(i).getKey());
						params.add(transactionInfo.getCustomerInfo().getExtInfo().get(i).getValue());
					}
				} else {
					params.add(null);
				}
			} else {
				params.add(transactionInfo.getCustomerInfo().getExtInfo());
			}
		} else {
			params.add(transactionInfo.getCustomerInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> ShippingInfo
		//parameters
		if (transactionInfo.getShippingInfo() != null) {
			params.add(transactionInfo.getShippingInfo().getShippingCity());
			params.add(transactionInfo.getShippingInfo().getShippingCountry());
			params.add(transactionInfo.getShippingInfo().getShippingDeliveryCompanyName());
			params.add(transactionInfo.getShippingInfo().getShippingName());
			params.add(transactionInfo.getShippingInfo().getShippingPhone());
			params.add(transactionInfo.getShippingInfo().getShippingSpeed());
			params.add(transactionInfo.getShippingInfo().getShippingState());
			params.add(transactionInfo.getShippingInfo().getShippingStatus());
			params.add(transactionInfo.getShippingInfo().getShippingStreetNumber());
			params.add(transactionInfo.getShippingInfo().getShippingStreet());
			params.add(transactionInfo.getShippingInfo().getShippingStreet2());
			params.add(transactionInfo.getShippingInfo().getShippingDistrict());
			params.add(transactionInfo.getShippingInfo().getShippingType());
			params.add(transactionInfo.getShippingInfo().getShippingZipCode());
		} else {
			params.add(transactionInfo.getShippingInfo());
		}
		
		//Starting createWithThreeDSResponse -> TransactionInfo -> ExtraInfo
		//parameters
		if (transactionInfo.getExtraInfo() != null) {
			params.add(transactionInfo.getExtraInfo().getCtxMode());
		} else {
			params.add(transactionInfo.getExtraInfo());
		}
		
		params.add(transactionInfo.getTransactionStatusLabel());
		params.add(payzenCertificate);
		
		String createInfoData = createPlusSeparatedString(params);
		//logger.info("Signature Data for Finalyze Response is: "+createInfoData);
		try {
			encrytData = new PayZenPGDataEncryptor();
			wsSignature = encrytData.encryptString(createInfoData);
		} catch (Exception e) {
			logger.error("Exception while Creating Signature for createInfoData: ",e);
		}
		return wsSignature;
	}
	
	/**
	 * 
	 * @param siteId
	 * @param transactionId
	 * @param sequenceNumber
	 * @param ctxMode
	 * @param payzenCertificate
	 * @return
	 */
	public static String createSignatureDataForPayZenEnquiryCall(String siteId, String transactionId,
			int sequenceNumber, String ctxMode, String payzenCertificate) {
		
		PayZenPGDataEncryptor encrytData = null;
		String wsSignature = null;
		
		List<Object> params = new ArrayList<Object>();
		params.add(siteId);
		params.add(transactionId);
		params.add(sequenceNumber);
		params.add(ctxMode);
		params.add(payzenCertificate);
		
		String createInfoData = createPlusSeparatedString(params);
		try{
			encrytData = new PayZenPGDataEncryptor();
			wsSignature = encrytData.encryptString(createInfoData);
		} catch (Exception e) {
			logger.error("Exception while Creating Signature for createInfoData: ",e);
		}
		return wsSignature;
	}
	
	/**
	 * 
	 * @param siteId
	 * @param transactionId
	 * @param sequenceNumber
	 * @param ctxMode
	 * @param newTransactionId
	 * @param amount
	 * @param currency
	 * @param validationMode
	 * @param comment
	 * @param payzenCertificate
	 * @return
	 */
	public static String createSignatureDataForPayZenPGRefundCall(String siteId, String transactionId,
			int sequenceNumber, String ctxMode, String newTransactionId, Long amount, 
			int currency, int validationMode, String comment, String payzenCertificate) {
		
		PayZenPGDataEncryptor encrytData = null;
		String wsSignature = null;
		
		List<Object> params = new ArrayList<Object>();
		
		params.add(siteId);
		params.add(transactionId);
		params.add(sequenceNumber);
		params.add(ctxMode);
		params.add(newTransactionId);
		params.add(amount);
		params.add(currency);
		params.add(validationMode);
		params.add(comment);
		params.add(payzenCertificate);
		
		String createInfoData = createPlusSeparatedString(params);
	
		try {

			encrytData = new PayZenPGDataEncryptor();
			wsSignature = encrytData.encryptString(createInfoData);
		} catch (Exception e) {
			logger.error("Exception while Creating Signature for createInfoData: ",e);
		}
		return wsSignature;
	}
	
	/**
	 * 
	 * @param siteId
	 * @param transactionId
	 * @param sequenceNumber
	 * @param ctxMode
	 * @param comment
	 * @param payzenCertificate
	 * @return
	 */
	public static String createSignatureCancelCall(String siteId, String transactionId,
			int sequenceNumber, String ctxMode, String comment, String payzenCertificate ){
		PayZenPGDataEncryptor encrytData = null;
		String wsSignature = null;
		List<Object> params = new ArrayList<Object>();
		
		params.add(siteId);
		params.add(transactionId);
		params.add(sequenceNumber);
		params.add(ctxMode);
		params.add(comment);
		params.add(payzenCertificate);
		
		String createInfoData = createPlusSeparatedString(params);
		try {
			encrytData = new PayZenPGDataEncryptor();
			wsSignature = encrytData.encryptString(createInfoData);
		} catch (Exception e) {
			logger.error("Exception while Creating Signature for createInfoData: ", e);
		}
		return wsSignature;
	}
}
