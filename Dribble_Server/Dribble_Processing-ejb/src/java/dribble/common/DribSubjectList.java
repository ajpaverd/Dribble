/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dribble.common;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dribble
 */

@XmlRootElement(name="dribble.common.DribSubjectList")
public class DribSubjectList {

    @XmlElementWrapper(name = "list")
    @XmlElement(name = "dribble.common.DribSubject")
    public ArrayList<DribSubject> list;

}
