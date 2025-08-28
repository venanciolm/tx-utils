package com.farmafene.commons.tx;

import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;

public class XAHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(XAHelper.class);

	public static String getStringFromFlag(int flags) {
		String salida = null;
		switch (flags) {
		case XAResource.TMENDRSCAN:
			salida = "TMENDRSCAN";
			break;
		case XAResource.TMFAIL:
			salida = "TMFAIL";
			break;
		case XAResource.TMJOIN:
			salida = "TMJOIN";
			break;
		case XAResource.TMNOFLAGS:
			salida = "TMNOFLAGS";
			break;
		case XAResource.TMONEPHASE:
			salida = "TMONEPHASE";
			break;
		case XAResource.TMRESUME:
			salida = "TMRESUME";
			break;
		case XAResource.TMSTARTRSCAN:
			salida = "TMSTARTRSCAN";
			break;
		case XAResource.TMSUCCESS:
			salida = "TMSUCCESS";
			break;
		case XAResource.TMSUSPEND:
			salida = "TMSUSPEND";
			break;
		default:
			salida = "NO RECONOCIDO";
		}
		return salida;
	}

	public static String getStringFromStatus(int status) {
		String out = "";
		switch (status) {
		case Status.STATUS_ACTIVE:
			out = "STATUS_ACTIVE";
			break;
		case Status.STATUS_MARKED_ROLLBACK:
			out = "STATUS_MARKED_ROLLBACK";
			break;
		case Status.STATUS_PREPARED:
			out = "STATUS_PREPARED";
			break;
		case Status.STATUS_COMMITTED:
			out = "STATUS_COMMITTED";
			break;
		case Status.STATUS_ROLLEDBACK:
			out = "STATUS_ROLLEDBACK";
			break;
		case Status.STATUS_NO_TRANSACTION:
			out = "STATUS_NO_TRANSACTION";
			break;
		case Status.STATUS_PREPARING:
			out = "STATUS_PREPARING";
			break;
		case Status.STATUS_COMMITTING:
			out = "STATUS_COMMITTING";
			break;
		case Status.STATUS_ROLLING_BACK:
			out = "STATUS_ROLLING_BACK";
			break;
		case Status.STATUS_UNKNOWN:
		default:
			out = "STATUS_UNKNOWN";
		}
		return out;
	}

	public static String getStringFromStatus(Transaction tx) {
		String out = "<null>";
		if (null != tx) {
			try {
				return getStringFromStatus(tx.getStatus());
			} catch (SystemException e) {
				LOGGER.info("Error en la evaluación del estado.", e);
			}
		}
		return out;
	}
}
