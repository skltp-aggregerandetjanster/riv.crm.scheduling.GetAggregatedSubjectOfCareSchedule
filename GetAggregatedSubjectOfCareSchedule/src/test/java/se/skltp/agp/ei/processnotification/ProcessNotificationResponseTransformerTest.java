/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.agp.ei.processnotification;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.soitoolkit.commons.mule.util.MiscUtil;

public class ProcessNotificationResponseTransformerTest {

	@Test
	public void testTransformer_ok() throws Exception {

		// Specify input and expected result 

		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/response-expected-result.xml");
		
		// Create the transformer under test and let it perform the transformation

		ProcessNotificationResponseTransformer transformer = new ProcessNotificationResponseTransformer();
		String result = (String)transformer.pojoTransform(null, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}


	@Test
	public void testTransformer_fault() throws Exception {

		// Specify input and expected result 
		// FIX ME: For the moment we don't have ny negative test, go with the positive results...
		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/response-expected-result.xml");
//		String expectedResult = MiscUtil.readFileAsString("src/test/resources/testfiles/process-notification/fault-response-expected-result.xml");
		
		// Create the transformer under test and let it perform the transformation

		ProcessNotificationResponseTransformer transformer = new ProcessNotificationResponseTransformer();
		String result = (String)transformer.pojoTransform(null, "UTF-8");


		// Compare the result to the expected value
		assertEquals(expectedResult, result);
	}

}