package com.citruspay.enquiry.gateway;

import java.util.HashMap;
import java.util.Map;

public class PayZenPGConstants {
	//PayZen Currency Codes
	public static final String PAYZENPG_CURRENCY_CODE_INR = "356";
	
	public static final String PAYZENPG_VALIDATION_MODE_AUTOMATIC = "0";
	public static final String PAYZENPG_VALIDATION_MODE_MANUAL = "1";
	
	public static final String PAYZENPG_TRANSACTIONIFO_ERROR_CODE = "0";
	public static final String PAYZENPG_TRANSACTIONIFO_TXN_STATUS = "4";
	
	public static final String PAYZENPG_TXN_STATUS_REFUSED = "REFUSED";
	
	public static String transactionInfoErrorCodeDesc(int num) {
		
		//PayZen Error Codes for TransactionInfo
		Map<Integer, String> mapIntStrForTxnInfo = new HashMap<Integer, String>();
		
		mapIntStrForTxnInfo.put(0, "Action successfully completed");
		mapIntStrForTxnInfo.put(1, "Unauthorized request");
		mapIntStrForTxnInfo.put(2, "TransactionID was not found");
		mapIntStrForTxnInfo.put(3, "Bad transaction status");
		mapIntStrForTxnInfo.put(4, "Transaction already exists");
		mapIntStrForTxnInfo.put(5, "Incorrect signature computation");
		mapIntStrForTxnInfo.put(6, "TransmissionDate is too far from current UTC date");
		mapIntStrForTxnInfo.put(7, "");
		mapIntStrForTxnInfo.put(8, "");
		mapIntStrForTxnInfo.put(9, "");
		mapIntStrForTxnInfo.put(10, "Invalid input field ‘amount’");
		mapIntStrForTxnInfo.put(11, "Invalid input field ‘currency’");
		mapIntStrForTxnInfo.put(12, "Unknown card type");
		mapIntStrForTxnInfo.put(13, "Invalid input field ‘expiryDate’");
		mapIntStrForTxnInfo.put(14, "Invalid input field ‘cvv’");
		mapIntStrForTxnInfo.put(15, "Unknown contract number");
		mapIntStrForTxnInfo.put(16, "Invalid input field ‘cardNumber’");
		mapIntStrForTxnInfo.put(17, "CardIdent not found");
		mapIntStrForTxnInfo.put(18, "Invalid cardIdent (cancelled, …)");
		mapIntStrForTxnInfo.put(19, "SubscriptionID was not found");
		mapIntStrForTxnInfo.put(20, "Invalid Subscription");
		mapIntStrForTxnInfo.put(21, "CardIdent already exists");
		mapIntStrForTxnInfo.put(22, "cardIdent creation declined");
		mapIntStrForTxnInfo.put(23, "cardIdent purged");
		mapIntStrForTxnInfo.put(24, "");
		mapIntStrForTxnInfo.put(25, "Nothing has changed");
		mapIntStrForTxnInfo.put(26, "Amount not authorized");
		mapIntStrForTxnInfo.put(27, "");
		mapIntStrForTxnInfo.put(28, "");
		mapIntStrForTxnInfo.put(29, "");
		mapIntStrForTxnInfo.put(30, "");
		mapIntStrForTxnInfo.put(31, "");
		mapIntStrForTxnInfo.put(32, "");
		mapIntStrForTxnInfo.put(33, "");
		mapIntStrForTxnInfo.put(34, "");
		mapIntStrForTxnInfo.put(35, "");
		mapIntStrForTxnInfo.put(36, "");
		mapIntStrForTxnInfo.put(37, "");
		mapIntStrForTxnInfo.put(38, "");
		mapIntStrForTxnInfo.put(39, "Card range not found");
		mapIntStrForTxnInfo.put(40, "");
		mapIntStrForTxnInfo.put(41, "");
		mapIntStrForTxnInfo.put(42, "");
		mapIntStrForTxnInfo.put(43, "");
		mapIntStrForTxnInfo.put(44, "");
		mapIntStrForTxnInfo.put(45, "");
		mapIntStrForTxnInfo.put(46, "");
		mapIntStrForTxnInfo.put(47, "");
		mapIntStrForTxnInfo.put(48, "");
		mapIntStrForTxnInfo.put(49, "");
		mapIntStrForTxnInfo.put(50, "Invalid input field ‘siteId’ invalide");
		mapIntStrForTxnInfo.put(51, "Invalid input field ‘transmissionDate’");
		mapIntStrForTxnInfo.put(52, "Invalid input field ‘transactionId’");
		mapIntStrForTxnInfo.put(53, "Invalid input field ‘ctxMode’");
		mapIntStrForTxnInfo.put(54, "Invalid input field ‘comment’");
		mapIntStrForTxnInfo.put(55, "Invalid input field ‘AutoNb’");
		mapIntStrForTxnInfo.put(56, "Invalid input field ‘AutoDate’");
		mapIntStrForTxnInfo.put(57, "Invalid input field ‘captureDate’");
		mapIntStrForTxnInfo.put(58, "Invalid input field ‘newTransactionId’");
		mapIntStrForTxnInfo.put(59, "Invalid input field ‘validationMode’");
		mapIntStrForTxnInfo.put(60, "Invalid input field ‘orderId’");
		mapIntStrForTxnInfo.put(61, "Invalid input field ‘orderInfo1’");
		mapIntStrForTxnInfo.put(62, "Invalid input field ‘orderInfo2’");
		mapIntStrForTxnInfo.put(63, "Invalid input field ‘orderInfo3’");
		mapIntStrForTxnInfo.put(64, "Invalid input field ‘paymentSource’");
		mapIntStrForTxnInfo.put(65, "Invalid input field ‘cardNetwork’");
		mapIntStrForTxnInfo.put(66, "Invalid input field ‘contractNumber’");
		mapIntStrForTxnInfo.put(67, "Invalid input field ‘customerId’");
		mapIntStrForTxnInfo.put(68, "Invalid input field ‘customerTitle’");
		mapIntStrForTxnInfo.put(69, "Invalid input field ‘customerName’");
		mapIntStrForTxnInfo.put(70, "Invalid input field ‘customerPhone’");
		mapIntStrForTxnInfo.put(71, "Invalid input field ‘customerMail’");
		mapIntStrForTxnInfo.put(72, "Invalid input field ‘customerAddress’");
		mapIntStrForTxnInfo.put(73, "Invalid input field ‘customerZipCode’");
		mapIntStrForTxnInfo.put(74, "Invalid input field ‘customerCity’");
		mapIntStrForTxnInfo.put(75, "Invalid input field ‘customerCountry’");
		mapIntStrForTxnInfo.put(76, "Invalid input field ‘customerLanguage’");
		mapIntStrForTxnInfo.put(77, "Invalid input field ‘customerIp’");
		mapIntStrForTxnInfo.put(78, "Invalid input field ‘customerSendMail’");
		mapIntStrForTxnInfo.put(79, "Invalid input field ‘customerMobilePhone’");
		mapIntStrForTxnInfo.put(80, "Invalid input field ‘subPaiementType’");
		mapIntStrForTxnInfo.put(81, "Invalid input field ‘subReference’");
		mapIntStrForTxnInfo.put(82, "Invalid input field ‘initialAmount’");
		mapIntStrForTxnInfo.put(83, "Invalid input field ‘occInitialAMount’");
		mapIntStrForTxnInfo.put(84, "Invalid input field ‘effectDate’");
		mapIntStrForTxnInfo.put(85, "Invalid input field ‘state’");
		mapIntStrForTxnInfo.put(86, "Invalid input field ‘customerAddressNumber’");
		mapIntStrForTxnInfo.put(87, "Invalid input field ‘customerDistrict");
		mapIntStrForTxnInfo.put(88, "Invalid input field ‘customerState");
		mapIntStrForTxnInfo.put(89, "");
		mapIntStrForTxnInfo.put(90, "Invalid input field ‘enrolled’");
		mapIntStrForTxnInfo.put(91, "Invalid input field ‘authStatus’");
		mapIntStrForTxnInfo.put(92, "Invalid input field ‘eci’");
		mapIntStrForTxnInfo.put(93, "Invalid input field ‘xid’");
		mapIntStrForTxnInfo.put(94, "Invalid input field ‘cavv’");
		mapIntStrForTxnInfo.put(95, "Invalid input field ‘cavvAlgo’");
		mapIntStrForTxnInfo.put(96, "Invalid input field ‘brand’");
		mapIntStrForTxnInfo.put(97, "");
		mapIntStrForTxnInfo.put(98, "Invalid input field ‘requestId’");
		mapIntStrForTxnInfo.put(99, "Unknown error");
		
		return mapIntStrForTxnInfo.get(num);
	}
	
	public static String veResPAReqInfoErrorCodeDesc(int num) {
		
		Map<Integer, String> mapIntStrForVeResPAReqInfo = new HashMap<Integer, String>();
		
		//PayZen Error Codes for veResPAReqInfo
		mapIntStrForVeResPAReqInfo.put(0, "Action successfully completed");
		mapIntStrForVeResPAReqInfo.put(1, "Unauthorized request");
		mapIntStrForVeResPAReqInfo.put(2, "Incorrect signature computation");
		mapIntStrForVeResPAReqInfo.put(3, "Brand not found");
		mapIntStrForVeResPAReqInfo.put(4, "Invalid card number");
		mapIntStrForVeResPAReqInfo.put(5, "No suitable contract");
		mapIntStrForVeResPAReqInfo.put(6, "Ambiguous contract");
		mapIntStrForVeResPAReqInfo.put(7, "Merchant not enrolled");
		mapIntStrForVeResPAReqInfo.put(8, "Invalid ACS Signature");
		mapIntStrForVeResPAReqInfo.put(9, "Technical error");
		mapIntStrForVeResPAReqInfo.put(10, "Wrong Parameter");
		mapIntStrForVeResPAReqInfo.put(11, "Incorrect date format");
		mapIntStrForVeResPAReqInfo.put(12, "3DS Disabled");
		mapIntStrForVeResPAReqInfo.put(13, "cardIdent not found");
		mapIntStrForVeResPAReqInfo.put(14, "PAN not found");
		mapIntStrForVeResPAReqInfo.put(99, "Unknown error");

		return mapIntStrForVeResPAReqInfo.get(num);
	}
	
	public static String authMarkResultErrorCodeDesc(int num) {
		
		Map<Integer, String> mapIntStrForAuthMarkResult = new HashMap<Integer, String>();
		
		mapIntStrForAuthMarkResult.put(00, "Approved");
		mapIntStrForAuthMarkResult.put(02, "Refer to card issuer, special condition");
		mapIntStrForAuthMarkResult.put(03, "Invalid Merchant");
		mapIntStrForAuthMarkResult.put(04, "Pick up card");
		mapIntStrForAuthMarkResult.put(05, "Do not Honour");
		mapIntStrForAuthMarkResult.put(07, "Pick-Up card, Special condition");
		mapIntStrForAuthMarkResult.put(8, "Honour with Identification");
		mapIntStrForAuthMarkResult.put(12, "Invalid transaction");
		mapIntStrForAuthMarkResult.put(13, "Invalid amount");
		mapIntStrForAuthMarkResult.put(14, "Invalid card number");
		mapIntStrForAuthMarkResult.put(15, "No such issuer");
		mapIntStrForAuthMarkResult.put(17, "Operator cancelled");
		mapIntStrForAuthMarkResult.put(19, "Re enter transaction");
		mapIntStrForAuthMarkResult.put(20, "Invalid response");
		mapIntStrForAuthMarkResult.put(24, "File update not supported");
		mapIntStrForAuthMarkResult.put(25, "Unable to locate record");
		mapIntStrForAuthMarkResult.put(26, "Duplicate record");
		mapIntStrForAuthMarkResult.put(27, "File update edit error");
		mapIntStrForAuthMarkResult.put(28, "File update file locked");
		mapIntStrForAuthMarkResult.put(29, "File update not successful");
		mapIntStrForAuthMarkResult.put(30, "Format error");
		mapIntStrForAuthMarkResult.put(31, "Bank not supported");
		mapIntStrForAuthMarkResult.put(33, "Expired card, pick-up");
		mapIntStrForAuthMarkResult.put(34, "Suspected fraud, pick-up");
		mapIntStrForAuthMarkResult.put(38, "Pin tries exceeded, pick-up");
		mapIntStrForAuthMarkResult.put(41, "Lost card");
		mapIntStrForAuthMarkResult.put(43, "Stolen card");
		mapIntStrForAuthMarkResult.put(51, "Not sufficient funds (client to contact bank)");
		mapIntStrForAuthMarkResult.put(54, "Expired card");
		mapIntStrForAuthMarkResult.put(55, "Incorrect pin");
		mapIntStrForAuthMarkResult.put(56, "No card record");
		mapIntStrForAuthMarkResult.put(57, "Transaction not permitted to cardholder");
		mapIntStrForAuthMarkResult.put(58, "Transaction not permitted on terminal");
		mapIntStrForAuthMarkResult.put(59, "Suspected fraud");
		mapIntStrForAuthMarkResult.put(60, "Contact Acquirer");
		mapIntStrForAuthMarkResult.put(61, "Exceeds withdrawal limit");
		mapIntStrForAuthMarkResult.put(63, "Security violation");
		mapIntStrForAuthMarkResult.put(68, "Response received too late");
		mapIntStrForAuthMarkResult.put(75, "PIN Tries exceeded");
		mapIntStrForAuthMarkResult.put(90, "Cardholder already in opposition");
		mapIntStrForAuthMarkResult.put(91, "Cut-off in progress");
		mapIntStrForAuthMarkResult.put(93, "Issuer or switch inoperative");
		mapIntStrForAuthMarkResult.put(94, "Duplicate transaction");
		mapIntStrForAuthMarkResult.put(96, "Communication system malfunction");
		mapIntStrForAuthMarkResult.put(97, "Communication error – Cannot connect to FNB");
		mapIntStrForAuthMarkResult.put(98, "Server unavailable, route request sent again");
		mapIntStrForAuthMarkResult.put(99, "Technical error");

		return mapIntStrForAuthMarkResult.get(num);
	}
}