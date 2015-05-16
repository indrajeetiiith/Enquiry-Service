package com.citruspay;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * BaseDomain that will be extended by all other Entity classes.
 * Provides basis object utilities.
 * @author Badal
 */
@SuppressWarnings("serial")
public class BaseDomain implements Serializable, Cloneable, Comparable<Object> {

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	/**
	 * Check if object passed is equals to the current object
	 * 
	 * @param o
	 *            object to check the equility to
	 * @return true if objects are equal else return false
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	/**
	 * Compares the current object with the object passed
	 * 
	 * @param o
	 *            object to compare with
	 * @return int
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return CompareToBuilder.reflectionCompare(this, o);
	}

	/**
	 * Override hash code
	 * @param o
	 * @return
	 */
	public int hashCode(Object o) {
		return HashCodeBuilder.reflectionHashCode(o);
	}
}
