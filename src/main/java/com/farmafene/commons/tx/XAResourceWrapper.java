package com.farmafene.commons.tx;

import java.util.Arrays;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XAResourceWrapper implements XAResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(XAResourceWrapper.class);

	private XAResource xaResource;

	public XAResourceWrapper(XAResource xaResource) {
		this.xaResource = xaResource;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#commit(javax.transaction.xa.Xid,
	 *      boolean)
	 */
	@Override
	public void commit(Xid xid, boolean onePhase) throws XAException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("commit(onePhase: {}, Xid: {}), wrapped: {}", onePhase, xid,
					xaResource.getClass().getCanonicalName());
		}
		xaResource.commit(xid, onePhase);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#end(javax.transaction.xa.Xid, int)
	 */
	@Override
	public void end(Xid xid, int flags) throws XAException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("end(flags: {}, Xid: {}), wrapped: {}", XAHelper.getStringFromFlag(flags), xid,
					xaResource.getClass().getCanonicalName());
		}
		xaResource.end(xid, flags);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#forget(javax.transaction.xa.Xid)
	 */
	@Override
	public void forget(Xid xid) throws XAException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("forget(Xid: {}), wrapped: {}", xid, xaResource.getClass().getCanonicalName());
		}
		xaResource.forget(xid);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#getTransactionTimeout()
	 */
	@Override
	public int getTransactionTimeout() throws XAException {
		int out = xaResource.getTransactionTimeout();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getTransactionTimeout():={}, wrapped: {}", out, xaResource.getClass().getCanonicalName());
		}
		return out;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#isSameRM(javax.transaction.xa.XAResource)
	 */
	@Override
	public boolean isSameRM(XAResource xares) throws XAException {
		boolean out = xaResource.isSameRM(xares);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("isSameRM(XAResource: {}):={}, wrapped: {}", xares, out,
					xaResource.getClass().getCanonicalName());
		}
		return out;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#prepare(javax.transaction.xa.Xid)
	 */
	@Override
	public int prepare(Xid xid) throws XAException {
		int out = xaResource.prepare(xid);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("prepare(Xid: {}):={}, wrapped: {}", xid, out, xaResource.getClass().getCanonicalName());
		}
		return out;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#recover(int)
	 */
	@Override
	public Xid[] recover(int flag) throws XAException {
		Xid[] out = xaResource.recover(flag);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("recover(flag: {}):={}, wrapped: {}", XAHelper.getStringFromFlag(flag), Arrays.toString(out),
					xaResource.getClass().getCanonicalName());
		}
		return out;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#rollback(javax.transaction.xa.Xid)
	 */
	@Override
	public void rollback(Xid xid) throws XAException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("rollback(Xid: {}), wrapped: {}", xid, xaResource.getClass().getCanonicalName());
		}
		xaResource.rollback(xid);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#setTransactionTimeout(int)
	 */
	@Override
	public boolean setTransactionTimeout(int seconds) throws XAException {
		boolean out = xaResource.setTransactionTimeout(seconds);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("setTransactionTimeout(seconds: {}):={}, wrapped: {}", seconds, out,
					xaResource.getClass().getCanonicalName());
		}
		return out;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.transaction.xa.XAResource#start(javax.transaction.xa.Xid, int)
	 */
	@Override
	public void start(Xid xid, int flags) throws XAException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("start(flags: {}), wrapped: {}", XAHelper.getStringFromFlag(flags),
					xaResource.getClass().getCanonicalName());
		}
		xaResource.start(xid, flags);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return xaResource.hashCode();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return xaResource.equals(obj);
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append("[");
		sb.append("wrapped=").append(xaResource.getClass().getCanonicalName());
		sb.append(", ").append(xaResource);
		sb.append("}");
		return sb.toString();
	}
}
