//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.28 at 10:52:32 PM ALMT 
//


package org.processbase.ui.core.bonita.forms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Validators complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Validators">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="validator" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="classname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="regex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="style" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="position" type="{}ValidatorPosition" minOccurs="0"/>
 *                 &lt;/all>
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Validators", propOrder = {
    "validators"
})
public class Validators {

    @XmlElement(name = "validator")
    protected List<Validators.Validator> validators;

    /**
     * Gets the value of the validators property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the validators property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValidators().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Validators.Validator }
     * 
     * 
     */
    public List<Validators.Validator> getValidators() {
        if (validators == null) {
            validators = new ArrayList<Validators.Validator>();
        }
        return this.validators;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="classname" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="regex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="style" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="position" type="{}ValidatorPosition" minOccurs="0"/>
     *       &lt;/all>
     *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Validator {

        @XmlElement(required = true)
        protected String classname;
        @XmlElement(required = true)
        protected String label;
        protected String regex;
        protected String style;
        protected ValidatorPosition position;
        @XmlAttribute(name = "id", required = true)
        protected String id;

        /**
         * Gets the value of the classname property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getClassname() {
            return classname;
        }

        /**
         * Sets the value of the classname property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setClassname(String value) {
            this.classname = value;
        }

        /**
         * Gets the value of the label property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLabel() {
            return label;
        }

        /**
         * Sets the value of the label property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLabel(String value) {
            this.label = value;
        }

        /**
         * Gets the value of the regex property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRegex() {
            return regex;
        }

        /**
         * Sets the value of the regex property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRegex(String value) {
            this.regex = value;
        }

        /**
         * Gets the value of the style property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStyle() {
            return style;
        }

        /**
         * Sets the value of the style property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStyle(String value) {
            this.style = value;
        }

        /**
         * Gets the value of the position property.
         * 
         * @return
         *     possible object is
         *     {@link ValidatorPosition }
         *     
         */
        public ValidatorPosition getPosition() {
            return position;
        }

        /**
         * Sets the value of the position property.
         * 
         * @param value
         *     allowed object is
         *     {@link ValidatorPosition }
         *     
         */
        public void setPosition(ValidatorPosition value) {
            this.position = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

    }

}