/*
 * @Author Bruce Martin
 * Created on 5/09/2005 for RecordEditor Version 0.55
 *
 * Purpose:
 *   Float / Double field Type
 *
 * Changes
 * # Version 0.56 Bruce Martin 2007/01/16
 *   - remove unused field val
 *
 * # Version 0.60 Bruce Martin 2007/02/16
 *   - Starting to seperate the Record package out from the RecordEditor
 *     so that it can be used seperately. So classes have been moved
 *     to the record package (ie RecordException + new Constant interface
 */
/*  -------------------------------------------------------------------------
 *
 *            Sub-Project: JRecord Common
 *    
 *    Sub-Project purpose: Common Low-Level Code shared between 
 *                        the JRecord and Record Projects
 *    
 *                 Author: Bruce Martin
 *    
 *                License: LGPL 2.1 or latter
 *                
 *    Copyright (c) 2016, Bruce Martin, All Rights Reserved.
 *   
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *   
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 * ------------------------------------------------------------------------ */
      
package net.sf.JRecord.Types;

import net.sf.JRecord.Common.Conversion;
import net.sf.JRecord.Common.IFieldDetail;

import net.sf.converters.CvtConversions;

/**
 * Float / Double field Type
 *
 * <p>This class is the interface between the raw data in the file
 * and what is to be displayed on the screen for Float or Double fields
 *
 * <p>By default the raw data is interpreted as little-endian IEEE-754
 * floating point. When {@link #setIbmHfpFloatingPoint(boolean) IBM HFP mode}
 * is enabled, the raw data is instead interpreted as big-endian IBM
 * System/360 Hexadecimal Floating Point (HFP) and converted to/from IEEE
 * using {@link CvtConversions}.
 *
 * @author Bruce Martin
 *
 * @version 0.55
 */
public class TypeFloat extends TypeNum {

	private static final int  LENGTH_OF_DOUBLE  =  8;
	private static final int  LENGTH_OF_FLOAT   =  4;

	/**
	 * Whether float/double fields are stored as IBM System/360 Hexadecimal
	 * Floating Point (HFP) rather than IEEE-754. This is a global interpretation
	 * choice for a conversion run and is configured through the
	 * {@code ICobolJsonConversion} interface.
	 */
	private static volatile boolean ibmHfpFloatingPoint = false;

	/**
	 * Enable or disable interpreting float/double fields as IBM System/360
	 * Hexadecimal Floating Point (HFP) instead of IEEE-754.
	 *
	 * @param useIbmHfp whether to use IBM HFP floating point
	 */
	public static void setIbmHfpFloatingPoint(boolean useIbmHfp) {
		ibmHfpFloatingPoint = useIbmHfp;
	}

	/**
	 * @return whether float/double fields are interpreted as IBM System/360
	 * Hexadecimal Floating Point (HFP)
	 */
	public static boolean isIbmHfpFloatingPoint() {
		return ibmHfpFloatingPoint;
	}


    /**
     *  Float / Double field Type
     *
     * <p>This class is the interface between the raw data in the file
     * and what is to be displayed on the screen for Float or Double fields
     */
    public TypeFloat() {
        super(true, false, false, false, true, true, false);
    }

    /**
     * @see net.sf.JRecord.Types.Type#getField(byte[], int, IFieldDetail)
     */
    public Object getField(byte[] record,
            final int position,
			final IFieldDetail field) {
         Object val = "";

         int fldLength = field.getLen();
         int pos = position - 1;

         if (ibmHfpFloatingPoint) {
             return "" + getIbmHfpField(record, pos, fldLength);
         }

         if (fldLength == LENGTH_OF_FLOAT) {
        	 val = Float.toString(Float.intBitsToFloat(Conversion.getLittleEndianBigInt(record, pos, pos + fldLength).intValue()));
//             val = Float.toString(
//                     Float.intBitsToFloat(
//                        Conversion.getBigInt(record, pos, fldLength).intValue()));
         } else if (fldLength == LENGTH_OF_DOUBLE) {
        	 val = Double.toString(Double.longBitsToDouble(Conversion.getLittleEndianBigInt(record, pos, pos + fldLength).longValue()));
//             val = Double.toString(
//                 Double.longBitsToDouble(
//                     Conversion.getBigInt(record, pos, fldLength).longValue()));
         }

         return "" + val;
    }


    /**
     * @see net.sf.JRecord.Types.Type#setField(byte[], int, IFieldDetail, Object)
     */
    public byte[] setField(byte[] record,
            final int position,
			final IFieldDetail field,
			Object value) {

        int len = field.getLen();
        int pos = position - 1;
        double doubleVal  = getBigDecimal(field, toNumberString(value)).doubleValue();

        if (ibmHfpFloatingPoint) {
            setIbmHfpField(record, pos, len, doubleVal);
            return record;
        }

	    if (len == LENGTH_OF_FLOAT) {
	        long l = Float.floatToRawIntBits((float) doubleVal);
	        Conversion.setLongLow2High(record, pos, len, l, true);
	    } else if (len == LENGTH_OF_DOUBLE) {
	        long l = Double.doubleToRawLongBits(doubleVal);
	        Conversion.setLongLow2High(record, pos, len, l, true);
	    }

	    return record;
    }

    /**
     * Read an IBM System/360 HFP field (big-endian) from the record and return
     * its value as a String, converting via {@link CvtConversions}.
     */
    private String getIbmHfpField(byte[] record, int pos, int fldLength) {
        if (fldLength == LENGTH_OF_FLOAT) {
            byte[] ibm = new byte[LENGTH_OF_FLOAT];
            System.arraycopy(record, pos, ibm, 0, LENGTH_OF_FLOAT);
            byte[] ieee = Conversion.convertHFPBytesToIEEE(true, ibm);
            return Float.toString(Float.intBitsToFloat((int) Conversion.toBigEndian(ieee, LENGTH_OF_FLOAT)));
        } else if (fldLength == LENGTH_OF_DOUBLE) {
            byte[] ibm = new byte[LENGTH_OF_DOUBLE];
            System.arraycopy(record, pos, ibm, 0, LENGTH_OF_DOUBLE);
            byte[] ieee = Conversion.convertHFPBytesToIEEE(false, ibm);
            return Double.toString(Double.longBitsToDouble(Conversion.toBigEndian(ieee, LENGTH_OF_DOUBLE)));
        }
        return "";
    }

    /**
     * Convert a double value to an IBM System/360 HFP field (big-endian) and
     * write it into the record, converting via {@link CvtConversions}.
     */
    private void setIbmHfpField(byte[] record, int pos, int len, double doubleVal) {
        if (len == LENGTH_OF_FLOAT) {
            byte[] ieee = Conversion.fromBigEndian(Float.floatToRawIntBits((float) doubleVal) & 0xFFFFFFFFL, LENGTH_OF_FLOAT);
            byte[] ibm = Conversion.convertIEEEBytesToHFP(true, ieee);
            System.arraycopy(ibm, 0, record, pos, LENGTH_OF_FLOAT);
        } else if (len == LENGTH_OF_DOUBLE) {
            byte[] ieee = Conversion.fromBigEndian(Double.doubleToRawLongBits(doubleVal), LENGTH_OF_DOUBLE);
            byte[] ibm = Conversion.convertIEEEBytesToHFP(false, ieee);
            System.arraycopy(ibm, 0, record, pos, LENGTH_OF_DOUBLE);
        }
    }


}
