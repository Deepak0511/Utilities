/**
 * NOTES:
 * 
 * Dependencies:
 * vtd-xml_2_13.jar
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ximpleware.AutoPilot;
import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.IndexWriteException;
import com.ximpleware.ModifyException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.TranscodeException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;

/**
 * @author Deepak0511
 *<P>
 *Utility Class for VTD XML parser
 */
public class VTDXmlUtils {
	
	
	/**
	 * How many times did the given element occur?
	 * @param vn
	 * @param elementName
	 * @return Integer Count
	 * @throws NavException
	 * @throws IndexWriteException
	 * @throws IOException
	 */
	public static int getElementFrequencyOf(String elementName,VTDNav vn)
			throws NavException, IndexWriteException, IOException {

		List<String> elements = new ArrayList<String>();

		for (boolean element = (vn != null); element == true; element = vn.toElement(VTDNav.NEXT_SIBLING)) {

			addSingleChild(elements, vn);

			parseAndAddChildren(elements, vn);

		}

		return Collections.frequency(elements, elementName);

	}

	private static void parseAndAddChildren(List<String> elementList, VTDNav vn) throws NavException {

		vn.push();

		for (boolean element = vn.toElement(VTDNav.FIRST_CHILD); element == true; element = vn.toElement(VTDNav.NEXT_SIBLING)) {

			addSingleChild(elementList, vn);

			parseAndAddChildren(elementList, vn);
		}

		vn.pop();

	}

	private static void addSingleChild(List<String> elementList, VTDNav vn) throws NavException {
		String tag = vn.toString(vn.getCurrentIndex());

		elementList.add(tag);

	}
	
	/**
	 * Removes all occurrences of an element using its tag name.
	 * <p>
	 * This method should not be used for deep nodes with high number of occurrence.
	 * @param tagname
	 * @param vn
	 * @return XML in Byte[] form
	 * @throws ModifyException
	 * @throws NavException
	 * @throws IndexWriteException
	 * @throws IOException
	 * @throws ParseException
	 * @throws TranscodeException
	 */
	public static byte[] removeElementsWithTagName(String tagname, VTDNav vn)
			throws ModifyException, NavException, IndexWriteException, IOException, ParseException, TranscodeException {

		XMLModifier xm = new XMLModifier(vn);

		int count = VTDXmlUtils.getElementFrequencyOf(tagname, vn);
		AutoPilot ap = new AutoPilot();
		ap.bind(vn);
		for (int i = 0; i < count; i++) {

			ap.selectElement(tagname);
			ap.iterate();
			xm.remove();
			VTDNav newNav = xm.outputAndReparse();
			ap.bind(newNav);
			xm = new XMLModifier(newNav);
		}

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		xm.output(bout);
		byte[] xmlByteArray = bout.toByteArray();
		return xmlByteArray;
	}

	/**
	 * Removes all occurrences of an element using its tag name.
	 * <p>
	 * This method should not be used for deep nodes with high number of occurrence.
	 * @param tagname
	 * @param vn
	 * @return XML in Byte[] form
	 * @throws ModifyException
	 * @throws NavException
	 * @throws IndexWriteException
	 * @throws IOException
	 * @throws ParseException
	 * @throws TranscodeException
	 */
	public static String removeElementsWithTagNameStr(String tagname, VTDNav vn)
			throws ModifyException, NavException, IndexWriteException, IOException, ParseException, TranscodeException {

		XMLModifier xm = new XMLModifier(vn);

		int count = VTDXmlUtils.getElementFrequencyOf(tagname, vn);
		AutoPilot ap = new AutoPilot();
		ap.bind(vn);
		for (int i = 0; i < count; i++) {

			ap.selectElement(tagname);
			ap.iterate();
			xm.remove();
			VTDNav newNav = xm.outputAndReparse();
			ap.bind(newNav);
			xm = new XMLModifier(newNav);
		}

		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		xm.output(bout);
		bout.toString("UTF-8");
		
		return bout.toString("UTF-8");
	}
	/**
	 * Takes String XML as input and Returns Navigator
	 * @param strxml
	 * @return Navigator
	 * @throws EncodingException
	 * @throws EOFException
	 * @throws EntityException
	 * @throws ParseException
	 */
	public static synchronized VTDNav StringtoNavigator(String strxml)
			throws EncodingException, EOFException, EntityException, ParseException {
		VTDGen vtdgen = new VTDGen();
		vtdgen.setDoc(strxml.getBytes());
		vtdgen.parse(false);
		VTDNav vtdNav = vtdgen.getNav();
		return vtdNav;
	}
	/**
	 * Deepak0511
	 * <P>
	 * This method randomly selects nodes over the passed context.
	 * @param elementName
	 * @param vn
	 * @return byte[]
	 * @throws NavException
	 * @throws IndexWriteException
	 * @throws IOException
	 */
	public static List<byte[]> selectElements(String elementName, VTDNav vn)
			throws NavException, IndexWriteException, IOException {

		List<byte[]> elements = new ArrayList<byte[]>();

		for (boolean el = (vn != null); el == true; el = vn.toElement(VTDNav.NEXT_SIBLING)) {

			collectOneOpportunistic(elements, elementName, vn);
			breadthFirstSearchRecursive(elements, elementName, vn);

		}
		return elements;

	}

	private static void breadthFirstSearchRecursive(List<byte[]> elements, String token, VTDNav vn)
			throws NavException, IOException {

		vn.push();

		for (boolean el = vn.toElement(VTDNav.FIRST_CHILD); el == true; el = vn.toElement(VTDNav.NEXT_SIBLING)) {

			collectOneOpportunistic(elements, token, vn);
			breadthFirstSearchRecursive(elements, token, vn);
		}

		vn.pop();

	}

	private static void collectOneOpportunistic(List<byte[]> elements, String token, VTDNav vn)
			throws NavException, IOException {

		if (vn.matchElement(token)) {

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			vn.dumpFragment(bout);

			elements.add(bout.toByteArray());

		}

	}
	
	/**
	 * returns value of an xml tag
	 * @param nodeFragment
	 * @return String inside tags
	 */
	public static String getNodeText(byte[] nodeFragment){
		String xml = new String(nodeFragment);			
		return xml.substring(xml.indexOf(">")+1, xml.indexOf("<", 3));
	}
	

}
