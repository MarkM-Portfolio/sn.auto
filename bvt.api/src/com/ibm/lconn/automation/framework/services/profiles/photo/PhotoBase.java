/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.photo;

import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.Assert;
import com.ibm.lconn.automation.framework.services.profiles.base.AbstractTest;
import com.ibm.lconn.automation.framework.services.profiles.util.IoUtils;
import com.ibm.lconn.automation.framework.services.profiles.util.Sha256Encoder;
import com.ibm.lconn.automation.framework.services.profiles.util.Transport;

public abstract class PhotoBase extends AbstractTest {

	// photo is scaled to 155 X 155 see default value in PropertiesConfig.java [RTC 118290]
	protected final int PHOTO_DEFAULT_HEIGHT = 155;
	protected final int PHOTO_DEFAULT_WIDTH = 155; // photo is scaled to 155 X 155

	protected InputStream getResourceAsStream(Class<?> clazz, String name) {
		InputStream is = clazz.getResourceAsStream(name);
		return is;

	}

	public static void validateImageDimensions(InputStream is, int height, int width) throws Exception {
		ImageReader ir = null;
		ImageInputStream iis = null;

		try {
			iis = ImageIO.createImageInputStream(is);

			Iterator<?> imageReaderIterator = ImageIO.getImageReaders(iis);
			ir = (ImageReader) imageReaderIterator.next();

			ir.setInput(iis);

			Assert.assertEquals(ir.getHeight(0), height);
			Assert.assertEquals(ir.getWidth(0), width);
		}
		finally {
			ir = null;
			iis.close();
			IoUtils.closeQuietly(is);
		}
	}

	public static void validateImageDimensions(String imageUrl, Transport transport, int height, int width) throws Exception {
		ClientResponse response = null;
		InputStream is = null;

		try {
			response = transport.doResponseGet(imageUrl, NO_HEADERS);
			is = response.getInputStream();
			validateImageDimensions(is, height, width);
		}
		finally {
			IoUtils.closeQuietly(is);
			response.release();
		}
	}

	protected String getMcodePhotoUrl(String url, String email) {
		Assert.assertNotNull(url);
		Assert.assertNotNull(email);
		// rework the image url to use mcode
		int endIndex = url.indexOf("?");
		String newUrl = url.substring(0, endIndex);
		String mcode = Sha256Encoder.hashLowercaseStringUTF8(email, true);
		newUrl += "?mcode=" + mcode;
		return newUrl;
	}
}
