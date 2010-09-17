// Authors: Dribble

package dribble.common;
import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class DribSubjectList {
	@ElementList
	public ArrayList<DribSubject> list;
}
