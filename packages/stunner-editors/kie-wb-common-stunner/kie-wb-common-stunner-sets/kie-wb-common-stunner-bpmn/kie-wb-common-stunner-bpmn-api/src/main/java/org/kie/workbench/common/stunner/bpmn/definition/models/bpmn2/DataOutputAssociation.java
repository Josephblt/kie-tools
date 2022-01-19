/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.core.util.HashUtil;

@XmlRootElement(name = "dataOutputAssociation", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class DataOutputAssociation {

    @XmlElement
    private TargetRef targetRef;

    @XmlElement
    private Assignment assignment;

    @XmlElement
    private SourceRef sourceRef;

    public DataOutputAssociation() {
        this(new SourceRef(), new Assignment());
    }

    public DataOutputAssociation(String source, String target) {
        this(new SourceRef(source), new TargetRef(target));
    }

    public DataOutputAssociation(SourceRef source, Assignment assignment) {
        this.sourceRef = source;
        this.assignment = assignment;
    }

    public DataOutputAssociation(SourceRef source, TargetRef target) {
        this.sourceRef = source;
        this.targetRef = target;
    }

    public TargetRef getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(TargetRef targetRef) {
        this.targetRef = targetRef;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public SourceRef getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(SourceRef sourceRef) {
        this.sourceRef = sourceRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataOutputAssociation)) {
            return false;
        }
        DataOutputAssociation that = (DataOutputAssociation) o;
        return Objects.equals(getTargetRef(), that.getTargetRef())
                && Objects.equals(getSourceRef(), that.getSourceRef())
                && Objects.equals(getAssignment(), that.getAssignment());
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(targetRef),
                                         Objects.hashCode(sourceRef),
                                         Objects.hashCode(assignment));
    }
}
