
package com.dribble.common;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dribble
 */

@XmlRootElement(name="com.dribble.common.DribList")
public class DribList {

    @XmlElementWrapper(name = "list")
    @XmlElement(name = "com.dribble.common.Drib")
    public ArrayList<Drib> list;

}
