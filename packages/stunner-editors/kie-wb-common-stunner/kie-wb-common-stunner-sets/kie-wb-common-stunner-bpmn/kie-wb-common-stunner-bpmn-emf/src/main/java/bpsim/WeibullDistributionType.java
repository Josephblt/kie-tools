/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package bpsim;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Weibull Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link bpsim.WeibullDistributionType#getScale <em>Scale</em>}</li>
 *   <li>{@link bpsim.WeibullDistributionType#getShape <em>Shape</em>}</li>
 * </ul>
 *
 * @see bpsim.BpsimPackage#getWeibullDistributionType()
 * @model extendedMetaData="name='WeibullDistribution_._type' kind='empty'"
 * @generated
 */
public interface WeibullDistributionType extends DistributionParameter {
	/**
	 * Returns the value of the '<em><b>Scale</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Scale</em>' attribute.
	 * @see #isSetScale()
	 * @see #unsetScale()
	 * @see #setScale(double)
	 * @see bpsim.BpsimPackage#getWeibullDistributionType_Scale()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='scale'"
	 * @generated
	 */
	double getScale();

	/**
	 * Sets the value of the '{@link bpsim.WeibullDistributionType#getScale <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Scale</em>' attribute.
	 * @see #isSetScale()
	 * @see #unsetScale()
	 * @see #getScale()
	 * @generated
	 */
	void setScale(double value);

	/**
	 * Unsets the value of the '{@link bpsim.WeibullDistributionType#getScale <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetScale()
	 * @see #getScale()
	 * @see #setScale(double)
	 * @generated
	 */
	void unsetScale();

	/**
	 * Returns whether the value of the '{@link bpsim.WeibullDistributionType#getScale <em>Scale</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Scale</em>' attribute is set.
	 * @see #unsetScale()
	 * @see #getScale()
	 * @see #setScale(double)
	 * @generated
	 */
	boolean isSetScale();

	/**
	 * Returns the value of the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shape</em>' attribute.
	 * @see #isSetShape()
	 * @see #unsetShape()
	 * @see #setShape(double)
	 * @see bpsim.BpsimPackage#getWeibullDistributionType_Shape()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='shape'"
	 * @generated
	 */
	double getShape();

	/**
	 * Sets the value of the '{@link bpsim.WeibullDistributionType#getShape <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape</em>' attribute.
	 * @see #isSetShape()
	 * @see #unsetShape()
	 * @see #getShape()
	 * @generated
	 */
	void setShape(double value);

	/**
	 * Unsets the value of the '{@link bpsim.WeibullDistributionType#getShape <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetShape()
	 * @see #getShape()
	 * @see #setShape(double)
	 * @generated
	 */
	void unsetShape();

	/**
	 * Returns whether the value of the '{@link bpsim.WeibullDistributionType#getShape <em>Shape</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Shape</em>' attribute is set.
	 * @see #unsetShape()
	 * @see #getShape()
	 * @see #setShape(double)
	 * @generated
	 */
	boolean isSetShape();

} // WeibullDistributionType
